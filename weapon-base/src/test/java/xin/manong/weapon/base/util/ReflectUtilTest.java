package xin.manong.weapon.base.util;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-07-22 12:05:46
 */
@SuppressWarnings("unchecked")
public class ReflectUtilTest {

    @Test
    public void testFieldOperation() {
        JSONObject object = new JSONObject();
        Map<String, Object> map = new HashMap<>();
        map.put("key", "abc");
        String string = "test";
        ReflectUtil.setFieldValue(object, "map", map);
        Map<String, Object> jsonMap = (Map<String, Object>) ReflectUtil.getFieldValue(object, "map");
        Assert.assertNotNull(map);
        Assert.assertTrue(jsonMap == map);
        Assert.assertEquals("abc", jsonMap.get("key"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInvokeMethod() {
        Map<String, Object> map = (Map<String, Object>) ReflectUtil.newInstance(
                "java.util.HashMap", new ReflectArgs());
        ReflectArgs reflectParams = new ReflectArgs(new Class[]
                {Object.class, Object.class}, new Object[] {"key", 123});
        Object object = ReflectUtil.invoke("put", map, reflectParams);
        Assert.assertNull(object);
        Boolean isEmpty = (Boolean) ReflectUtil.invoke("isEmpty", map, new ReflectArgs());
        Assert.assertFalse(isEmpty);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testConstructorWithParams() {
        ReflectArgs reflectParams = new ReflectArgs(new Class[]{int.class}, new Object[]{36});
        Map map = (Map) ReflectUtil.newInstance("java.util.HashMap", reflectParams);
        Assert.assertNotNull(map);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testConstructorWithoutParams() {
        Map map = (Map) ReflectUtil.newInstance("java.util.HashMap", null);
        Assert.assertNotNull(map);
    }

    @Test
    public void testGetFields() {
        {
            String str = "abc";
            Field[] fields = ReflectUtil.getFields(str);
            Assert.assertEquals(11, fields.length);
        }
        {
            Object object = new Object();
            Field[] fields = ReflectUtil.getFields(object);
            Assert.assertEquals(0, fields.length);
        }
        {
            Field[] fields = ReflectUtil.getFields(null);
            Assert.assertEquals(0, fields.length);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testGetNoSuchField() {
        String string = "test";
        ReflectUtil.getFieldValue(string, "unknown");
    }

    @Test(expected = RuntimeException.class)
    public void testSetNoSuchField() {
        String string = "test";
        ReflectUtil.setFieldValue(string, "unknown", "v");
    }

    @Test(expected = RuntimeException.class)
    public void testInvokeNoSuchMethod() {
        String string = "test";
        ReflectUtil.invoke("unknown", string, null);
    }

    @Test(expected = RuntimeException.class)
    public void testInvokeErrorParams() {
        String string = "test";
        ReflectArgs reflectParams = new ReflectArgs(new Class[]{int.class}, new Object[]{"1"});
        ReflectUtil.invoke("charAt", string, reflectParams);
    }

    @Test(expected = RuntimeException.class)
    public void testNoSuchClass() {
        ReflectUtil.newInstance("unknown", null);
    }

    @Test(expected = RuntimeException.class)
    public void testConstructorErrorParams() {
        ReflectArgs reflectParams = new ReflectArgs(new Class[]{String.class}, new Object[]{36});
        ReflectUtil.newInstance(String.class, reflectParams);
    }
}
