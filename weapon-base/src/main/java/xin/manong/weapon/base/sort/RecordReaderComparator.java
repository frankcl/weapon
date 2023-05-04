package xin.manong.weapon.base.sort;

import java.util.Comparator;

/**
 * 数据读取器比较器
 *
 * @author frankcl
 * @date 2023-04-27 14:53:43
 */
class RecordReaderComparator<T> implements Comparator<RecordReader<T>> {

    private Comparator<T> comparator;

    public RecordReaderComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(RecordReader<T> reader1, RecordReader<T> reader2) {
        if (reader1.peak() == reader2.peak()) return 0;
        if (reader1.peak() == null) return 1;
        if (reader2.peak() == null) return -1;
        return comparator.compare(reader1.peak(), reader2.peak());
    }
}
