package xin.manong.weapon.base.sort;

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
public class MemoryReader<T> implements RecordReader<T> {

    private static final Logger logger = LoggerFactory.getLogger(MemoryReader.class);

    private int cursor;
    private T record;
    private Comparator<T> comparator;
    private List<T> memoryCachedRecords;

    public MemoryReader(List<T> memoryCachedRecords, Comparator<T> comparator) {
        this.comparator = comparator;
        this.memoryCachedRecords = memoryCachedRecords;
    }

    @Override
    public boolean open() {
        cursor = 0;
        memoryCachedRecords.sort(comparator);
        logger.info("open memory reader success");
        return true;
    }

    @Override
    public void close() {
        logger.info("close memory reader success");
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
