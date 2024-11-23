package xin.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frankcl
 * @date 2019-10-10 10:59:57
 */
public class MapUtilTest {

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
        Assert.assertNull(MapUtil.getValue(configMap, "k2", String.class));
        Assert.assertNull(MapUtil.getValue(configMap, "k1", Integer.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFlattenMapToMultiMap() {
        Map<String, Object> flattenMap = new HashMap<>();
        flattenMap.put("weapon.aliyun.accessKey", "123");
        flattenMap.put("weapon.aliyun.secretKey", "456");
        flattenMap.put("weapon.list[1]", 123);
        flattenMap.put("weapon.list[0]", 333);
        flattenMap.put("weapon.array[1].id", "frank");
        flattenMap.put("weapon.array[0].id", "lay");
        flattenMap.put("weapon.array[1].name", "chen");
        flattenMap.put("weapon.array[0].name", "li");
        flattenMap.put("weapon.file", "path");
        Map<String, Object> multiMap = MapUtil.flattenMapToMultiMap(flattenMap);
        Assert.assertEquals(1, multiMap.size());

        Map<String, Object> weaponMap = (Map<String, Object>) multiMap.get("weapon");
        Assert.assertEquals(4, weaponMap.size());
        Assert.assertTrue(weaponMap.containsKey("aliyun"));
        Assert.assertTrue(weaponMap.containsKey("list"));
        Assert.assertTrue(weaponMap.containsKey("file"));

        Assert.assertEquals("path", weaponMap.get("file"));

        Map<String, Object> aliyunMap = (Map<String, Object>) weaponMap.get("aliyun");
        Assert.assertEquals(2, aliyunMap.size());
        Assert.assertEquals("123", aliyunMap.get("accessKey"));
        Assert.assertEquals("456", aliyunMap.get("secretKey"));

        List<Integer> list = (List<Integer>) weaponMap.get("list");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(333, list.get(0).intValue());
        Assert.assertEquals(123, list.get(1).intValue());

        List<Map<String, Object>> array = (List<Map<String, Object>>) weaponMap.get("array");
        Assert.assertEquals(2, array.size());
        Assert.assertEquals("li", array.get(0).get("name"));
        Assert.assertEquals("lay", array.get(0).get("id"));
        Assert.assertEquals("chen", array.get(1).get("name"));
        Assert.assertEquals("frank", array.get(1).get("id"));
    }
}
