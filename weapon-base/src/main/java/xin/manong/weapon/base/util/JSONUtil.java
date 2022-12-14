package xin.manong.weapon.base.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * JSON工具类
 *
 * @author frankcl
 * @create 2019-06-02 00:12
 */
public class JSONUtil {

    private final static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    /**
     * 从JSON数组选择指定key内容
     *
     * @param array JSON数组
     * @param keys key集合
     * @return 选择JSON数组
     */
    public static JSONArray select(JSONArray array, Set<String> keys) {
        JSONArray select = new JSONArray();
        Set<String> accessibleKeys = findAccessibleKeys(array, keys);
        if (accessibleKeys.isEmpty()) return select;
        for (String accessibleKey : accessibleKeys) {
            move(select, array, accessibleKey);
        }
        return select;
    }

    /**
     * 从JSON对象选择指定key内容
     *
     * @param object JSON对象
     * @param keys key集合
     * @return 选择JSON对象
     */
    public static JSONObject select(JSONObject object, Set<String> keys) {
        JSONObject select = new JSONObject();
        Set<String> accessibleKeys = findAccessibleKeys(object, keys);
        if (accessibleKeys.isEmpty()) return select;
        for (String accessibleKey : accessibleKeys) {
            move(select, object, accessibleKey);
        }
        return select;
    }

    /**
     * 从源JSON数组搬运指定key内容到目标JSON数组
     *
     * @param target 目标数组
     * @param source 源数组
     * @param key key
     */
    private static void move(JSONArray target, JSONArray source, String key) {
        for (int i = 0; i < source.size(); i++) {
            Object o = source.get(i);
            if (o instanceof JSONArray) {
                JSONArray array = i < target.size() ? target.getJSONArray(i) : new JSONArray();
                move(array, (JSONArray) o, key);
                if (i >= target.size()) target.add(array);
            } else if (o instanceof JSONObject) {
                JSONObject object = i < target.size() ? target.getJSONObject(i) : new JSONObject();
                move(object, (JSONObject) o, key);
                if (i >= target.size()) target.add(object);
            } else throw new RuntimeException(String.format("unexpected object[%s]", o.getClass().getName()));
        }
    }

    /**
     * 从源JSON对象搬运指定key内容到目标JSON对象
     *
     * @param target 目标对象
     * @param source 源对象
     * @param key key
     */
    private static void move(JSONObject target, JSONObject source, String key) {
        String[] splitKeys = key.split("\\.");
        if (splitKeys.length > 1) {
            Object o = source.get(splitKeys[0]);
            if (o == null) return;
            else if (o instanceof JSONArray) {
                JSONArray array = target.containsKey(splitKeys[0]) ?
                        target.getJSONArray(splitKeys[0]) : new JSONArray();
                move(array, (JSONArray) o, String.join(".",
                        Arrays.copyOfRange(splitKeys, 1, splitKeys.length)));
                if (!target.containsKey(splitKeys[0])) target.put(splitKeys[0], array);
            } else if (o instanceof JSONObject) {
                JSONObject object = target.containsKey(splitKeys[0]) ?
                        target.getJSONObject(splitKeys[0]) : new JSONObject();
                move(object, (JSONObject) o, String.join(".",
                        Arrays.copyOfRange(splitKeys, 1, splitKeys.length)));
                if (!target.containsKey(splitKeys[0])) target.put(splitKeys[0], object);
            } else throw new RuntimeException(String.format("unexpected object[%s]", o.getClass().getName()));
            return;
        }
        target.put(splitKeys[0], source.get(splitKeys[0]));
    }

    /**
     * 从候选key集合中寻找可达的key集合
     *
     * @param json JSON数组或对象
     * @param keys key集合
     * @return 可达key集合
     */
    private static Set<String> findAccessibleKeys(JSON json, Set<String> keys) {
        Set<String> accessibleKeys = new HashSet<>();
        for (String key : keys) {
            if (json instanceof JSONObject) {
                if (get((JSONObject) json, key) != null) accessibleKeys.add(key);
            } else if (json instanceof JSONArray) {
                if (get((JSONArray) json, key) != null) accessibleKeys.add(key);
            }
        }
        return accessibleKeys;
    }

    /**
     * 合并JSON数组
     *
     * @param array1 待合并数组
     * @param array2 待合并数组
     * @return 合并结果
     */
    public static JSONArray merge(JSONArray array1, JSONArray array2) {
        if (array1 == null && array2 == null) return null;
        if (array1 == null) {
            JSONArray array = new JSONArray();
            array.addAll(array2);
            return array;
        }
        if (array2 == null) {
            JSONArray array = new JSONArray();
            array.addAll(array1);
            return array;
        }
        Set<Object> set = new HashSet<>(array1);
        set.addAll(array2);
        return new JSONArray(new ArrayList<>(set));
    }

    /**
     * 合并JSON对象
     *
     * @param object1 待合并对象
     * @param object2 待合并对象
     * @return 合并结果
     */
    public static JSONObject merge(JSONObject object1, JSONObject object2) {
        if (object1 == null && object2 == null) return null;
        JSONObject object = new JSONObject();
        if (object1 == null) {
            object.putAll(object2);
            return object;
        }
        if (object2 == null) {
            object.putAll(object1);
            return object;
        }
        object.putAll(object1);
        object.putAll(object2);
        return object;
    }

    /**
     * 深度拷贝JSON数组
     *
     * @param array
     * @return 数据拷贝
     */
    public static JSONArray deepCopy(JSONArray array) {
        JSONArray replica = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            Object o = array.get(i);
            if (o instanceof JSONObject) {
                replica.add(deepCopy((JSONObject) o));
            } else if (o instanceof JSONArray) {
                replica.add(deepCopy((JSONArray) o));
            } else {
                replica.add(o);
            }
        }
        return replica;
    }

    /**
     * 深度拷贝JSON对象
     *
     * @param object
     * @return 数据拷贝
     */
    public static JSONObject deepCopy(JSONObject object) {
        JSONObject replica = new JSONObject();
        for (String key : object.keySet()) {
            Object o = object.get(key);
            if (o instanceof JSONObject) {
                replica.put(key, deepCopy((JSONObject) o));
            } else if (o instanceof JSONArray) {
                replica.put(key, deepCopy((JSONArray) o));
            } else {
                replica.put(key, o);
            }
        }
        return replica;
    }

    /**
     * 根据key获取JSON对象字段
     *
     * @param object JSON对象
     * @param key 点分隔全路径key
     * @return 如果找到返回对象，否则返回null
     */
    public static Object get(JSONObject object, String key) {
        if (StringUtils.isEmpty(key)) return null;
        return get(object, key.split("\\."));
    }

    /**
     * 根据key获取JSON数组字段
     *
     * @param array JSON数组
     * @param key 点分隔全路径key
     * @return 如果找到返回列表，否则返回null
     */
    public static List<Object> get(JSONArray array, String key) {
        if (StringUtils.isEmpty(key)) return null;
        return get(array, key.split("\\."));
    }

    /**
     * 根据key列表获取JSON对象字段值
     *
     * @param object JSON对象
     * @param keys key数组
     * @return 如果找到返回值，否则返回null
     */
    private static Object get(JSONObject object, String[] keys) {
        if (object == null || keys.length == 0) return null;
        Object o = object.get(keys[0]);
        if (o == null) return null;
        if (keys.length == 1) return o;
        if (o instanceof JSONObject) {
            return get((JSONObject) o, Arrays.copyOfRange(keys, 1, keys.length));
        } else if (o instanceof JSONArray) {
            return get((JSONArray) o, Arrays.copyOfRange(keys, 1, keys.length));
        }
        return null;
    }

    /**
     * 根据key列表获取JSON数组字段值
     *
     * @param array JSON数组
     * @param keys key数组
     * @return 如果找到返回值列表，否则返回null
     */
    private static List<Object> get(JSONArray array, String[] keys) {
        if (array == null || array.isEmpty() || keys.length == 0) return null;
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Object object = array.get(i);
            if (object instanceof JSONObject) {
                Object o = get((JSONObject) object, keys);
                if (o == null) continue;
                else if (o instanceof List) objects.addAll((List) o);
                else objects.add(o);
            } else if (object instanceof JSONArray) {
                List<Object> o = get((JSONArray) object, keys);
                if (o != null) objects.addAll(o);
            }
        }
        return objects.isEmpty() ? null : objects;
    }

    /**
     * 对JSON对象的指定字段进行JSON解析
     * 如果字段值是字符串且符合JSON规则，则解析为JSON，否则不做解析
     *
     * @param json JSON对象
     * @param fields 需要解析的字段集合
     */
    public static void parseFields(JSONObject json, Set<String> fields) {
        for (String key : json.keySet()) {
            if (fields == null || !fields.contains(key)) continue;
            Object v = json.get(key);
            if (!(v instanceof String)) continue;
            try {
                json.put(key, JSON.parse((String) v));
            } catch (Exception e) {
                logger.warn("parse json failed for value[{}]", v);
            }
        }
    }
}
