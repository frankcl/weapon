package xin.manong.weapon.base.collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

/**
 * 内存数据读取器
 *
 * @author frankcl
 * @date 2023-04-27 14:46:09
 */
class MemoryReader<T> implements RecordReader<T> {

    private static final Logger logger = LoggerFactory.getLogger(MemoryReader.class);

    private int cursor;
    private T record;
    private final Comparator<? super T> comparator;
    private final List<T> memoryCachedRecords;

    public MemoryReader(List<T> memoryCachedRecords, Comparator<? super T> comparator) {
        if (memoryCachedRecords == null) throw new NullPointerException();
        this.comparator = comparator;
        this.memoryCachedRecords = memoryCachedRecords;
    }

    @Override
    public boolean open() {
        cursor = 0;
        memoryCachedRecords.sort(comparator);
        logger.info("Open memory reader success");
        return true;
    }

    @Override
    public void close() {
        logger.info("Close memory reader success");
    }

    @Override
    public T read() {
        record = cursor < memoryCachedRecords.size() ? memoryCachedRecords.get(cursor++) : null;
        return record;
    }

    @Override
    public T peak() {
        return record;
    }
}
