package xin.manong.weapon.aliyun.ots;

import com.alibaba.fastjson.JSON;
import com.alicloud.openservices.tablestore.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.RecordType;

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
        for (String name : row.getColumnsMap().keySet()) {
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
            else columnMap.put(key, value instanceof JSON ? value.toString() : value);
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