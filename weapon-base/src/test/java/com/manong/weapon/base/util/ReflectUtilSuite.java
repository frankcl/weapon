package com.manong.weapon.base.util;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author frankcl
 * @date 2022-07-22 12:05:46
 */
public class ReflectUtilSuite {

    @Test
    public void testFieldOperation() {
        JSONObject object = new JSONObject();
        Map<String, Object> map = (Map<String, Object>) ReflectUtil.getFieldValue(object, "map");
        Assert.assertTrue(map != null);

        String string = "test";
        ReflectUtil.setFieldValue(string, "hash", 10086);
        int hash = (int) ReflectUtil.getFieldValue(string, "hash");
        Assert.assertEquals(10086, hash);
    }

    @Test
    public void testInvokeMethod() {
        Map<String, Object> map = (Map<String, Object>) ReflectUtil.newInstance(
                "java.util.HashMap", new ReflectParams());
        ReflectParams reflectParams = new ReflectParams(new Class[]
                {Object.class, Object.class}, new Object[] {"key", 123});
        Object object = ReflectUtil.invoke("put", map, reflectParams);
        Assert.assertTrue(object == null);
        Boolean isEmpty = (Boolean) ReflectUtil.invoke("isEmpty", map, new ReflectParams());
        Assert.assertFalse(isEmpty);
    }

    @Test
    public void testConstructorWithParams() {
        ReflectParams reflectParams = new ReflectParams(new Class[]{int.class}, new Object[]{36});
        Map map = (Map) ReflectUtil.newInstance("java.util.HashMap", reflectParams);
        Assert.assertTrue(map != null);
    }

    @Test
    public void testConstructorWithoutParams() {
        Map map = (Map) ReflectUtil.newInstance("java.util.HashMap", null);
        Assert.assertTrue(map != null);
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
        ReflectParams reflectParams = new ReflectParams(new Class[]{int.class}, new Object[]{"1"});
        ReflectUtil.invoke("charAt", string, reflectParams);
    }

    @Test(expected = RuntimeException.class)
    public void testNoSuchClass() {
        ReflectUtil.newInstance("unknown", null);
    }

    @Test(expected = RuntimeException.class)
    public void testConstructorErrorParams() {
        ReflectParams reflectParams = new ReflectParams(new Class[]{String.class}, new Object[]{36});
        ReflectUtil.newInstance(String.class, reflectParams);
    }
}
