package xin.manong.weapon.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Map工具
 *
 * @author frankcl
 * @date 2019-06-22 14:16
 */
public class MapUtil {

    private final static Logger logger = LoggerFactory.getLogger(MapUtil.class);

    private static final Pattern pattern = Pattern.compile("(.+?)\\[\\d+]");

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
        try {
            Object v = map.get(key);
            return clazz.cast(v);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 扁平Map转换多层Map
     *
     * @param flattenMap 扁平Map
     * @return 多层Map
     */
    public static Map<String, Object> flattenMapToMultiMap(Map<String, Object> flattenMap) {
        Map<String, Object> multiMap = new HashMap<>();
        List<String> flattenKeys = new ArrayList<>(flattenMap.keySet());
        flattenKeys.sort(String::compareTo);
        for (String flattenKey : flattenKeys) {
            String[] multiKeys = flattenKey.split("\\.");
            flattenValueToMultiValue(multiMap, multiKeys, flattenMap.get(flattenKey));
        }
        return multiMap;
    }

    /**
     * 扁平Map值转换多层Map值
     *
     * @param multiMap 扁平Map
     * @param multiKeys 多层key
     * @param value Map值
     */
    @SuppressWarnings("unchecked")
    private static void flattenValueToMultiValue(Map<String, Object> multiMap, String[] multiKeys, Object value) {
        if (multiKeys.length < 1) return;
        if (multiKeys.length > 1) {
            if (!multiMap.containsKey(multiKeys[0])) multiMap.put(multiKeys[0], new HashMap<>());
            Map<String, Object> innerMap = (Map<String, Object>) multiMap.get(multiKeys[0]);
            flattenValueToMultiValue(innerMap, Arrays.copyOfRange(multiKeys, 1, multiKeys.length), value);
            return;
        }
        Matcher matcher = pattern.matcher(multiKeys[0]);
        if (matcher.matches()) {
            String key = matcher.group(1);
            if (!multiMap.containsKey(key)) multiMap.put(key, new ArrayList<>());
            List<Object> innerList = (List<Object>) multiMap.get(key);
            innerList.add(value);
        } else {
            multiMap.put(multiKeys[0], value);
        }
    }
}
