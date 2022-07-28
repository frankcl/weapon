package com.manong.weapon.base.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author frankcl
 * @date 2022-07-23 11:28:40
 */
public class ContextSuite {

    @Test
    public void testContextOperations() {
        Context context = new Context();
        context.put("k1", "v1");
        context.put("k2", 123);
        Assert.assertEquals("v1", context.get("k1"));
        Assert.assertEquals(123, context.get("k2"));
        Assert.assertTrue(context.contains("k1"));
        Assert.assertTrue(context.contains("k2"));
        Assert.assertFalse(context.contains("k3"));
        context.remove("k1");
        Assert.assertFalse(context.contains("k1"));
        Map<String, Object> featureMap = context.getFeatureMap();
        Assert.assertEquals(1, featureMap.size());
    }
}
