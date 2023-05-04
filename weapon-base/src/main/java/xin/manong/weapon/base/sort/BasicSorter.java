package xin.manong.weapon.base.sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

/**
 * 基础排序
 *
 * @author frankcl
 * @date 2023-04-25 10:58:36
 */
public class BasicSorter {

    private static final Logger logger = LoggerFactory.getLogger(BasicSorter.class);

    /**
     * 快速排序
     *
     * @param objects 排序列表
     * @param comparator 比较器
     * @param <T>
     */
    public static <T> void quickSort(List<T> objects, Comparator<T> comparator) {
        if (objects == null || objects.isEmpty()) {
            logger.warn("sorting objects are empty");
            return;
        }
        if (comparator == null) {
            logger.warn("comparator is null");
            return;
        }
        quickSort(objects, comparator, 0, objects.size() - 1);
    }

    /**
     * 快速排序
     *
     * @param objects 排序列表
     * @param comparator 比较器
     * @param start 起始元素位置
     * @param end 结束元素位置
     * @param <T>
     */
    private static <T> void quickSort(List<T> objects, Comparator<T> comparator,
                                      int start, int end) {
        if (start >= end) return;
        int left = start, right = end, pivot = start;
        T pivotObject = objects.get(start);
        while (left < right) {
            for (; left < right; right--) {
                T object = objects.get(right);
                if (comparator.compare(object, pivotObject) >= 0) continue;
                objects.set(pivot, object);
                pivot = right;
                right--;
                break;
            }
            for (; left < right; left++) {
                T object = objects.get(left);
                if (comparator.compare(object, pivotObject) <= 0) continue;
                objects.set(pivot, object);
                pivot = left;
                left++;
                break;
            }
        }
        objects.set(pivot, pivotObject);
        quickSort(objects, comparator, start, pivot - 1);
        quickSort(objects, comparator, pivot + 1, end);
    }

    /**
     * 堆排序
     *
     * @param objects 排序列表
     * @param comparator 比较器
     * @param <T>
     */
    public static <T> void heapSort(List<T> objects, Comparator<T> comparator) {
        if (objects == null || objects.isEmpty()) {
            logger.warn("sorting objects are empty");
            return;
        }
        if (comparator == null) {
            logger.warn("comparator is null");
            return;
        }
        for (int i = (objects.size() - 1) / 2; i >= 0; i--) adjustHeap(objects, comparator, i, objects.size());
        for (int i = objects.size() - 1; i >= 0; i--) {
            T object = objects.get(0);
            objects.set(0, objects.get(i));
            objects.set(i, object);
            adjustHeap(objects, comparator, 0, i);
        }
    }

    /**
     * 调整堆
     *
     * @param objects 堆列表
     * @param comparator 比较器
     * @param position 调整位置
     * @param heapSize 堆大小
     * @param <T>
     */
    private static <T> void adjustHeap(List<T> objects, Comparator<T> comparator,
                                       int position, int heapSize) {
        while (position < heapSize - 1) {
            int left = position * 2, right = left + 1;
            T leftChild = left >= heapSize ? null : objects.get(left);
            T rightChild = right >= heapSize ? null : objects.get(right);
            if (leftChild == null && rightChild == null) break;
            T selectedChild = leftChild == null ? rightChild : (rightChild == null ? leftChild :
                    comparator.compare(leftChild, rightChild) >= 0 ? leftChild : rightChild);
            int adjustedPosition = selectedChild == leftChild ? left : right;
            T object = objects.get(position);
            if (comparator.compare(object, selectedChild) >= 0) break;
            objects.set(position, selectedChild);
            objects.set(adjustedPosition, object);
            position = adjustedPosition;
        }
    }
}
