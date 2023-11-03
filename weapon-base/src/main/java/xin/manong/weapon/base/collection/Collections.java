package xin.manong.weapon.base.collection;

import java.util.Comparator;
import java.util.List;

/**
 * 集合工具
 *
 * @author frankcl
 * @date 2023-04-25 10:58:36
 */
public class Collections {

    /**
     * 二分查找
     * comparator为空：保证元素类型实现Comparable接口
     *
     * @param elements 有序列表
     * @param e 查找元素
     * @param comparator 比较器
     * @return 成功返回下标，否则返回-1
     * @param <E>
     */
    public static <E> int binarySearch(List<E> elements, E e, Comparator<E> comparator) {
        if (e == null) throw new NullPointerException();
        if (elements == null || elements.isEmpty()) return -1;
        int i = 0, j = elements.size() - 1, m = (i + j) / 2;
        while (true) {
            E element = elements.get(m);
            int c = compare(e, element, comparator);
            if (c == 0) {
                for (; m > 0; m--) {
                    c = compare(elements.get(m), elements.get(m - 1), comparator);
                    if (c != 0) break;
                }
                return m;
            }
            else if (c < 0) j = m - 1;
            else i = m + 1;
            if (i > j) break;
            m = (i + j) / 2;
        }
        return -1;
    }

    /**
     * 堆排序
     * comparator为空：保证元素类型实现Comparable接口
     *
     * @param elements 排序列表
     * @param comparator 比较器
     * @param <E>
     */
    public static <E> void sortHeap(List<E> elements, Comparator<E> comparator) {
        Heap<E> heap = new Heap<>(elements, comparator);
        elements.clear();
        while (!heap.isEmpty()) elements.add(heap.poll());
    }

    /**
     * 快速排序
     * comparator为空：保证元素类型实现Comparable接口
     *
     * @param elements 排序列表
     * @param comparator 比较器
     * @param <E>
     */
    public static <E> void sortQuick(List<E> elements, Comparator<E> comparator) {
        if (elements == null || elements.isEmpty()) return;
        sortQuick(elements, comparator, 0, elements.size() - 1);
    }

    /**
     * 快速排序
     *
     * @param elements 排序列表
     * @param comparator 比较器
     * @param from 起始元素位置
     * @param to 结束元素位置
     * @param <E>
     */
    private static <E> void sortQuick(List<E> elements, Comparator<E> comparator,
                                      int from, int to) {
        if (from >= to) return;
        int i = from;
        int j = to;
        int pivot = from;
        boolean forward = true;
        E pivotElement = elements.get(pivot);
        while (i < j) {
            int k = forward ? j : i;
            E element = elements.get(k);
            int c = compare(element, pivotElement, comparator);
            if (forward && c >= 0) {
                j--;
                continue;
            } else if (!forward && c <= 0) {
                i++;
                continue;
            }
            elements.set(pivot, element);
            pivot = forward ? j-- : i++;
            forward = !forward;
        }
        elements.set(pivot, pivotElement);
        sortQuick(elements, comparator, from, pivot - 1);
        sortQuick(elements, comparator, pivot + 1, to);
    }

    /**
     * 比较数据
     * 1. comparator为空，元素需要保证实现Comparable接口，使用Comparable接口compareTo方法进行比较
     * 2. comparator不为空，使用Comparator方法compare进行比较
     *
     * @param e1 数据
     * @param e2 数据
     * @param comparator 比较器
     * @return e1小于e2返回负数，e1大于e2返回正数，相等返回0
     * @param <E>
     */
    private static <E> int compare(E e1, E e2, Comparator<? super E> comparator) {
        return comparator == null ? ((Comparable<? super E>) e1).compareTo(e2) : comparator.compare(e1, e2);
    }
}
