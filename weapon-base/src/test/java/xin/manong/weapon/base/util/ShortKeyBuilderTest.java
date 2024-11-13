package xin.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2022-09-28 15:26:23
 */
public class ShortKeyBuilderTest {

    @Test
    public void testBuild() {
        Assert.assertEquals("43WpOb1XL80K", ShortKeyBuilder.build("4bd185d10a8486af6ae6a75b2baa59bf"));
        Assert.assertEquals("JdeaWbpYIOXO", ShortKeyBuilder.build("eb32d97e2fc345b7806174648754898e"));
        Assert.assertEquals("JdeaWbNOfYL2", ShortKeyBuilder.build("7a615520bb4ffcf52c08a39327258f87"));
    }
}
