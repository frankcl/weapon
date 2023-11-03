package xin.manong.weapon.base.collection;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @author frankcl
 * @date 2023-10-30 16:18:27
 */
public class BTreeSuite {

    @Test
    public void testCompress() {
        Long totalTime = 0L;
        Random random = new Random();
        BTree<Integer, Integer> bTree = new BTree<>(21);
        for (int i = 0; i < 100000; i++) {
            int v = random.nextInt(1000000);
            Long startTime = System.currentTimeMillis();
            bTree.add(v, v);
            totalTime += System.currentTimeMillis() - startTime;
        }
        System.out.println(bTree.size());
        System.out.println("TotalTime: " + totalTime);
        Entry<Integer, Integer> prevEntry = null;
        Iterator<Entry<Integer, Integer>> iterator = bTree.iterator();
        while (iterator.hasNext()) {
            Entry<Integer, Integer> entry = iterator.next();
            if (prevEntry != null) {
                Assert.assertTrue(prevEntry.getKey().intValue() < entry.getKey().intValue());
                Assert.assertTrue(prevEntry.getValue().intValue() < entry.getValue().intValue());
            }
            prevEntry = entry;
            System.out.println(entry);
        }
    }

    @Test
    public void testBTreeOperations() {
        BTree<Integer, Integer> bTree = new BTree<>(3);
        Assert.assertTrue(bTree.isEmpty());

        Assert.assertTrue(bTree.add(1, -1));
        Assert.assertEquals(-1, bTree.search(1).intValue());
        Assert.assertFalse(bTree.add(1, 1));
        Assert.assertEquals(1, bTree.search(1).intValue());
        Assert.assertTrue(bTree.add(10, 10));
        Assert.assertTrue(bTree.add(5, 5));
        Assert.assertTrue(bTree.add(15, 15));
        Assert.assertTrue(bTree.add(8, 8));
        Assert.assertTrue(bTree.add(12, 12));
        Assert.assertTrue(bTree.add(7, 7));
        Assert.assertTrue(bTree.add(9, 9));

        Assert.assertFalse(bTree.isEmpty());

        Assert.assertTrue(bTree.search(8).intValue() == 8);
        Assert.assertTrue(bTree.search(6) == null);
        List<Integer> values = bTree.search(6, 10);
        Assert.assertEquals(4, values.size());
        Assert.assertEquals(7, values.get(0).intValue());
        Assert.assertEquals(8, values.get(1).intValue());
        Assert.assertEquals(9, values.get(2).intValue());
        Assert.assertEquals(10, values.get(3).intValue());

        Assert.assertEquals(8, bTree.size());
        Entry<Integer, Integer> first = bTree.getFirst();
        Assert.assertEquals(1, first.getKey().intValue());
        Assert.assertEquals(1, first.getValue().intValue());
        Entry<Integer, Integer> last = bTree.getLast();
        Assert.assertEquals(15, last.getKey().intValue());
        Assert.assertEquals(15, last.getValue().intValue());
        Assert.assertEquals(8, bTree.size());

        first = bTree.removeFirst();
        Assert.assertTrue(first != null);
        Assert.assertEquals(1, first.getKey().intValue());
        Assert.assertEquals(1, first.getValue().intValue());
        Assert.assertEquals(7, bTree.size());

        last = bTree.removeLast();
        Assert.assertTrue(last != null);
        Assert.assertEquals(15, last.getKey().intValue());
        Assert.assertEquals(15, last.getValue().intValue());
        Assert.assertEquals(6, bTree.size());

        Iterator<Entry<Integer, Integer>> iterator = bTree.iterator();
        System.out.println("iterator: ");
        while (iterator.hasNext()) {
            Entry<Integer, Integer> entry = iterator.next();
            if (entry.getKey().intValue() == 8) {
                iterator.remove();
                continue;
            }
            System.out.print(entry + " ");
        }
        System.out.println();

        Iterator<Entry<Integer, Integer>> reversedIterator = bTree.reversedIterator();
        System.out.println("reversed iterator: ");
        while (reversedIterator.hasNext()) {
            Entry<Integer, Integer> entry = reversedIterator.next();
            if (entry.getKey().intValue() == 9) {
                reversedIterator.remove();
                continue;
            }
            System.out.print(entry + " ");
        }
        System.out.println();

        Assert.assertTrue(bTree.add(1, 1));
        Assert.assertTrue(bTree.add(15, 15));

        Assert.assertEquals(15, bTree.remove(15).intValue());
        Assert.assertEquals(7, bTree.remove(7).intValue());
        Assert.assertEquals(1, bTree.remove(1).intValue());
        Assert.assertEquals(5, bTree.remove(5).intValue());
        Assert.assertEquals(10, bTree.remove(10).intValue());
        Assert.assertEquals(12, bTree.remove(12).intValue());
        Assert.assertEquals(0, bTree.size());
        Assert.assertTrue(bTree.isEmpty());
        iterator = bTree.iterator();
        Assert.assertFalse(iterator.hasNext());
        Assert.assertTrue(iterator.next() == null);
        reversedIterator = bTree.reversedIterator();
        Assert.assertFalse(reversedIterator.hasNext());
        Assert.assertTrue(reversedIterator.next() == null);
    }
}
