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
        int leftPos = start, rightPos = end, pivotPos = start;
        T pivot = objects.get(start);
        while (leftPos < rightPos) {
            for (; leftPos < rightPos; rightPos--) {
                T object = objects.get(rightPos);
                if (comparator.compare(object, pivot) >= 0) continue;
                objects.set(pivotPos, object);
                pivotPos = rightPos;
                rightPos--;
                break;
            }
            for (; leftPos < rightPos; leftPos++) {
                T object = objects.get(leftPos);
                if (comparator.compare(object, pivot) <= 0) continue;
                objects.set(pivotPos, object);
                pivotPos = leftPos;
                leftPos++;
                break;
            }
        }
        objects.set(pivotPos, pivot);
        quickSort(objects, comparator, start, pivotPos - 1);
        quickSort(objects, comparator, pivotPos + 1, end);
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
     * @param pos 调整位置
     * @param heapSize 堆大小
     * @param <T>
     */
    private static <T> void adjustHeap(List<T> objects, Comparator<T> comparator,
                                       int pos, int heapSize) {
        while (pos < heapSize - 1) {
            int leftPos = pos * 2, rightPos = leftPos + 1;
            T leftObject = leftPos >= heapSize ? null : objects.get(leftPos);
            T rightObject = rightPos >= heapSize ? null : objects.get(rightPos);
            if (leftObject == null && rightObject == null) break;
            T comparedObject = leftObject == null ? rightObject : (rightObject == null ? leftObject :
                    comparator.compare(leftObject, rightObject) >= 0 ? leftObject : rightObject);
            int comparedPos = comparedObject == leftObject ? leftPos : rightPos;
            T object = objects.get(pos);
            if (comparator.compare(object, comparedObject) >= 0) break;
            objects.set(pos, comparedObject);
            objects.set(comparedPos, object);
            pos = comparedPos;
        }
    }
}
