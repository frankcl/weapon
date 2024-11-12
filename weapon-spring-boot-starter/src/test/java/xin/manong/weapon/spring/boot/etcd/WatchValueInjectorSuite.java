package xin.manong.weapon.spring.boot.etcd;

import org.junit.Assert;
import org.junit.Test;
import xin.manong.weapon.base.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author frankcl
 * @date 2024-11-12 16:28:20
 */
public class WatchValueInjectorSuite {

    private static class A {
        public String nickName;
    }
    private static class Record {
        @WatchValue(namespace = "test", key = "key")
        public String key;
        @WatchValue(namespace = "test", key = "map")
        public Map<String, Long> map;
        @WatchValue(namespace = "test", key = "a")
        public A obj;
    }

    @Test
    public void testInject() throws NoSuchFieldException {
        Record record = new Record();
        Field keyField = ReflectUtil.getField(record, "key");
        WatchValueInjector.inject(record, keyField, "test");
        Field mapField = ReflectUtil.getField(record, "map");
        WatchValueInjector.inject(record, mapField, "{\"k1\": 1, \"k2\": 2}");
        Field objField = ReflectUtil.getField(record, "obj");
        WatchValueInjector.inject(record, objField, "{\"nickName\": \"frankcl\"}");

        Assert.assertEquals("test", record.key);
        Assert.assertNotNull(record.map);
        Assert.assertEquals(2, record.map.size());
        Assert.assertEquals(1L, record.map.get("k1").longValue());
        Assert.assertEquals(2L, record.map.get("k2").longValue());
        Assert.assertNotNull(record.obj);
        Assert.assertEquals("frankcl", record.obj.nickName);
    }
}
