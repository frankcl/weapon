package xin.manong.weapon.base.record;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    /**
     * KVRecord转换JSON对象
     *
     * @param kvRecord 数据
     * @param fields 转换字段集合
     * @return JSON对象
     */
    public static JSONObject convert2JSON(KVRecord kvRecord, Set<String> fields) {
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
            else json.put(key, value);
        }
        return json;
    }
}
