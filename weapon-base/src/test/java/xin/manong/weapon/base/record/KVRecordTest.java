package xin.manong.weapon.base.record;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author frankcl
 * @date 2019-05-27 19:49
 */
public class KVRecordTest {

    @Test
    public void testKVRecordOperations() {
        Set<String> keys = new HashSet<>();
        keys.add("k1");
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("k1", "v1");
        fieldMap.put("k2", 1L);
        fieldMap.put("k3", 100);
        fieldMap.put("k4", true);
        KVRecord kvRecord = new KVRecord(keys, fieldMap);
        Assert.assertFalse(kvRecord.isEmpty());
        Assert.assertEquals(4, kvRecord.getFieldCount());
        Assert.assertTrue(kvRecord.has("k1"));
        Assert.assertEquals("v1", kvRecord.get("k1"));
        Assert.assertTrue(kvRecord.has("k2"));
        Assert.assertEquals(1L, ((Long) kvRecord.get("k2")).longValue());
        Assert.assertTrue(kvRecord.has("k3"));
        Assert.assertEquals(100, ((Integer) kvRecord.get("k3")).intValue());
        Assert.assertTrue(kvRecord.has("k4"));
        Assert.assertTrue((Boolean) kvRecord.get("k4"));
        Assert.assertFalse(kvRecord.has("k5"));
        Assert.assertTrue(kvRecord.remove("k1"));
        Assert.assertFalse(kvRecord.remove("k5"));
        kvRecord.clear();
        Assert.assertEquals(0, kvRecord.getFieldCount());
        Assert.assertTrue(kvRecord.isEmpty());
        Assert.assertTrue(kvRecord.getKeys().isEmpty());
    }

    @Test
    public void testCopy() {
        Set<String> keys = new HashSet<>();
        keys.add("k1");
        KVRecord kvRecord = new KVRecord();
        kvRecord.put("k1", "v1");
        kvRecord.put("k2", 1L);
        kvRecord.setKeys(keys);
        KVRecord replica = kvRecord.copy();
        Assert.assertNotNull(replica);
        Assert.assertEquals(2, replica.getFieldCount());
        Assert.assertEquals("v1", replica.get("k1"));
        Assert.assertEquals(1L, ((Long) replica.get("k2")).longValue());
        Assert.assertEquals(1, kvRecord.getKeys().size());
        Assert.assertTrue(kvRecord.getKeys().contains("k1"));
    }

    @Test
    public void testGet() {
        KVRecord kvRecord = new KVRecord();
        kvRecord.put("k1", "abc");
        kvRecord.put("k2", 1L);
        Assert.assertEquals("abc", kvRecord.get("k1", String.class));
        Assert.assertEquals(1L, kvRecord.get("k2", Long.class).longValue());
        Assert.assertNull(kvRecord.get("k2", String.class));
    }
}
