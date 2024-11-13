package xin.manong.weapon.base.collection;

import java.util.Comparator;

/**
 * 数据读取器比较器
 *
 * @author frankcl
 * @date 2023-04-27 14:53:43
 */
class RecordReaderComparator<T> implements Comparator<RecordReader<T>> {

    private final Comparator<? super T> comparator;

    public RecordReaderComparator(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(RecordReader<T> reader1, RecordReader<T> reader2) {
        if (reader1.peak() == reader2.peak()) return 0;
        if (reader1.peak() == null) return 1;
        if (reader2.peak() == null) return -1;
        return compare(reader1.peak(), reader2.peak(), comparator);
    }

    /**
     * 比较元素
     *
     * @param e1 元素
     * @param e2 元素
     * @param comparator 比较器
     * @return 比较结果
     */
    @SuppressWarnings("unchecked")
    private int compare(T e1, T e2, Comparator<? super T> comparator) {
        return comparator == null ? ((Comparable<? super T>) e1).compareTo(e2) : comparator.compare(e1, e2);
    }
}
