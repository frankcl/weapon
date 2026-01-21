package xin.manong.weapon.base.collection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.SerializerFactory;
import com.esotericsoftware.kryo.io.Output;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * LSM Tree实现：基于本地磁盘外部排序
 *
 * @author frankcl
 * @date 2023-04-25 16:53:37
 */
public class LSMTree<T> {

    /**
     * LSM Tree状态
     */
    private enum State {
        PREPARE, SORT, CLOSED
    }

    private static final Logger logger = LoggerFactory.getLogger(LSMTree.class);

    private static final int DEFAULT_MAX_OPEN_FILE_NUM = 100;
    private static final int DEFAULT_MAX_CACHE_RECORD_NUM = 10000;
    private static final String DEFAULT_TEMP_DIRECTORY = "./temp/";
    private static final String DUMP_FILE_PREFIX = "TEMP_SORT_FILE_";
    private static final String DUMP_FILE_SUFFIX = ".dump";

    private int dumpFileIndex;
    /* 同时打开文件数量 */
    @Setter
    private int maxOpenFileNum;
    /* 内存cache数据数量 */
    @Setter
    private int maxCacheRecordNum;
    /* LSM Tree当前状态 */
    private State state;
    /* 临时文件目录 */
    private String tempDirectory;
    /* 数据类型 */
    private final Class<T> recordClass;
    /* 数据比较器 */
    private final Comparator<? super T> comparator;
    /* 数据读取器比较器 */
    private final RecordReaderComparator<T> readerComparator;
    /* 内存cache数据 */
    private List<T> memoryCachedRecords;
    /* dump文件列表 */
    private LinkedList<String> dumpFiles;
    /* 数据读取器堆 用于归并文件数据 */
    private PriorityQueue<RecordReader<T>> heap;
    /* kryo序列化 */
    private final Kryo kryo;

    private LSMTree(Class<T> recordClass, Comparator<? super T> comparator) {
        this(recordClass, comparator, DEFAULT_TEMP_DIRECTORY);
    }

    public LSMTree(Class<T> recordClass, Comparator<? super T> comparator, String tempDirectory) {
        maxOpenFileNum = DEFAULT_MAX_OPEN_FILE_NUM;
        maxCacheRecordNum = DEFAULT_MAX_CACHE_RECORD_NUM;
        this.kryo = new Kryo();
        this.kryo.setReferences(true);
        this.kryo.setRegistrationRequired(false);
        this.kryo.setDefaultSerializer(new SerializerFactory.CompatibleFieldSerializerFactory());
        this.recordClass = recordClass;
        this.comparator = comparator;
        this.readerComparator = new RecordReaderComparator<>(this.comparator);
        setTempDirectory(tempDirectory);
        reset();
    }

    /**
     * 增加排序数据
     *
     * @param record 数据
     * @throws IOException I/O异常
     */
    public void addRecord(T record) throws IOException {
        if (record == null) throw new NullPointerException();
        if (!recordClass.isAssignableFrom(record.getClass())) {
            logger.error("Record class:{} is not compatible for {}",
                    record.getClass().getName(), recordClass.getName());
            throw new IllegalArgumentException("not compatible class");
        }
        if (state != State.PREPARE) {
            logger.error("Unsupported operation[ADD] for state:{}", state.name());
            throw new IllegalStateException(String.format("Inappropriate state:%s for adding", state.name()));
        }
        memoryCachedRecords.add(record);
        if (memoryCachedRecords.size() < maxCacheRecordNum) return;
        memoryCachedRecords.sort(comparator);
        dumpRecords(memoryCachedRecords);
        memoryCachedRecords = new ArrayList<>();
    }

    /**
     * 获取排序数据
     *
     * @return 有序数据，如果没有数据返回null
     * @throws IOException I/O异常
     */
    public T getRecord() throws IOException {
        if (state == State.PREPARE) {
            mergeDumpFiles();
            heap = new PriorityQueue<>(dumpFiles.size() + 1, readerComparator);
            if (!memoryCachedRecords.isEmpty()) {
                MemoryReader<T> memoryReader = new MemoryReader<>(memoryCachedRecords, comparator);
                if (!memoryReader.open()) throw new RuntimeException("Open memory reader failed");
                if (memoryReader.read() != null) heap.add(memoryReader);
                else memoryReader.close();
            }
            buildHeap(dumpFiles, heap);
            state = State.SORT;
        }
        if (state != State.SORT) {
            logger.error("Unsupported operation[GET] for state:{}", state.name());
            throw new IllegalStateException(String.format("Inappropriate state:%s for getting", state.name()));
        }
        if (!heap.isEmpty()) {
            RecordReader<T> recordReader = heap.poll();
            T record = recordReader.peak();
            if (recordReader.read() != null) heap.add(recordReader);
            else recordReader.close();
            return record;
        }
        return null;
    }

    /**
     * 重置排序
     */
    public void reset() {
        sweepDumpFiles();
        dumpFileIndex = 0;
        dumpFiles = new LinkedList<>();
        memoryCachedRecords = new ArrayList<>();
        heap = new PriorityQueue<>();
        state = State.PREPARE;
    }

    /**
     * 关闭清理资源
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void close() {
        sweepDumpFiles();
        new File(tempDirectory).delete();
        dumpFileIndex = 0;
        dumpFiles.clear();
        heap.clear();
        state = State.CLOSED;
        logger.info("Close external sorter");
    }

    /**
     * 生成数据文件
     *
     * @param records 数据列表
     * @throws IOException I/O异常
     */
    private void dumpRecords(List<T> records) throws IOException {
        String dumpFile = String.format("%s%s%d%s", tempDirectory, DUMP_FILE_PREFIX, dumpFileIndex, DUMP_FILE_SUFFIX);
        Output output = new Output(new FileOutputStream(dumpFile));
        for (T record : records) kryo.writeObject(output, record);
        output.close();
        dumpFiles.add(dumpFile);
        dumpFileIndex++;
    }

    /**
     * 合并dump文件
     *
     * @throws IOException I/O异常
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void mergeDumpFiles() throws IOException {
        while (dumpFiles.size() > maxOpenFileNum) {
            List<String> batchDumpFiles = new ArrayList<>();
            for (int i = 0; i < maxOpenFileNum; i++) batchDumpFiles.add(dumpFiles.removeFirst());
            PriorityQueue<RecordReader<T>> priorityQueue = new PriorityQueue<>(maxOpenFileNum, readerComparator);
            buildHeap(batchDumpFiles, priorityQueue);
            String dumpFile = String.format("%s%s%d%s", tempDirectory, DUMP_FILE_PREFIX, dumpFileIndex, DUMP_FILE_SUFFIX);
            Output output = new Output(new FileOutputStream(dumpFile));
            while (!priorityQueue.isEmpty()) {
                RecordReader<T> recordReader = priorityQueue.poll();
                T record = recordReader.peak();
                kryo.writeObject(output, record);
                record = recordReader.read();
                if (record != null) priorityQueue.add(recordReader);
                else recordReader.close();
            }
            output.close();
            for (String batchDumpFile : batchDumpFiles) new File(batchDumpFile).delete();
            dumpFiles.add(dumpFile);
            dumpFileIndex++;
        }
    }

    /**
     * 构建排序堆
     *
     * @param dumpFiles dump文件
     * @param priorityQueue 队列
     */
    private void buildHeap(List<String> dumpFiles, PriorityQueue<RecordReader<T>> priorityQueue) {
        for (String dumpFile : dumpFiles) {
            DumpReader<T> dumpReader = new DumpReader<>(dumpFile, recordClass, kryo);
            if (!dumpReader.open()) throw new RuntimeException(String.format("Open dump file:%s failed", dumpFile));
            if (dumpReader.read() != null) priorityQueue.add(dumpReader);
            else dumpReader.close();
        }
    }

    /**
     * 清理dump文件
     */
    private void sweepDumpFiles() {
        if (StringUtils.isEmpty(tempDirectory)) return;
        File dumpDirectory = new File(tempDirectory);
        if (!dumpDirectory.exists() || !dumpDirectory.isDirectory()) return;
        File[] dumpFiles = dumpDirectory.listFiles();
        if (dumpFiles == null) return;
        for (File dumpFile : dumpFiles) {
            String fileName = dumpFile.getName();
            if (dumpFile.isDirectory() || !fileName.startsWith(DUMP_FILE_PREFIX) ||
                    !fileName.endsWith(DUMP_FILE_SUFFIX)) continue;
            if (dumpFile.delete()) logger.info("Sweep dump file:{}", fileName);
        }
    }

    /**
     * 设置临时文件目录
     *
     * @param tempDirectory 临时文件目录
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory.endsWith("/") ? tempDirectory : tempDirectory + "/";
        File directory = new File(tempDirectory);
        if (!directory.exists() || !directory.isDirectory()) directory.mkdirs();
    }
}
