package xin.manong.weapon.base.sort;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.SerializerFactory;
import com.esotericsoftware.kryo.io.Output;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 外部排序
 *
 * @author frankcl
 * @date 2023-04-25 16:53:37
 */
public class ExternalSorter<T> {

    private static enum State {
        PREPARE, SORT, CLOSED
    }

    private static final Logger logger = LoggerFactory.getLogger(ExternalSorter.class);

    private static final int DEFAULT_MAX_OPEN_FILE_NUM = 100;
    private static final int DEFAULT_MAX_CACHE_RECORD_NUM = 10000;
    private static final String DUMP_FILE_PREFIX = "TEMP_SORT_FILE_";
    private static final String DUMP_FILE_SUFFIX = ".dump";

    private int dumpFileIndex;
    private int maxOpenFileNum;
    private int maxCacheRecordNum;
    private State state;
    private String tempDirectory;
    private Class<T> recordClass;
    private Comparator<T> comparator;
    private RecordReaderComparator<T> readerComparator;
    private List<T> memoryCachedRecords;
    private LinkedList<String> dumpFiles;
    private PriorityQueue<RecordReader<T>> heap;
    private Kryo kryo;

    public ExternalSorter(Class<T> recordClass, Comparator<T> comparator) {
        maxOpenFileNum = DEFAULT_MAX_OPEN_FILE_NUM;
        maxCacheRecordNum = DEFAULT_MAX_CACHE_RECORD_NUM;
        this.kryo = new Kryo();
        this.kryo.setReferences(true);
        this.kryo.setRegistrationRequired(false);
        this.kryo.setDefaultSerializer(new SerializerFactory.CompatibleFieldSerializerFactory());
        this.recordClass = recordClass;
        this.comparator = comparator;
        this.readerComparator = new RecordReaderComparator<>(this.comparator);
        reset();
    }

    public ExternalSorter(Class<T> recordClass, Comparator<T> comparator, String tempDirectory) {
        this(recordClass, comparator);
        setTempDirectory(tempDirectory);
    }

    /**
     * 增加排序数据
     *
     * @param record 数据
     * @throws IOException
     */
    public void addRecord(T record) throws IOException {
        if (record == null) return;
        if (state != State.PREPARE) {
            logger.error("unsupported operation[ADD] for state[{}]", state.name());
            return;
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
     * @throws IOException
     */
    public T getRecord() throws IOException {
        if (state == State.PREPARE) {
            mergeDumpFiles();
            heap = new PriorityQueue<>(dumpFiles.size() + 1, readerComparator);
            if (!memoryCachedRecords.isEmpty()) {
                MemoryReader memoryReader = new MemoryReader(memoryCachedRecords, comparator);
                if (!memoryReader.open()) throw new RuntimeException("open memory reader failed");
                if (memoryReader.read() != null) heap.add(memoryReader);
                else memoryReader.close();
            }
            buildHeap(dumpFiles, heap);
            state = State.SORT;
        }
        if (state != State.SORT) {
            logger.error("unsupported operation[GET] for state[{}]", state.name());
            return null;
        }
        while (!heap.isEmpty()) {
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
    public void close() {
        sweepDumpFiles();
        new File(tempDirectory).delete();
        dumpFileIndex = 0;
        dumpFiles.clear();
        heap.clear();
        state = State.CLOSED;
        logger.info("close external sorter");
    }

    /**
     * 生成数据文件
     *
     * @param records 数据列表
     * @throws IOException
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
     * @throws IOException
     */
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
     * @param dumpFiles
     * @param priorityQueue
     */
    private void buildHeap(List<String> dumpFiles, PriorityQueue<RecordReader<T>> priorityQueue) {
        for (String dumpFile : dumpFiles) {
            DumpReader dumpReader = new DumpReader(dumpFile, recordClass, kryo);
            if (!dumpReader.open()) throw new RuntimeException(String.format("open dump file[%s] failed", dumpFile));
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
        for (File dumpFile : dumpFiles) {
            String fileName = dumpFile.getName();
            if (dumpFile.isDirectory() || !fileName.startsWith(DUMP_FILE_PREFIX) ||
                    !fileName.endsWith(DUMP_FILE_SUFFIX)) continue;
            if (dumpFile.delete()) logger.info("sweep dump file[{}]", fileName);
        }
    }

    /**
     * 设置临时文件目录
     *
     * @param tempDirectory 临时文件目录
     */
    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory.endsWith("/") ? tempDirectory : tempDirectory + "/";
        File directory = new File(tempDirectory);
        if (!directory.exists() || !directory.isDirectory()) directory.mkdirs();
        else sweepDumpFiles();
    }

    /**
     * 设置最大打开合并文件数量
     *
     * @param maxOpenFileNum 最大打开合并文件数量
     */
    public void setMaxOpenFileNum(int maxOpenFileNum) {
        this.maxOpenFileNum = maxOpenFileNum;
    }

    /**
     * 设置内存最大缓存记录数量
     *
     * @param maxCacheRecordNum 内存最大缓存记录数量
     */
    public void setMaxCacheRecordNum(int maxCacheRecordNum) {
        this.maxCacheRecordNum = maxCacheRecordNum;
    }
}
