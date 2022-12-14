package xin.manong.weapon.base.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.util.RandomID;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * KV数据
 *
 * @author frankcl
 * @create 2019-05-27 13:23
 */
public class KVRecord implements Serializable {

    private final static Logger logger = LoggerFactory.getLogger(KVRecord.class);

    private String id;
    private RecordType recordType;
    private final Set<String> keys;
    private final Map<String, Object> fieldMap;

    public KVRecord() {
        id = RandomID.build();
        recordType = RecordType.PUT;
        keys = new HashSet<>();
        fieldMap = new HashMap<>();
    }

    public KVRecord(Set<String> keys, Map<String, Object> fieldMap) {
        id = RandomID.build();
        recordType = RecordType.PUT;
        this.keys = keys;
        this.fieldMap = fieldMap;
    }

    /**
     * 拷贝数据：针对JSON数据进行深拷贝
     *
     * @return 拷贝数据
     */
    public KVRecord copy() {
        KVRecord replica = new KVRecord();
        replica.recordType = recordType;
        replica.setKeys(keys);
        replica.setFieldMap(fieldMap);
        return replica;
    }

    /**
     * 判断是否为空
     *
     * @return 如果没有字段返回true，否则返回false
     */
    public boolean isEmpty() {
        return fieldMap == null || fieldMap.isEmpty();
    }

    /**
     * 获取字段数量
     *
     * @return 返回大于等于0的值
     */
    public int getFieldCount() {
        return fieldMap == null ? 0 : fieldMap.size();
    }

    /**
     * 是否包含key
     *
     * @param key
     * @return 如果key为null返回false；如果包含返回true，否则返回false
     */
    public boolean has(String key) {
        if (key == null) return false;
        return fieldMap.containsKey(key);
    }

    /**
     * 增加数据
     * 如果key或value为null，不产生效果
     *
     * @param key key
     * @param value 数据值
     */
    public void put(String key, Object value) {
        if (key == null || value == null) return;
        if (fieldMap.containsKey(key)) logger.debug("key[{}] has existed, overwrite it", key);
        fieldMap.put(key, value);
    }

    /**
     * 根据key获取值
     *
     * @param key key
     * @return 如果key存在返回值，否则返回null
     */
    public Object get(String key) {
        return fieldMap.getOrDefault(key, null);
    }

    /**
     * 删除指定key数据
     *
     * @param key key
     * @return 如果成功返回true，否则返回false
     */
    public boolean remove(String key) {
        if (fieldMap.containsKey(key)) {
            fieldMap.remove(key);
            return true;
        }
        return false;
    }

    /**
     * 清除所有字段信息
     */
    public void clear() {
        keys.clear();
        fieldMap.clear();
    }

    /**
     * 获取key集合
     *
     * @return key集合
     */
    public Set<String> getKeys() {
        return keys;
    }

    /**
     * 设置key集合
     *
     * @param keys key集合
     */
    public void setKeys(Set<String> keys) {
        this.keys.clear();
        if (keys != null) this.keys.addAll(keys);
    }

    /**
     * 获取数据
     *
     * @return 数据
     */
    public Map<String, Object> getFieldMap() {
        return fieldMap;
    }

    /**
     * 获取key值映射
     *
     * @return key值映射
     */
    public final Map<String, Object> getKeyMap() {
        Map<String, Object> keyMap = new HashMap<>();
        if (keys == null || keys.isEmpty()) return keyMap;
        for (String key : keys) {
            if (!fieldMap.containsKey(key)) continue;
            keyMap.put(key, fieldMap.get(key));
        }
        return keyMap;
    }

    /**
     * 获取数据ID
     *
     * @return 数据ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取数据类型
     *
     * @return 数据类型枚举
     */
    public RecordType getRecordType() {
        return recordType;
    }

    /**
     * 设置数据类型
     *
     * @param recordType 数据类型枚举
     */
    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    /**
     * 设置数据内容
     *
     * @param fieldMap 数据内容
     */
    public void setFieldMap(Map<String, Object> fieldMap) {
        this.fieldMap.clear();
        if (fieldMap != null) this.fieldMap.putAll(fieldMap);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof KVRecord)) return false;
        KVRecord kvRecord = (KVRecord) object;
        return id.equals(kvRecord.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        boolean firstFlag = true;
        buffer.append("kv record[");
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            if (!firstFlag) buffer.append(", ");
            else firstFlag = false;
            buffer.append(entry.getKey());
            buffer.append("=");
            buffer.append(entry.getValue().toString());
        }
        buffer.append("]");
        return buffer.toString();
    }
}
