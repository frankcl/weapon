package xin.manong.weapon.base.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
