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
