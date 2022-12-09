package xin.manong.weapon.base.base.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2022-08-08 11:58:05
 */
public class DomainUtilSuite {

    @Test
    public void testGetDomainByIP() {
        Assert.assertEquals("127.0.0.1", DomainUtil.getDomain("127.0.0.1"));
        Assert.assertEquals("192.168.0.1", DomainUtil.getDomain("192.168.0.1"));
    }

    @Test
    public void testGetDomainNormal() {
        Assert.assertEquals("sina.com.cn", DomainUtil.getDomain("www.sina.com.cn"));
        Assert.assertEquals("sina.com.cn", DomainUtil.getDomain("abc.blog.sina.com.cn"));
        Assert.assertEquals("google.com.hk", DomainUtil.getDomain("www.google.com.hk"));
        Assert.assertEquals("g.cn", DomainUtil.getDomain("www.g.cn"));
        Assert.assertEquals("cmu.edu.us", DomainUtil.getDomain("www.cmu.edu.us"));
        Assert.assertEquals("abc.us", DomainUtil.getDomain("www.abc.us"));
        Assert.assertEquals("geosociety.gd.cn", DomainUtil.getDomain("geosociety.gd.cn"));
        Assert.assertEquals("geosociety.gd.cn", DomainUtil.getDomain("www.geosociety.gd.cn"));
        Assert.assertEquals("cmu.edu", DomainUtil.getDomain("www.cmu.edu"));
        Assert.assertEquals("a.ac.cn", DomainUtil.getDomain("www.a.ac.cn"));
        Assert.assertEquals("gov.hk", DomainUtil.getDomain("www.gov.hk"));
        Assert.assertEquals("immd.gov.hk", DomainUtil.getDomain("www.immd.gov.hk"));
        Assert.assertEquals("immd.gov.hk", DomainUtil.getDomain("immd.gov.hk"));
    }

    @Test
    public void testGetDomainFail() {
        Assert.assertEquals("unknown", DomainUtil.getDomain("unknown"));
        Assert.assertEquals("abc.unknown", DomainUtil.getDomain("abc.unknown"));
        Assert.assertEquals("www.abc.unknown", DomainUtil.getDomain("www.abc.unknown"));
        Assert.assertEquals("", DomainUtil.getDomain(""));
        Assert.assertTrue(null == DomainUtil.getDomain(null));
    }
}
