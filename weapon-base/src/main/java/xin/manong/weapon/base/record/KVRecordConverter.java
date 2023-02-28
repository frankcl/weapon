package xin.manong.weapon.base.record;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.util.CommonUtil;
import xin.manong.weapon.base.util.JSONUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * KVRecord数据转换器
 *
 * @author frankcl
 * @date 2022-12-14 16:37:14
 */
public class KVRecordConverter {

    private final static Logger logger = LoggerFactory.getLogger(KVRecordConverter.class);

    /**
     * KVRecord转换JSON对象
     *
     * @param kvRecord 数据
     * @param fields 转换字段集合，空代表转换所有字段
     * @return JSON对象
     */
    public static JSONObject convertToJSON(KVRecord kvRecord, Set<String> fields) {
        return convertToJSON(kvRecord, fields, false);
    }

    /**
     * KVRecord转换JSON对象
     *
     * @param kvRecord 数据
     * @param fields 转换字段集合，空代表转换所有字段
     * @param jsonParse 对复杂对象是否进行json结构化
     * @return JSON对象
     */
    public static JSONObject convertToJSON(KVRecord kvRecord, Set<String> fields, Boolean jsonParse) {
        JSONObject json = new JSONObject();
        if (kvRecord == null) return json;
        Iterator<Map.Entry<String, Object>> iterator = kvRecord.getFieldMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            if (fields != null && !fields.contains(key)) continue;
            Object value = entry.getValue();
            if (value instanceof JSONArray) json.put(key, JSONUtil.deepCopy((JSONArray) value));
            else if (value instanceof JSONObject) json.put(key, JSONUtil.deepCopy((JSONObject) value));
            else if (CommonUtil.isPrimitiveType(value) || (jsonParse == null || !jsonParse)) json.put(key, value);
            else {
                try {
                    json.put(key, JSON.toJSON(value));
                } catch (Exception e) {
                    logger.error("convert java object to JSON failed for type[{}]", value.getClass().getName());
                    logger.error(e.getMessage(), e);
                    json.put(key, value);
                }
            }
        }
        return json;
    }
}
