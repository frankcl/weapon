package com.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2022-09-28 15:26:23
 */
public class ShortKeyBuilderSuite {

    @Test
    public void testBuild() {
        Assert.assertEquals("43WpOb", ShortKeyBuilder.build("4bd185d10a8486af6ae6a75b2baa59bf"));
    }
}
