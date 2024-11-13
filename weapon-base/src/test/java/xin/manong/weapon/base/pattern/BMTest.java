package xin.manong.weapon.base.pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2022-11-19 15:48:31
 */
public class BMTest {

    @Test
    public void testSearch() {
        BM bm = new BM("万岁");
        MatchResult result = bm.search("中华人民共和国万岁，万万岁");
        Assert.assertNotNull(result);
        Assert.assertEquals("万岁", result.pattern);
        Assert.assertEquals(2, result.positions.size());
        Assert.assertEquals(7, result.positions.get(0).intValue());
        Assert.assertEquals(11, result.positions.get(1).intValue());

        bm.rebuild("万万岁");
        result = bm.search("中华人民共和国万岁，万万岁");
        Assert.assertNotNull(result);
        Assert.assertEquals("万万岁", result.pattern);
        Assert.assertEquals(1, result.positions.size());
        Assert.assertEquals(10, result.positions.get(0).intValue());

        result = bm.search("一个好人");
        Assert.assertNull(result);
    }

    @Test
    public void testSpecialSearch() {
        BM bm = new BM("哈哈");
        MatchResult result = bm.search("万岁哈哈哈哈哈");
        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.positions.size());
        Assert.assertEquals(2, result.positions.get(0).intValue());
        Assert.assertEquals(3, result.positions.get(1).intValue());
        Assert.assertEquals(4, result.positions.get(2).intValue());
        Assert.assertEquals(5, result.positions.get(3).intValue());
    }
}
