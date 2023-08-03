package xin.manong.weapon.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Map工具
 *
 * @author frankcl
 * @create 2019-06-22 14:16
 */
public class MapUtil {

    private final static Logger logger = LoggerFactory.getLogger(MapUtil.class);

    /**
     * 判断map是否相等
     * 1. 大小不相同则不相等
     * 2. 包含key不相同则不相等
     * 3. key对应值不相等则不相等（值仅限简单类型及简单类型的数组）
     *
     * @param leftMap map1
     * @param rightMap map2
     * @return 相等返回true，否则返回false
     */
    public static boolean equals(Map<String, Object> leftMap, Map<String, Object> rightMap) {
        if (leftMap == rightMap) return true;
        if (leftMap == null && rightMap != null) return false;
        if (leftMap != null && rightMap == null) return false;
        if (leftMap.size() != rightMap.size()) return false;
        for (Map.Entry<String, Object> entry : leftMap.entrySet()) {
            String k = entry.getKey();
            Object v1 = entry.getValue();
            if (!rightMap.containsKey(k)) return false;
            Object v2 = rightMap.get(k);
            boolean isArray1 = v1.getClass().isArray();
            boolean isArray2 = v2.getClass().isArray();
            if (isArray1 != isArray2) return false;
            if (!isArray1 && !v1.equals(v2)) return false;
            if (isArray1 && !arrayEquals(v1, v2)) return false;
        }
        return true;
    }

    /**
     * 用replaceMap内容安全替换targetMap内容
     *
     * @param targetMap 待替换map
     * @param replaceMap 替换map
     */
    public static <K, V> void safeReplace(Map<K, V> targetMap, Map<K, V> replaceMap) {
        if (targetMap == null || replaceMap == null) return;
        targetMap.putAll(replaceMap);
        List<K> keys = new ArrayList<>(targetMap.keySet());
        for (K key : keys) {
            if (replaceMap.containsKey(key)) continue;
            targetMap.remove(key);
        }
    }

    /**
     * 获取map值，值必须符合指定class类型
     *
     * @param map map对象
     * @param key key
     * @param clazz 期望class
     * @return 如果成功返回值对象，否则返回null
     */
    public static <T> T getValue(Map<String, Object> map, String key, Class<T> clazz) {
        if (map == null || !map.containsKey(key)) return null;
        Object v = null;
        try {
            v = map.get(key);
            return clazz.cast(v);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 判断两个数组是否相等
     * 1. 数组大小不相同则不相等
     * 2. 数组对应元素不相等则不相等
     *
     * @param array1 数组1
     * @param array2 数组2
     * @return 相等返回true，否则返回false
     */
    private static boolean arrayEquals(Object array1, Object array2) {
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
     * @param o1 对象1
     * @param o2 对象2
     * @return 相等返回true，否则返回false
     */
    private static boolean objectEquals(Object o1, Object o2) {
        if (o1 == o2) return true;
        if (o1 == null && o2 != null) return false;
        if (o1 != null && o2 == null) return false;
        if (o1.getClass() != o2.getClass()) return false;
        return o1.equals(o2);
    }
}
