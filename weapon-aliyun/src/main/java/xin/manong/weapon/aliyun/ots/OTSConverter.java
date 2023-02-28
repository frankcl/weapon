package xin.manong.weapon.aliyun.ots;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alicloud.openservices.tablestore.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.RecordType;
import xin.manong.weapon.base.util.ReflectUtil;

import java.lang.reflect.*;
import java.util.*;

/**
 * OTS数据转换
 *
 * @author frankcl
 * @date 2022-07-24 11:28:12
 */
public class OTSConverter {

    private final static Logger logger = LoggerFactory.getLogger(OTSConverter.class);

    /**
     * 转换流数据
     *
     * @param streamRecord 流数据
     * @return 成功返回转换结果，否则返回null
     */
    public static KVRecord convertStreamRecord(StreamRecord streamRecord) {
        if (streamRecord == null) return null;
        Map<String, Object> keyMap = convertPrimaryKey(streamRecord.getPrimaryKey());
        if (keyMap.isEmpty()) return null;
        KVRecord kvRecord = new KVRecord(new HashSet<>(keyMap.keySet()), keyMap);
        StreamRecord.RecordType recordType = streamRecord.getRecordType();
        if (recordType != null) kvRecord.setRecordType(RecordType.valueOf(recordType.name()));
        if (kvRecord.getRecordType() == RecordType.DELETE) return kvRecord;
        Map<String, Object> fieldMap = convertRecordColumns(streamRecord.getColumns());
        kvRecord.getFieldMap().putAll(fieldMap);
        return kvRecord;
    }

    /**
     * 转换KVRecord为java对象
     *
     * @param kvRecord kvRecord数据
     * @param javaClass java class
     * @return 成功返回java对象，否则抛出异常
     * @param <T> java对象类型
     */
    public static <T> T convertKVRecordToJavaObject(KVRecord kvRecord, Class<T> javaClass) {
        if (kvRecord == null || javaClass == null) throw new RuntimeException("convert record or java class is null");
        Map<String, Object> keyMap = kvRecord.getKeyMap();
        if (keyMap.isEmpty()) throw new RuntimeException("missing keys");
        Map<String, Object> fieldMap = kvRecord.getFieldMap();
        T javaObject = (T) ReflectUtil.newInstance(javaClass, null);
        List<Field> primaryKeyFields = ReflectUtil.getAnnotatedFields(javaObject,
                xin.manong.weapon.aliyun.ots.annotation.PrimaryKey.class);
        for (Field primaryKeyField : primaryKeyFields) {
            xin.manong.weapon.aliyun.ots.annotation.PrimaryKey primaryKey = ReflectUtil.getFieldAnnotation(
                    primaryKeyField, xin.manong.weapon.aliyun.ots.annotation.PrimaryKey.class);
            String keyName = StringUtils.isEmpty(primaryKey.name()) ? primaryKeyField.getName() : primaryKey.name();
            if (!keyMap.containsKey(keyName)) {
                logger.error("primary key[{}] is not found from record", keyName);
                throw new RuntimeException(String.format("primary key[%s] is not found from record", keyName));
            }
            ReflectUtil.setFieldValue(javaObject, primaryKeyField.getName(), keyMap.get(keyName));
        }
        List<Field> columnFields = ReflectUtil.getAnnotatedFields(javaObject,
                xin.manong.weapon.aliyun.ots.annotation.Column.class);
        for (Field columnField : columnFields) {
            xin.manong.weapon.aliyun.ots.annotation.Column column = ReflectUtil.getFieldAnnotation(
                    columnField, xin.manong.weapon.aliyun.ots.annotation.Column.class);
            String columnName = StringUtils.isEmpty(column.name()) ? columnField.getName() : column.name();
            if (!fieldMap.containsKey(columnName)) continue;
            Object javaField = convertColumnValueToJavaField(fieldMap.get(columnName), columnField);
            if (javaField == null) continue;
            if (javaField instanceof Long && columnField.getType() == Integer.class) {
                javaField = ((Long) javaField).intValue();
            }
            ReflectUtil.setFieldValue(javaObject, columnField.getName(), javaField);
        }
        return javaObject;
    }

    /**
     * 转换java对象为KVRecord
     *
     * @param javaObject java对象
     * @return 成功返回KVRecord，否则抛出异常
     */
    public static KVRecord convertJavaObjectToKVRecord(Object javaObject) {
        if (javaObject == null) throw new RuntimeException("convert java object is null");
        List<Field> primaryKeyFields = ReflectUtil.getAnnotatedFields(
                javaObject, xin.manong.weapon.aliyun.ots.annotation.PrimaryKey.class);
        if (primaryKeyFields.isEmpty()) {
            logger.error("primary keys are not annotated for java object[{}]", javaObject.getClass().getName());
            throw new RuntimeException(String.format("primary keys are not annotated for java object[%s]",
                    javaObject.getClass().getName()));
        }
        Set<String> keys = new HashSet<>();
        KVRecord kvRecord = new KVRecord();
        for (Field primaryKeyField : primaryKeyFields) {
            xin.manong.weapon.aliyun.ots.annotation.PrimaryKey primaryKey = ReflectUtil.getFieldAnnotation(
                    primaryKeyField, xin.manong.weapon.aliyun.ots.annotation.PrimaryKey.class);
            String keyName = StringUtils.isEmpty(primaryKey.name()) ? primaryKeyField.getName() : primaryKey.name();
            Object value = ReflectUtil.getFieldValue(javaObject, primaryKeyField.getName());
            if (!checkColumn(value)) {
                throw new RuntimeException(String.format("primary key[{}] is invalid", primaryKeyField.getName()));
            }
            kvRecord.put(keyName, value);
            keys.add(keyName);
        }
        List<Field> columnFields = ReflectUtil.getAnnotatedFields(javaObject,
                xin.manong.weapon.aliyun.ots.annotation.Column.class);
        for (Field columnField : columnFields) {
            xin.manong.weapon.aliyun.ots.annotation.Column column = ReflectUtil.getFieldAnnotation(
                    columnField, xin.manong.weapon.aliyun.ots.annotation.Column.class);
            String columnName = StringUtils.isEmpty(column.name()) ? columnField.getName() : column.name();
            Object value = ReflectUtil.getFieldValue(javaObject, columnField.getName());
            if (value == null) continue;
            if (value.getClass().isEnum()) value = ((Enum) value).name();
            kvRecord.put(columnName, convertJavaFieldToColumnValue(value));
        }
        kvRecord.setKeys(keys);
        return kvRecord;
    }

    /**
     * 转换java对象为keyMap
     *
     * @param javaObject java对象
     * @return 成功返回keyMap，否则抛出异常
     */
    public static Map<String, Object> convertJavaObjectToKeyMap(Object javaObject) {
        if (javaObject == null) throw new RuntimeException("convert java object is null");
        List<Field> primaryKeyFields = ReflectUtil.getAnnotatedFields(
                javaObject, xin.manong.weapon.aliyun.ots.annotation.PrimaryKey.class);
        if (primaryKeyFields.isEmpty()) {
            logger.error("primary keys are not annotated for java object[{}]", javaObject.getClass().getName());
            throw new RuntimeException(String.format("primary keys are not annotated for java object[%s]",
                    javaObject.getClass().getName()));
        }
        Map<String, Object> keyMap = new HashMap<>();
        for (Field primaryKeyField : primaryKeyFields) {
            xin.manong.weapon.aliyun.ots.annotation.PrimaryKey primaryKey = ReflectUtil.getFieldAnnotation(
                    primaryKeyField, xin.manong.weapon.aliyun.ots.annotation.PrimaryKey.class);
            String keyName = StringUtils.isEmpty(primaryKey.name()) ? primaryKeyField.getName() : primaryKey.name();
            Object value = ReflectUtil.getFieldValue(javaObject, primaryKeyField.getName());
            if (!checkColumn(value)) {
                throw new RuntimeException(String.format("primary key[{}] is invalid", primaryKeyField.getName()));
            }
            keyMap.put(keyName, value);
        }
        return keyMap;
    }

    /**
     * 转换OTS数据
     *
     * @param row 原始OTS数据
     * @return 如果成功返回转换结果，否则抛出RuntimeException
     */
    public static KVRecord convertRecord(Row row) {
        if (row == null) throw new RuntimeException("convert row is null");
        KVRecord kvRecord = new KVRecord();
        PrimaryKey primaryKey = row.getPrimaryKey();
        for (PrimaryKeyColumn primaryKeyColumn : primaryKey.getPrimaryKeyColumns()) {
            kvRecord.put(primaryKeyColumn.getName(), convertPrimaryKeyValue(primaryKeyColumn.getValue()));
        }
        kvRecord.setKeys(primaryKey.getPrimaryKeyColumnsMap().keySet());
        for (String name : getColumnNames(row)) {
            Column column = row.getLatestColumn(name);
            if (column == null) continue;
            kvRecord.put(name, convertColumnValue(column.getValue()));
        }
        return kvRecord;
    }

    /**
     * 转换OTS数据
     *
     * @param kvRecord 转换数据
     * @return 如果成功返回转换结果，否则抛出RuntimeException
     */
    public static Row convertRecord(KVRecord kvRecord) {
        if (kvRecord == null || kvRecord.isEmpty()) throw new RuntimeException("convert record is null or empty");
        if (CollectionUtils.isEmpty(kvRecord.getKeys())) throw new RuntimeException("convert keys are empty");
        Map<String, Object> keyMap = new HashMap(), columnMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : kvRecord.getFieldMap().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (kvRecord.getKeys().contains(key)) keyMap.put(key, value);
            else {
                if (value instanceof JSON) columnMap.put(key, value.toString());
                else if (value instanceof List) columnMap.put(key, JSON.toJSONString(value));
                else if (value instanceof Map) columnMap.put(key, JSON.toJSONString(value));
                else columnMap.put(key, value);
            }
        }
        if (keyMap.size() != kvRecord.getKeys().size()) throw new RuntimeException("missing primary keys");
        return new Row(convertPrimaryKey(keyMap), convertColumns(columnMap));
    }

    /**
     * 主键转换
     *
     * @param primaryKey OTS主键
     * @return 如果成功返回转换结果，否则抛出RuntimeException
     */
    public static Map<String, Object> convertPrimaryKey(PrimaryKey primaryKey) {
        Map<String, Object> keyMap = new HashMap<>();
        PrimaryKeyColumn[] primaryKeyColumns = primaryKey.getPrimaryKeyColumns();
        if (primaryKeyColumns == null) return keyMap;
        for (int i = 0; i < primaryKeyColumns.length; i++) {
            String name = primaryKeyColumns[i].getName();
            PrimaryKeyValue primaryKeyValue = primaryKeyColumns[i].getValue();
            keyMap.put(name, convertPrimaryKeyValue(primaryKeyValue));
        }
        return keyMap;
    }

    /**
     * 主键转换
     *
     * @param keyMap 主键映射
     * @return 如果成功返回转换结果，否则抛出RuntimeException
     */
    public static PrimaryKey convertPrimaryKey(Map<String, Object> keyMap) {
        if (keyMap == null || keyMap.isEmpty()) throw new RuntimeException("primary keys are empty");
        PrimaryKeyBuilder builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        for (Map.Entry<String, Object> entry : keyMap.entrySet()) {
            Object value = entry.getValue();
            checkPrimaryKey(value);
            if (value instanceof Integer) {
                PrimaryKeyValue primaryKeyValue = PrimaryKeyValue.fromLong(((Integer) value).longValue());
                builder.addPrimaryKeyColumn(entry.getKey(), primaryKeyValue);
            } else if (value instanceof Long) {
                PrimaryKeyValue primaryKeyValue = PrimaryKeyValue.fromLong((Long) value);
                builder.addPrimaryKeyColumn(entry.getKey(), primaryKeyValue);
            } else if (value instanceof byte[]) {
                PrimaryKeyValue primaryKeyValue = PrimaryKeyValue.fromBinary((byte[]) value);
                builder.addPrimaryKeyColumn(entry.getKey(), primaryKeyValue);
            } else if (value == PrimaryKeyValue.INF_MIN || value == PrimaryKeyValue.INF_MAX) {
                builder.addPrimaryKeyColumn(entry.getKey(), (PrimaryKeyValue) value);
            } else {
                PrimaryKeyValue primaryKeyValue = PrimaryKeyValue.fromString((String) value);
                builder.addPrimaryKeyColumn(entry.getKey(), primaryKeyValue);
            }
        }
        return builder.build();
    }

    /**
     * 转换列
     *
     * @param recordColumns OTS流数据列
     * @return 如果成功返回转换结果，否则抛出RuntimeException
     */
    public static Map<String, Object> convertRecordColumns(List<RecordColumn> recordColumns) {
        Map<String, Object> columnMap = new HashMap<>();
        for (RecordColumn recordColumn : recordColumns) {
            RecordColumn.ColumnType columnType = recordColumn.getColumnType();
            if (columnType == RecordColumn.ColumnType.DELETE_ALL_VERSION ||
                    columnType == RecordColumn.ColumnType.DELETE_ONE_VERSION) continue;
            Column column = recordColumn.getColumn();
            columnMap.put(column.getName(), convertColumnValue(column.getValue()));
        }
        return columnMap;
    }

    /**
     * 转换列
     *
     * @param columnMap 列映射
     * @return 如果成功返回转换结果，否则抛出RuntimeException
     */
    public static List<Column> convertColumns(Map<String, Object> columnMap) {
        if (columnMap == null || columnMap.isEmpty()) throw new RuntimeException("column map is empty");
        List<Column> columns = new ArrayList<>();
        for (Map.Entry<String, Object> entry : columnMap.entrySet()) {
            Object value = entry.getValue();
            if (!checkColumn(value)) continue;
            ColumnValue columnValue;
            if (value instanceof Integer) {
                columnValue = ColumnValue.fromLong(((Integer) value).longValue());
            } else if (value instanceof Long) {
                columnValue = ColumnValue.fromLong((Long) value);
            } else if (value instanceof Float) {
                columnValue = ColumnValue.fromDouble(((Float) value).doubleValue());
            } else if (value instanceof Double) {
                columnValue = ColumnValue.fromDouble((Double) value);
            } else if (value instanceof Boolean) {
                columnValue = ColumnValue.fromBoolean((Boolean) value);
            } else if (value instanceof byte[]) {
                columnValue = ColumnValue.fromBinary((byte[]) value);
            } else {
                columnValue = ColumnValue.fromString((String) value);
            }
            columns.add(new Column(entry.getKey(), columnValue));
        }
        return columns;
    }

    /**
     * 转换Java字段为OTS列值
     * 复杂对象转化为JSON字符串
     *
     * @param javaObject java字段值
     * @return OTS列值
     */
    private static Object convertJavaFieldToColumnValue(Object javaObject) {
        if (javaObject == null) return null;
        if (checkColumn(javaObject)) return javaObject;
        return JSON.toJSONString(javaObject);
    }

    /**
     * 转换OTS列值为Java对象字段
     *
     * @param columnValue OTS列值
     * @param field java对象字段描述
     * @return  java对象字段值
     */
    private static Object convertColumnValueToJavaField(Object columnValue, Field field) {
        if (columnValue == null || field == null) return null;
        if (checkColumn(field.getType())) return columnValue;
        if (!(columnValue instanceof String)) {
            logger.warn("expected value type[{}] for column[{}], actual value type[{}]",
                    String.class.getName(), field.getName(), columnValue.getClass().getName());
            return null;
        }
        Class fieldClass = field.getType();
        if (List.class.isAssignableFrom(fieldClass)) {
            Type fieldType = field.getGenericType();
            if (!(fieldType instanceof ParameterizedType)) return JSON.parseArray((String) columnValue, ArrayList.class);
            Type[] paramTypes = ((ParameterizedType) fieldType).getActualTypeArguments();
            if (paramTypes.length != 1) return JSON.parseObject((String) columnValue, ArrayList.class);
            return JSON.parseObject((String) columnValue, buildListTypeReference((Class) paramTypes[0]));
        } else if (Map.class.isAssignableFrom(fieldClass)) {
            Type fieldType = field.getGenericType();
            if (!(fieldType instanceof ParameterizedType)) return JSON.parseArray((String) columnValue, HashMap.class);
            Type[] paramTypes = ((ParameterizedType) fieldType).getActualTypeArguments();
            if (paramTypes.length != 2) return JSON.parseObject((String) columnValue, HashMap.class);
            return JSON.parseObject((String) columnValue, buildMapTypeReference(
                    (Class) paramTypes[0], (Class) paramTypes[1]));
        }
        return JSON.parseObject((String) columnValue, fieldClass);
    }

    /**
     * 构建List类型TypeReference
     *
     * @param valueClass list值类型
     * @return List类型TypeReference
     * @param <T>
     */
    private static <T> TypeReference buildListTypeReference(Class<T> valueClass) {
        return new TypeReference<ArrayList<T>>(valueClass) {};
    }

    /**
     * 构建Map类型TypeReference
     *
     * @param keyClass map key类型
     * @param valueClass map value类型
     * @return Map类型TypeReference
     * @param <K>
     * @param <V>
     */
    private static <K, V> TypeReference buildMapTypeReference(Class<K> keyClass, Class<V> valueClass) {
        return new TypeReference<HashMap<K, V>>(keyClass, valueClass) {};
    }

    /**
     * 获取列名集合
     *
     * @param row 数据行
     * @return 列名集合
     */
    private static Set<String> getColumnNames(Row row) {
        Set<String> columnNames = new HashSet<>();
        if (row == null) return columnNames;
        Column[] columns = row.getColumns();
        for (Column column : columns) columnNames.add(column.getName());
        return columnNames;
    }

    /**
     * 检测主键值合法性，合法类型：Integer, Long, String, byte数组
     * 不合法抛出RuntimeException
     *
     * @param keyValue 主键值
     */
    private static void checkPrimaryKey(Object keyValue) {
        if (keyValue instanceof String) return;
        if (keyValue instanceof Integer) return;
        if (keyValue instanceof Long) return;
        if (keyValue instanceof byte[]) return;
        if (keyValue == PrimaryKeyValue.INF_MIN) return;
        if (keyValue == PrimaryKeyValue.INF_MAX) return;
        logger.error("unexpected primary key type[{}]", keyValue.getClass().getName());
        throw new RuntimeException(String.format("unexpected primary key type[%s]", keyValue.getClass().getName()));
    }

    /**
     * 检测列值合法性，合法类型：Integer, Long, Float, Double, String, byte数组
     *
     * @param columnValue 列值
     * @return 合法返回true，否则返回false
     */
    private static boolean checkColumn(Object columnValue) {
        if (columnValue instanceof String) return true;
        if (columnValue instanceof Integer) return true;
        if (columnValue instanceof Float) return true;
        if (columnValue instanceof Double) return true;
        if (columnValue instanceof Long) return true;
        if (columnValue instanceof Boolean) return true;
        if (columnValue instanceof byte[]) return true;
        logger.warn("unexpected column value type[{}]", columnValue.getClass().getName());
        return false;
    }

    /**
     * 检测列类型合法性，合法类型：Integer, Long, Float, Double, String, byte数组
     *
     * @param columnClass 列类型
     * @return 合法返回true，否则返回false
     */
    private static boolean checkColumn(Class columnClass) {
        if (columnClass == String.class) return true;
        if (columnClass == Integer.class) return true;
        if (columnClass == Float.class) return true;
        if (columnClass == Double.class) return true;
        if (columnClass == Long.class) return true;
        if (columnClass == Boolean.class) return true;
        if (columnClass.isArray() && columnClass.getName().equals("[B")) return true;
        return false;
    }

    /**
     * 转换PrimaryKeyValue
     *
     * @param primaryKeyValue PrimaryKeyValue实例
     * @return 如果类型非法抛出异常，否则返回主键值
     */
    private static Object convertPrimaryKeyValue(PrimaryKeyValue primaryKeyValue) {
        if (primaryKeyValue.isInfMax() || primaryKeyValue.isInfMin()) return primaryKeyValue;
        PrimaryKeyType primaryKeyType = primaryKeyValue.getType();
        if (primaryKeyType == PrimaryKeyType.BINARY) {
            return primaryKeyValue.asBinary();
        } else if (primaryKeyType == PrimaryKeyType.STRING) {
            return primaryKeyValue.asString();
        } else if (primaryKeyType == PrimaryKeyType.INTEGER) {
            return primaryKeyValue.asLong();
        }
        throw new RuntimeException(String.format("invalid primary key type[{}]", primaryKeyType.name()));
    }

    /**
     * 转换ColumnValue
     *
     * @param columnValue ColumnValue实例
     * @return 如果类型非法抛出异常，否则返回列值
     */
    private static Object convertColumnValue(ColumnValue columnValue) {
        ColumnType columnType = columnValue.getType();
        if (columnType == ColumnType.BINARY) {
            return columnValue.asBinary();
        } else if (columnType == ColumnType.BOOLEAN) {
            return columnValue.asBoolean();
        } else if (columnType == ColumnType.DOUBLE) {
            return columnValue.asDouble();
        } else if (columnType == ColumnType.INTEGER) {
            return columnValue.asLong();
        } else if (columnType == ColumnType.STRING) {
            return columnValue.asString();
        }
        throw new RuntimeException(String.format("invalid column value type[{}]", columnType.name()));
    }
}