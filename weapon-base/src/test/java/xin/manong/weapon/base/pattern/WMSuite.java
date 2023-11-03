package xin.manong.weapon.base.pattern;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author frankcl
 * @date 2022-11-12 16:40:32
 */
public class WMSuite {

    @Test
    public void testSearch() {
        List<String> patterns = new ArrayList<>();
        patterns.add("英文名");
        patterns.add("景德镇");
        patterns.add("china");
        WM wm = new WM(patterns);
        List<MatchResult> matchResults = wm.search("中国人说中文，爱中国，中国的英文名是china，瓷器的英文名也是china，景德镇产瓷器");
        Assert.assertTrue(matchResults.size() == 3);
        Assert.assertEquals("英文名", matchResults.get(0).pattern);
        Assert.assertTrue(matchResults.get(0).positions.size() == 2);
        Assert.assertEquals("china", matchResults.get(2).pattern);
        Assert.assertTrue(matchResults.get(2).positions.size() == 2);
        Assert.assertEquals("景德镇", matchResults.get(1).pattern);
        Assert.assertTrue(matchResults.get(1).positions.size() == 1);
        matchResults = wm.search("江西省会不是景德镇，景德镇产瓷器，瓷器英文名是china!!!");
        Assert.assertTrue(matchResults.size() == 3);
        Assert.assertEquals("景德镇", matchResults.get(1).pattern);
        Assert.assertTrue(matchResults.get(1).positions.size() == 2);
        Assert.assertEquals("英文名", matchResults.get(0).pattern);
        Assert.assertTrue(matchResults.get(0).positions.size() == 1);
        Assert.assertEquals("china", matchResults.get(2).pattern);
        Assert.assertTrue(matchResults.get(2).positions.size() == 1);
    }

    @Test
    public void testSearchSpecial() {
        List<String> patterns = new ArrayList<>();
        patterns.add("哈");
        patterns.add("哈哈");
        patterns.add("哈哈嘿");
        WM wm = new WM(patterns, 3);
        List<MatchResult> matchResults = wm.search("哈哈哈哈哈哈哈哈哈");
        Assert.assertTrue(matchResults.size() == 2);
        Assert.assertEquals("哈", matchResults.get(0).pattern);
        Assert.assertTrue(matchResults.get(0).positions.size() == 9);
        Assert.assertEquals("哈哈", matchResults.get(1).pattern);
        Assert.assertTrue(matchResults.get(1).positions.size() == 8);
    }

    @Test
    public void testSearchNotMatch() {
        List<String> patterns = new ArrayList<>();
        patterns.add("英文名");
        patterns.add("景德镇");
        patterns.add("china");
        WM wm = new WM(patterns);
        List<MatchResult> matchResults = wm.search("中国");
        Assert.assertTrue(matchResults.size() == 0);
    }

    @Test
    public void testRebuild() {
        List<String> patterns = new ArrayList<>();
        patterns.add("英文名");
        patterns.add("景德镇");
        patterns.add("china");
        WM wm = new WM(patterns);
        List<MatchResult> matchResults = wm.search("江西省会不是景德镇，景德镇产瓷器，瓷器英文名是china!!!");
        Assert.assertTrue(matchResults.size() == 3);
        Assert.assertEquals("英文名", matchResults.get(0).pattern);
        Assert.assertTrue(matchResults.get(0).positions.size() == 1);
        Assert.assertEquals("china", matchResults.get(2).pattern);
        Assert.assertTrue(matchResults.get(2).positions.size() == 1);
        Assert.assertEquals("景德镇", matchResults.get(1).pattern);
        Assert.assertTrue(matchResults.get(1).positions.size() == 2);

        patterns = new ArrayList<>();
        patterns.add("江西");
        patterns.add("省会");
        patterns.add("会不是");
        wm.rebuild(patterns);
        matchResults = wm.search("江西省会不是景德镇，景德镇产瓷器，瓷器英文名是china!!!");
        Assert.assertTrue(matchResults.size() == 3);
        Assert.assertEquals("江西", matchResults.get(0).pattern);
        Assert.assertTrue(matchResults.get(0).positions.size() == 1);
        Assert.assertEquals("省会", matchResults.get(1).pattern);
        Assert.assertTrue(matchResults.get(1).positions.size() == 1);
        Assert.assertEquals("会不是", matchResults.get(2).pattern);
        Assert.assertTrue(matchResults.get(2).positions.size() == 1);
    }
}
