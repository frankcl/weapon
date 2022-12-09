package xin.manong.weapon.aliyun.ots;

import com.alicloud.openservices.tablestore.model.*;
import org.junit.Assert;
import org.junit.Test;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.RecordType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author frankcl
 * @date 2022-07-25 14:32:54
 */
public class OTSConverterSuite {

    @Test
    public void testConvertPrimaryKeys() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("k1", "abc");
        keyMap.put("k2", 123);
        keyMap.put("k3", PrimaryKeyValue.INF_MAX);
        PrimaryKey primaryKey = OTSConverter.convertPrimaryKey(keyMap);
        Assert.assertTrue(primaryKey != null);
        Assert.assertTrue(primaryKey.getPrimaryKeyColumns().length == 3);
        Assert.assertEquals("k1", primaryKey.getPrimaryKeyColumn("k1").getName());
        Assert.assertEquals("abc", primaryKey.getPrimaryKeyColumn("k1").getValue().asString());
        Assert.assertEquals("k2", primaryKey.getPrimaryKeyColumn("k2").getName());
        Assert.assertEquals(123L, primaryKey.getPrimaryKeyColumn("k2").getValue().asLong());
        Assert.assertEquals("k3", primaryKey.getPrimaryKeyColumn("k3").getName());
        Assert.assertTrue(PrimaryKeyValue.INF_MAX == primaryKey.getPrimaryKeyColumn("k3").getValue());

        keyMap = OTSConverter.convertPrimaryKey(primaryKey);
        Assert.assertTrue(keyMap != null && keyMap.size() == 3);
        Assert.assertTrue(keyMap.containsKey("k1"));
        Assert.assertTrue(keyMap.containsKey("k2"));
        Assert.assertTrue(keyMap.containsKey("k3"));
        Assert.assertEquals("abc", keyMap.get("k1"));
        Assert.assertEquals(123L, keyMap.get("k2"));
        Assert.assertTrue(PrimaryKeyValue.INF_MAX == keyMap.get("k3"));
    }

    @Test
    public void testConvertColumns() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("k1", "abc");
        columnMap.put("k2", 123);
        columnMap.put("k3", new byte[] {0x11, 0x1f});
        columnMap.put("k4", 1.2d);
        columnMap.put("k5", false);
        List<Column> columns = OTSConverter.convertColumns(columnMap);
        Assert.assertEquals(5, columns.size());
        Assert.assertEquals("k1", columns.get(0).getName());
        Assert.assertEquals("abc", columns.get(0).getValue().asString());
        Assert.assertEquals("k2", columns.get(1).getName());
        Assert.assertEquals(123L, columns.get(1).getValue().asLong());
        Assert.assertEquals("k3", columns.get(2).getName());
        Assert.assertEquals(2, columns.get(2).getValue().asBinary().length);
        Assert.assertEquals(0x11, columns.get(2).getValue().asBinary()[0]);
        Assert.assertEquals(0x1f, columns.get(2).getValue().asBinary()[1]);
        Assert.assertEquals("k4", columns.get(3).getName());
        Assert.assertEquals(1.2d, columns.get(3).getValue().asDouble(), 0.1d);
        Assert.assertEquals("k5", columns.get(4).getName());
        Assert.assertEquals(false, columns.get(4).getValue().asBoolean());
    }

    @Test
    public void testConvertRecordColumns() {
        List<RecordColumn> recordColumns = new ArrayList<>();
        {
            Column column = new Column("k1", ColumnValue.fromString("v1"));
            RecordColumn recordColumn = new RecordColumn(column, RecordColumn.ColumnType.PUT);
            recordColumns.add(recordColumn);
        }
        {
            Column column = new Column("k2", ColumnValue.fromString("v2"));
            RecordColumn recordColumn = new RecordColumn(column, RecordColumn.ColumnType.DELETE_ONE_VERSION);
            recordColumns.add(recordColumn);
        }
        {
            Column column = new Column("k3", ColumnValue.fromString("v3"));
            RecordColumn recordColumn = new RecordColumn(column, RecordColumn.ColumnType.DELETE_ALL_VERSION);
            recordColumns.add(recordColumn);
        }
        Map<String, Object> columnMap = OTSConverter.convertRecordColumns(recordColumns);
        Assert.assertTrue(columnMap != null && columnMap.size() == 1);
        Assert.assertTrue(columnMap.containsKey("k1"));
        Assert.assertEquals("v1", columnMap.get("k1"));
    }

    @Test
    public void testConvertRowToKVRecord() {
        long currentTime = System.currentTimeMillis();
        PrimaryKeyBuilder builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        builder.addPrimaryKeyColumn("key", PrimaryKeyValue.fromString("key"));
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("c1", ColumnValue.fromDouble(1.1d), currentTime));
        columns.add(new Column("c2", ColumnValue.fromBoolean(true), currentTime));
        Row row = new Row(builder.build(), columns);
        KVRecord kvRecord = OTSConverter.convertRecord(row);
        Assert.assertEquals(3, kvRecord.getFieldCount());
        Assert.assertTrue(kvRecord.has("key"));
        Assert.assertTrue(kvRecord.has("c1"));
        Assert.assertTrue(kvRecord.has("c2"));
        Assert.assertEquals("key", kvRecord.get("key"));
        Assert.assertEquals(true, ((Boolean) kvRecord.get("c2")));
        Assert.assertEquals(1.1d, ((Double) kvRecord.get("c1")).doubleValue(), 0.1d);
    }

    @Test
    public void testConvertKVRecordToRow() {
        Set<String> keys = new HashSet<>();
        keys.add("key");
        KVRecord kvRecord = new KVRecord();
        kvRecord.put("key", "xyz");
        kvRecord.put("name", "abc");
        kvRecord.put("age", 18L);
        kvRecord.setKeys(keys);
        Row row = OTSConverter.convertRecord(kvRecord);
        Assert.assertEquals(1, row.getPrimaryKey().size());
        Assert.assertEquals("xyz", row.getPrimaryKey().getPrimaryKeyColumn("key").getValue().asString());
        Column[] columns = row.getColumns();
        Assert.assertEquals(2, columns.length);
        Assert.assertEquals("name", columns[1].getName());
        Assert.assertEquals("abc", columns[1].getValue().asString());
        Assert.assertEquals("age", columns[0].getName());
        Assert.assertEquals(18L, columns[0].getValue().asLong());
    }

    @Test
    public void testConvertDeleteStreamRecord() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", "k1");
        PrimaryKey primaryKey = OTSConverter.convertPrimaryKey(keyMap);
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("c1", "v1");
        List<Column> columns = OTSConverter.convertColumns(columnMap);
        List<RecordColumn> recordColumns = columns.stream().map(column -> new RecordColumn(
                column, RecordColumn.ColumnType.PUT)).collect(Collectors.toList());
        StreamRecord streamRecord = new StreamRecord();
        streamRecord.setPrimaryKey(primaryKey);
        streamRecord.setColumns(recordColumns);
        streamRecord.setRecordType(StreamRecord.RecordType.DELETE);
        KVRecord kvRecord = OTSConverter.convertStreamRecord(streamRecord);
        Assert.assertTrue(kvRecord != null);
        Assert.assertEquals(1, kvRecord.getKeys().size());
        Assert.assertTrue(kvRecord.getKeys().contains("key"));
        Assert.assertEquals(1, kvRecord.getFieldCount());
        Assert.assertTrue(kvRecord.has("key"));
        Assert.assertEquals("k1", kvRecord.get("key"));
        Assert.assertEquals(RecordType.DELETE, kvRecord.getRecordType());
    }
    @Test
    public void testConvertStreamRecord() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", "k1");
        PrimaryKey primaryKey = OTSConverter.convertPrimaryKey(keyMap);
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("c1", "v1");
        columnMap.put("c2", 123);
        columnMap.put("c3", 1d);
        columnMap.put("c4", false);
        List<Column> columns = OTSConverter.convertColumns(columnMap);
        List<RecordColumn> recordColumns = columns.stream().map(column -> new RecordColumn(
                column, RecordColumn.ColumnType.PUT)).collect(Collectors.toList());
        StreamRecord streamRecord = new StreamRecord();
        streamRecord.setPrimaryKey(primaryKey);
        streamRecord.setColumns(recordColumns);
        KVRecord kvRecord = OTSConverter.convertStreamRecord(streamRecord);
        Assert.assertTrue(kvRecord != null);
        Assert.assertEquals(1, kvRecord.getKeys().size());
        Assert.assertTrue(kvRecord.getKeys().contains("key"));
        Assert.assertEquals(5, kvRecord.getFieldCount());
        Assert.assertTrue(kvRecord.has("key"));
        Assert.assertTrue(kvRecord.has("c1"));
        Assert.assertTrue(kvRecord.has("c2"));
        Assert.assertTrue(kvRecord.has("c3"));
        Assert.assertTrue(kvRecord.has("c4"));
        Assert.assertEquals("k1", kvRecord.get("key"));
        Assert.assertEquals("v1", kvRecord.get("c1"));
        Assert.assertEquals(123L, kvRecord.get("c2"));
        Assert.assertEquals(1d, kvRecord.get("c3"));
        Assert.assertEquals(false, kvRecord.get("c4"));
        Assert.assertEquals(RecordType.PUT, kvRecord.getRecordType());
    }

    @Test(expected = RuntimeException.class)
    public void testConvertPrimaryKeysException() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("k1", 1.2d);
        OTSConverter.convertPrimaryKey(keyMap);
    }

    @Test(expected = RuntimeException.class)
    public void testConvertColumnsException() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("k1", new HashMap<>());
        OTSConverter.convertColumns(columnMap);
    }
}
