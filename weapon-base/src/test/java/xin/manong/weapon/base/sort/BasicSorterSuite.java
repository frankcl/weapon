package xin.manong.weapon.base.sort;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author frankcl
 * @date 2023-04-25 14:35:03
 */
public class BasicSorterSuite {

    @Test
    public void testHeapSort() {
        List<Integer> objects = new ArrayList<>();
        objects.add(3);
        objects.add(1);
        objects.add(27);
        objects.add(12);
        objects.add(6);
        objects.add(3);
        objects.add(103);
        objects.add(66);
        objects.add(83);
        objects.add(34);
        objects.add(3);
        objects.add(12);
        BasicSorter.heapSort(objects, new IntComparator());
        Assert.assertEquals(12, objects.size());
        Assert.assertEquals(1, objects.get(0).intValue());
        Assert.assertEquals(3, objects.get(1).intValue());
        Assert.assertEquals(3, objects.get(2).intValue());
        Assert.assertEquals(3, objects.get(3).intValue());
        Assert.assertEquals(6, objects.get(4).intValue());
        Assert.assertEquals(12, objects.get(5).intValue());
        Assert.assertEquals(12, objects.get(6).intValue());
        Assert.assertEquals(27, objects.get(7).intValue());
        Assert.assertEquals(34, objects.get(8).intValue());
        Assert.assertEquals(66, objects.get(9).intValue());
        Assert.assertEquals(83, objects.get(10).intValue());
        Assert.assertEquals(103, objects.get(11).intValue());
    }

    @Test
    public void testQuickSort() {
        List<Integer> objects = new ArrayList<>();
        objects.add(3);
        objects.add(1);
        objects.add(27);
        objects.add(12);
        objects.add(6);
        objects.add(3);
        objects.add(103);
        objects.add(66);
        objects.add(83);
        objects.add(34);
        objects.add(3);
        objects.add(12);
        BasicSorter.quickSort(objects, new IntComparator());
        Assert.assertEquals(12, objects.size());
        Assert.assertEquals(1, objects.get(0).intValue());
        Assert.assertEquals(3, objects.get(1).intValue());
        Assert.assertEquals(3, objects.get(2).intValue());
        Assert.assertEquals(3, objects.get(3).intValue());
        Assert.assertEquals(6, objects.get(4).intValue());
        Assert.assertEquals(12, objects.get(5).intValue());
        Assert.assertEquals(12, objects.get(6).intValue());
        Assert.assertEquals(27, objects.get(7).intValue());
        Assert.assertEquals(34, objects.get(8).intValue());
        Assert.assertEquals(66, objects.get(9).intValue());
        Assert.assertEquals(83, objects.get(10).intValue());
        Assert.assertEquals(103, objects.get(11).intValue());
    }
}
