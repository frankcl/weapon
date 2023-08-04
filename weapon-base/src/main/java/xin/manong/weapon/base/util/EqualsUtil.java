package xin.manong.weapon.base.util;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * 对象相等比较工具
 *
 * @author frankcl
 * @date 2023-08-04 10:26:17
 */
public class EqualsUtil {

    /**
     * 判断列表是否相等
     * 1. 列表大小不相同则不相等
     * 2. 列表对应元素不相等则不相等
     *
     * @param list1 比较列表
     * @param list2 比较列表
     * @return 相等返回true，否则返回false
     * @param <T>
     */
    public static <T> boolean listEquals(List<T> list1, List<T> list2) {
        if (list1 == list2) return true;
        if (list1 == null && list2 != null) return false;
        if (list1 != null && list2 == null) return false;
        if (list1.size() != list2.size()) return false;
        for (int i = 0; i < list1.size(); i++) {
            T v1 = list1.get(i), v2 = list2.get(i);
            boolean isArray1 = v1.getClass().isArray();
            boolean isArray2 = v2.getClass().isArray();
            if (isArray1 != isArray2) return false;
            if (!isArray1 && !objectEquals(v1, v2)) return false;
            if (isArray1 && !arrayEquals(v1, v2)) return false;
        }
        return true;
    }

    /**
     * 判断map是否相等
     * 1. 大小不相同则不相等
     * 2. 包含key不相同则不相等
     * 3. key对应值不相等则不相等
     *
     * @param map1 比较map
     * @param map1 比较map
     * @return 相等返回true，否则返回false
     * @param <K>
     * @param <V>
     */
    public static <K, V> boolean mapEquals(Map<K, V> map1, Map<K, V> map2) {
        if (map1 == map2) return true;
        if (map1 == null && map2 != null) return false;
        if (map1 != null && map2 == null) return false;
        if (map1.size() != map2.size()) return false;
        for (Map.Entry<K, V> entry : map1.entrySet()) {
            K k = entry.getKey();
            V v1 = entry.getValue();
            if (!map2.containsKey(k)) return false;
            V v2 = map2.get(k);
            boolean isArray1 = v1.getClass().isArray();
            boolean isArray2 = v2.getClass().isArray();
            if (isArray1 != isArray2) return false;
            if (!isArray1 && !objectEquals(v1, v2)) return false;
            if (isArray1 && !arrayEquals(v1, v2)) return false;
        }
        return true;
    }

    /**
     * 判断两个数组是否相等
     * 1. 数据类型不是数组则不相等
     * 2. 数组大小不相同则不相等
     * 2. 数组对应元素不相等则不相等
     *
     * @param array1 比较数组
     * @param array2 比较数组
     * @return 相等返回true，否则返回false
     */
    public static boolean arrayEquals(Object array1, Object array2) {
        if (array1 == array2) return true;
        if (array1 == null && array2 != null) return false;
        if (array1 != null && array2 == null) return false;
        if (!array1.getClass().isArray() || !array2.getClass().isArray()) return false;
        int len1 = Array.getLength(array1), len2 = Array.getLength(array2);
        if (len1 != len2) return false;
        for (int i = 0; i < len1; i++) {
            Object o1 = Array.get(array1, i);
            Object o2 = Array.get(array2, i);
            if (!objectEquals(o1, o2)) return false;
        }
        return true;
    }

    /**
     * 判断两个对象是否相等
     * 1. 引用相同则相等
     * 2. 类型不同不相等
     * 3. 类型相同，equals返回true则相等
     *
     * @param o1 比较对象
     * @param o2 比较对象
     * @return 相等返回true，否则返回false
     */
    public static boolean objectEquals(Object o1, Object o2) {
        if (o1 == o2) return true;
        if (o1 == null && o2 != null) return false;
        if (o1 != null && o2 == null) return false;
        if (o1.getClass() != o2.getClass()) return false;
        return o1.equals(o2);
    }
}
