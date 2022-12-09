package xin.manong.weapon.base.base.record;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @create 2019-05-27 20:00
 */
public class KVRecordsSuite {

    @Test
    public void testCopy() {
        KVRecords kvRecords = new KVRecords();
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("k1", "v1");
            kvRecords.addRecord(kvRecord);
        }
        KVRecords replica = kvRecords.copy();
        Assert.assertTrue(replica != null);
        Assert.assertEquals(1, replica.getRecordCount());
        KVRecord kvRecord = replica.getRecord(0);
        Assert.assertEquals(1, kvRecord.getFieldCount());
        Assert.assertEquals("v1", kvRecord.get("k1"));
    }

    @Test
    public void testKVRecordsOperations() {
        KVRecords kvRecords = new KVRecords();
        KVRecords otherKvRecords = new KVRecords();
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("k1", "v1");
            kvRecords.addRecord(kvRecord);
        }
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("k2", "v2");
            kvRecords.addRecord(kvRecord);
        }
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("k3", "v3");
            otherKvRecords.addRecord(kvRecord);
        }
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("k4", "v4");
            otherKvRecords.addRecord(kvRecord);
        }
        kvRecords.addRecords(otherKvRecords);
        kvRecords.toString();
        Assert.assertEquals(4, kvRecords.getRecordCount());
        {
            KVRecord kvRecord = kvRecords.getRecord(0);
            Assert.assertEquals(1, kvRecord.getFieldCount());
            Assert.assertEquals("v1", kvRecord.get("k1"));
        }
        {
            KVRecord kvRecord = kvRecords.getRecord(1);
            Assert.assertEquals(1, kvRecord.getFieldCount());
            Assert.assertEquals("v2", kvRecord.get("k2"));
        }
        {
            KVRecord kvRecord = kvRecords.getRecord(2);
            Assert.assertEquals(1, kvRecord.getFieldCount());
            Assert.assertEquals("v3", kvRecord.get("k3"));
        }
        {
            KVRecord kvRecord = kvRecords.getRecord(3);
            Assert.assertEquals(1, kvRecord.getFieldCount());
            Assert.assertEquals("v4", kvRecord.get("k4"));
        }
        Assert.assertFalse(kvRecords.isEmpty());
        kvRecords.clear();
        Assert.assertTrue(kvRecords.isEmpty());
    }
}
