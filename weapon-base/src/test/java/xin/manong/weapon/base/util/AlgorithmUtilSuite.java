package xin.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;
import xin.manong.weapon.base.sort.BasicSorter;
import xin.manong.weapon.base.sort.IntComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author frankcl
 * @date 2023-04-26 10:44:13
 */
public class AlgorithmUtilSuite {

    @Test
    public void testComputeMaxCommonSequence() {
        Assert.assertEquals("", AlgorithmUtil.computeMaxCommonSequence(null, "123"));
        Assert.assertEquals("", AlgorithmUtil.computeMaxCommonSequence("null", ""));
        Assert.assertEquals("", AlgorithmUtil.computeMaxCommonSequence("abc", "123"));
        Assert.assertEquals("bc", AlgorithmUtil.computeMaxCommonSequence("abc", "dbca"));
        Assert.assertEquals("bca", AlgorithmUtil.computeMaxCommonSequence("abcda", "dbca"));
        Assert.assertEquals("acda", AlgorithmUtil.computeMaxCommonSequence("abcda", "axcmdya"));
    }

    @Test
    public void testComputeMaxCommonString() {
        Assert.assertEquals("", AlgorithmUtil.computeMaxCommonString(null, "123"));
        Assert.assertEquals("", AlgorithmUtil.computeMaxCommonString("null", ""));
        Assert.assertEquals("", AlgorithmUtil.computeMaxCommonString("abc", "123"));
        Assert.assertEquals("bc", AlgorithmUtil.computeMaxCommonString("abc", "dbca"));
        Assert.assertEquals("deaf", AlgorithmUtil.computeMaxCommonString("abdeafsda", "fdgdeafade"));
        Assert.assertEquals("abdeaf", AlgorithmUtil.computeMaxCommonString("abdeafsda", "abdeafa"));
        Assert.assertEquals("eafa", AlgorithmUtil.computeMaxCommonString("xxfeeafa", "abdeafa"));
    }

    @Test
    public void testBinarySearch() {
        IntComparator comparator = new IntComparator();
        List<Integer> objects = new ArrayList<>();
        objects.add(3);
        objects.add(1);
        objects.add(27);
        objects.add(12);
        objects.add(6);
        objects.add(3);
        BasicSorter.quickSort(objects, comparator);
        Assert.assertEquals(2, AlgorithmUtil.binarySearch(objects, 3, comparator));
        Assert.assertEquals(5, AlgorithmUtil.binarySearch(objects, 27, comparator));
        Assert.assertEquals(-1, AlgorithmUtil.binarySearch(objects, 33, comparator));
        Assert.assertEquals(-1, AlgorithmUtil.binarySearch(objects, 0, comparator));
        Assert.assertEquals(-1, AlgorithmUtil.binarySearch(objects, 16, comparator));
    }
}
