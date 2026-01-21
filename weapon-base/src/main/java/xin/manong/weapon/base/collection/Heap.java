package xin.manong.weapon.base.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * 堆实现
 * 1. 内部以数组方式实现堆，通过comparator决定大顶堆或小顶堆
 * 2. 支持数据迭代，迭代器保证数据排序
 *
 * @author frankcl
 * @date 2023-11-01 17:29:56
 */
@SuppressWarnings("unchecked")
public class Heap<E> implements Iterable<E> {

    private static final int DEFAULT_INIT_CAPACITY = 13;

    /* 堆元素数量 */
    private int size;
    /* 元素数组 */
    private Object[] elements;
    /* 比较器 */
    private final Comparator<? super E> comparator;

    public Heap() {
        this(DEFAULT_INIT_CAPACITY, null);
    }

    public Heap(Comparator<? super E> comparator) {
        this(DEFAULT_INIT_CAPACITY, comparator);
    }

    public Heap(int initCapacity, Comparator<? super E> comparator) {
        if (initCapacity < 1) throw new IllegalArgumentException("Init capacity is invalid");
        this.elements = new Object[initCapacity];
        this.comparator = comparator;
        this.size = 0;
    }

    public Heap(Collection<E> collection) {
        this(collection, null);
    }

    public Heap(Collection<E> collection, Comparator<? super E> comparator) {
        if (collection == null) throw new NullPointerException();
        this.comparator = comparator;
        Object[] objects = collection.toArray();
        this.elements = Arrays.copyOf(objects, objects.length);
        this.size = this.elements.length;
        if (size < 2) return;
        for (int i = (size >>> 1) - 1; i >= 0; i--) moveDownElement(elements, size, (E) elements[i], i, comparator);
    }

    /**
     * 添加元素
     *
     * @param e 元素，元素为空抛出异常IllegalArgumentException
     */
    public void add(E e) {
        if (e == null) throw new NullPointerException();
        expandCapacity(size + 1);
        int current = size++;
        moveUpElement(elements, current, e, comparator);
    }

    /**
     * 获取堆顶元素
     * 完成操作后不影响堆元素
     *
     * @return 堆顶元素，如果堆为空返回null
     */
    public E peek() {
        if (size == 0) return null;
        return (E) elements[0];
    }

    /**
     * 弹出堆顶元素
     * 完成操作后移除堆顶元素
     *
     * @return 堆顶元素，如果堆为空返回null
     */
    public E poll() {
        if (size == 0) return null;
        return removeAt(0);
    }

    /**
     * 移除元素
     *
     * @param e 元素，元素为空抛出异常IllegalArgumentException
     * @return 移除成功返回true，否则返回false
     */
    public boolean remove(E e) {
        if (e == null) throw new NullPointerException();
        if (size == 0) return false;
        int pos = indexOf(e);
        if (pos != -1) {
            removeAt(pos);
            return true;
        }
        return false;
    }

    /**
     * 元素数量
     *
     * @return 当前堆元素数量
     */
    public int size() {
        return size;
    }

    /**
     * 堆是否为空
     *
     * @return 为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 数据迭代器
     *
     * @return 数据迭代器
     */
    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (!builder.isEmpty()) builder.append(",");
            builder.append(elements[i].toString());
        }
        builder.insert(0, "[").append("]");
        return builder.toString();
    }

    /**
     * 移除指定下标元素
     *
     * @param i 元素下标
     * @return 移除元素
     */
    private E removeAt(int i) {
        if (i < 0 || i > size - 1) {
            throw new ArrayIndexOutOfBoundsException(String.format("Index:%d out of range[0, %d)", i, size));
        }
        E removedElement = (E) elements[i];
        E e = (E) elements[--size];
        elements[size] = null;
        if (size == 0) return removedElement;
        moveDownElement(elements, size, e, i, comparator);
        if (elements[i] == e) {
            moveUpElement(elements, i, e, comparator);
        }
        return removedElement;
    }

    /**
     * 返回首个元素下标
     *
     * @param e 搜索元素
     * @return 发现返回元素下标，否则返回-1
     */
    private int indexOf(E e) {
        if (size == 0) return -1;
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(e)) return i;
        }
        return -1;
    }

    /**
     * 扩容
     *
     * @param capacity 扩容后容量
     */
    private void expandCapacity(int capacity) {
        int currentCapacity = elements.length;
        if (capacity < 0) throw new OutOfMemoryError();
        if (capacity <= currentCapacity) return;
        int newCapacity = currentCapacity + (currentCapacity < 64 ? 2 : currentCapacity >>> 1);
        if (newCapacity < 0) newCapacity = Integer.MAX_VALUE;
        elements = Arrays.copyOf(elements, newCapacity);
    }

    /**
     * 上移元素
     *
     * @param elements 数据数组
     * @param current 上移元素位置
     * @param e 上移元素
     * @param comparator 比较器
     * @param <E> 数据类型
     */
    private static <E> void moveUpElement(Object[] elements, int current, E e,
                                          Comparator<? super E> comparator) {
        while (current > 0) {
            int parent = (current - 1) >>> 1;
            int c = compare((E) elements[parent], e, comparator);
            if (c <= 0) break;
            elements[current] = elements[parent];
            current = parent;
        }
        elements[current] = e;
    }

    /**
     * 下移元素
     *
     * @param elements 数据数组
     * @param size 数据数量
     * @param e 下移元素
     * @param current 下移元素位置
     * @param comparator 比较器
     * @param <E> 数据类型
     */
    private static <E> void moveDownElement(Object[] elements, int size, E e, int current,
                                            Comparator<? super E> comparator) {
        int half = (size - 1) >>> 1;
        while (current <= half) {
            int child = (current << 1) + 1;
            if (child >= size) break;
            child = child + 1 < size ? (compare((E) elements[child], (E) elements[child+1], comparator) > 0 ?
                    child + 1 : child) : child;
            int c = compare(e, (E) elements[child], comparator);
            if (c <= 0) break;
            elements[current] = elements[child];
            current = child;
        }
        elements[current] = e;
    }

    /**
     * 比较元素
     * 1. 如果comparator为空，要求类型E实现Comparable接口，使用Comparable方法compareTo进行比较
     * 2. 如果comparator不为空，使用comparator进行比较
     *
     * @param e1 比较元素
     * @param e2 比较元素
     * @param comparator 比较器
     * @return e1小于e2返回负数，e1大于e2返回正数，相等返回0
     * @param <E> 数据类型
     */
    private static <E> int compare(E e1, E e2, Comparator<? super E> comparator) {
        return comparator == null ? ((Comparable<? super E>) e1).compareTo(e2) :
                comparator.compare(e1, e2);
    }

    /**
     * 堆数据迭代器
     * 迭代结果保证顺序
     */
    final class Itr implements Iterator<E> {

        private int queueSize;
        private E lastElement;
        private final Object[] queue;

        public Itr() {
            lastElement = null;
            queueSize = size;
            queue = Arrays.copyOf(elements, queueSize);
        }

        @Override
        public boolean hasNext() {
            return queueSize > 0;
        }

        @Override
        public E next() {
            if (queueSize <= 0) return null;
            lastElement = (E) queue[0];
            E movedElement = (E) queue[--queueSize];
            queue[queueSize] = null;
            if (queueSize == 0) return lastElement;
            moveDownElement(queue, queueSize, movedElement, 0, comparator);
            return lastElement;
        }

        @Override
        public void remove() {
            if (lastElement == null) return;
            for (int i = 0; i < size; i++) {
                if (elements[i] != lastElement) continue;
                removeAt(i);
                break;
            }
        }
    }
}
