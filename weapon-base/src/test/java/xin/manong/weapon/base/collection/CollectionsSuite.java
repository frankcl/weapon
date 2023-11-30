package xin.manong.weapon.base.collection;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author frankcl
 * @date 2023-04-25 14:35:03
 */
public class CollectionsSuite {

    @Test
    public void testBinarySearch() {
        List<Integer> elements = createElements();
        Collections.sortQuick(elements, null);
        Assert.assertEquals(1, Collections.binarySearch(elements, 3, null));
        Assert.assertEquals(0, Collections.binarySearch(elements, 1, null));
        Assert.assertEquals(4, Collections.binarySearch(elements, 6, null));
        Assert.assertEquals(11, Collections.binarySearch(elements, 103, null));
        Assert.assertEquals(-1, Collections.binarySearch(elements, 25, null));
        Assert.assertEquals(-1, Collections.binarySearch(elements, 10, null));
    }

    @Test
    public void testSortHeap() {
        List<Integer> elements = createElements();
        Collections.sortHeap(elements, null);
        Assert.assertEquals(12, elements.size());
        Assert.assertEquals(1, elements.get(0).intValue());
        Assert.assertEquals(3, elements.get(1).intValue());
        Assert.assertEquals(3, elements.get(2).intValue());
        Assert.assertEquals(3, elements.get(3).intValue());
        Assert.assertEquals(6, elements.get(4).intValue());
        Assert.assertEquals(12, elements.get(5).intValue());
        Assert.assertEquals(12, elements.get(6).intValue());
        Assert.assertEquals(27, elements.get(7).intValue());
        Assert.assertEquals(34, elements.get(8).intValue());
        Assert.assertEquals(66, elements.get(9).intValue());
        Assert.assertEquals(83, elements.get(10).intValue());
        Assert.assertEquals(103, elements.get(11).intValue());
    }

    @Test
    public void testSortQuick() {
        List<Integer> elements = createElements();
        Collections.sortQuick(elements, null);
        Assert.assertEquals(12, elements.size());
        Assert.assertEquals(1, elements.get(0).intValue());
        Assert.assertEquals(3, elements.get(1).intValue());
        Assert.assertEquals(3, elements.get(2).intValue());
        Assert.assertEquals(3, elements.get(3).intValue());
        Assert.assertEquals(6, elements.get(4).intValue());
        Assert.assertEquals(12, elements.get(5).intValue());
        Assert.assertEquals(12, elements.get(6).intValue());
        Assert.assertEquals(27, elements.get(7).intValue());
        Assert.assertEquals(34, elements.get(8).intValue());
        Assert.assertEquals(66, elements.get(9).intValue());
        Assert.assertEquals(83, elements.get(10).intValue());
        Assert.assertEquals(103, elements.get(11).intValue());
    }

    @Test
    public void testCombination() {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        Assert.assertEquals(0, Collections.combination(numbers, 0).size());
        Assert.assertEquals(4, Collections.combination(numbers, 1).size());
        Assert.assertEquals(6, Collections.combination(numbers, 2).size());
        Assert.assertEquals(4, Collections.combination(numbers, 3).size());
        Assert.assertEquals(1, Collections.combination(numbers, 4).size());
        Assert.assertEquals(0, Collections.combination(numbers, 5).size());
    }

    @Test
    public void testPermutation() {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        Assert.assertEquals(0, Collections.permutation(numbers, 0).size());
        Assert.assertEquals(4, Collections.permutation(numbers, 1).size());
        Assert.assertEquals(12, Collections.permutation(numbers, 2).size());
        Assert.assertEquals(24, Collections.permutation(numbers, 3).size());
        Assert.assertEquals(24, Collections.permutation(numbers, 4).size());
        Assert.assertEquals(0, Collections.permutation(numbers, 5).size());
    }

    private List<Integer> createElements() {
        List<Integer> elements = new ArrayList<>();
        elements.add(3);
        elements.add(1);
        elements.add(27);
        elements.add(12);
        elements.add(6);
        elements.add(3);
        elements.add(103);
        elements.add(66);
        elements.add(83);
        elements.add(34);
        elements.add(3);
        elements.add(12);
        return elements;
    }
}
