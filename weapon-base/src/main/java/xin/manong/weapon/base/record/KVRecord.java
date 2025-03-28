package xin.manong.weapon.base.record;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.util.RandomID;

import java.io.Serializable;
import java.util.*;

/**
 * KV数据
 *
 * @author frankcl
 * @date 2019-05-27 13:23
 */
@Getter
public class KVRecord implements Serializable {

    private final static Logger logger = LoggerFactory.getLogger(KVRecord.class);

    private final String id;
    @Setter
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
     * @return 数据拷贝
     */
    public KVRecord copy() {
        KVRecord replica = new KVRecord();
        replica.recordType = recordType;
        replica.setKeys(keys);
        replica.setFieldMap(fieldMap);
        return replica;
    }

    /**
     * 拷贝数据，移除removeFields定义字段
     *
     * @param removeFields 移除字段集合
     * @return 数据拷贝
     */
    public KVRecord copy(Set<String> removeFields) {
        KVRecord replica = copy();
        if (removeFields == null || removeFields.isEmpty()) return replica;
        for (String removeField : removeFields) {
            if (!replica.has(removeField)) continue;
            replica.remove(removeField);
        }
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
     * @param key 键
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
     * 根据key获取值并强制转换为指定类型
     *
     * @param key key
     * @param clazz 数据类型
     * @return 如果key存在返回值，不存在返回null，与指定类型不一致返回null
     * @param <T> 数据类型
     */
    public <T> T get(String key, Class<T> clazz) {
        if (clazz == null) throw new RuntimeException("convert class is null");
        Object object = get(key);
        if (object == null) return null;
        try {
            return clazz.cast(object);
        } catch (ClassCastException e) {
            logger.error("field[{}] is not an instance of class[{}]", key, clazz.getName());
            logger.error(e.getMessage(), e);
            return null;
        }
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
     * 设置key集合
     *
     * @param keys key集合
     */
    public void setKeys(Set<String> keys) {
        this.keys.clear();
        if (keys != null) this.keys.addAll(keys);
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
        StringBuilder builder = new StringBuilder();
        builder.append("record[");
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            if (builder.length() != 7) builder.append(", ");
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue().toString());
        }
        builder.append("]");
        return builder.toString();
    }
}
