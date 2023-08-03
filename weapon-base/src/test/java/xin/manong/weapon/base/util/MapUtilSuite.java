package xin.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @create 2019-10-10 10:59:57
 */
public class MapUtilSuite {

    @Test
    public void testEquals() {
        byte[] bytes1 = new byte[2];
        bytes1[0] = (byte) 0xff;
        bytes1[1] = (byte) 0xef;
        byte[] bytes2 = new byte[2];
        bytes2[0] = (byte) 0xff;
        bytes2[1] = (byte) 0xef;
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        Map<String, Object> map3 = new HashMap<>();
        map1.put("1", 1L);
        map1.put("2", "abc");
        map1.put("3", bytes1);

        map2.put("1", 1L);
        map2.put("2", "abc");
        map2.put("3", bytes2);

        map3.put("1", 1L);
        map3.put("2", "abcd");
        map3.put("3", bytes2);

        Assert.assertTrue(MapUtil.equals(map1, map2));
        Assert.assertFalse(MapUtil.equals(map1, map3));
    }

    @Test
    public void testSafeReplace() {
        Map<String, String> targetMap = new HashMap<>();
        targetMap.put("k1", "v1");
        targetMap.put("k2", "v2");
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("k1", "v11");
        replaceMap.put("k3", "v3");
        MapUtil.safeReplace(targetMap, replaceMap);
        Assert.assertEquals(2, targetMap.size());
        Assert.assertTrue(targetMap.containsKey("k1"));
        Assert.assertTrue(targetMap.containsKey("k3"));
        Assert.assertEquals("v11", targetMap.get("k1"));
        Assert.assertEquals("v3", targetMap.get("k3"));
    }

    @Test
    public void testGetValue() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("k1", "v1");
        Assert.assertEquals("v1", MapUtil.getValue(configMap, "k1", String.class));
        Assert.assertTrue(null == MapUtil.getValue(configMap, "k2", String.class));
        Assert.assertTrue(null == MapUtil.getValue(configMap, "k1", Integer.class));
    }
}
