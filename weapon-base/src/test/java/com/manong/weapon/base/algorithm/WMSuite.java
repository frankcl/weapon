package com.manong.weapon.base.algorithm;

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
        Assert.assertTrue(matchResults.size() == 5);
        matchResults = wm.search("江西省会不是景德镇，景德镇产瓷器，瓷器英文名是china!!!");
        Assert.assertTrue(matchResults.size() == 4);
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
        Assert.assertTrue(matchResults.size() == 4);

        patterns = new ArrayList<>();
        patterns.add("江西");
        patterns.add("省会");
        patterns.add("会不是");
        wm.rebuild(patterns);
        matchResults = wm.search("江西省会不是景德镇，景德镇产瓷器，瓷器英文名是china!!!");
        Assert.assertTrue(matchResults.size() == 3);
    }
}
