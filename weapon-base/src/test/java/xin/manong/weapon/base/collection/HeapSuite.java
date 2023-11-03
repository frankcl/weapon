package xin.manong.weapon.base.collection;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.Random;

/**
 * @author frankcl
 * @date 2023-11-02 16:51:50
 */
public class HeapSuite {

    @Test
    public void testCompress() {
        Random random = new Random();
        Heap<Integer> heap = new Heap<>();
        for (int i = 0; i < 100000; i++) {
            int v = random.nextInt(1000000);
            heap.add(v);
        }
        Assert.assertFalse(heap.isEmpty());
        Assert.assertEquals(100000, heap.size());
        Integer prev = null;
        while (!heap.isEmpty()) {
            Integer v = heap.poll();
            if (prev != null) Assert.assertTrue(prev <= v);
            prev = v;
        }
        Assert.assertTrue(heap.isEmpty());
    }

    @Test
    public void testHeapOperations() {
        Heap<Integer> heap = new Heap<>();
        heap.add(15);
        heap.add(3);
        heap.add(16);
        heap.add(30);
        heap.add(8);
        heap.add(20);
        heap.add(1);
        Assert.assertFalse(heap.isEmpty());
        Assert.assertEquals(7, heap.size());
        Assert.assertEquals(1, heap.peek().intValue());
        Assert.assertEquals(1, heap.poll().intValue());
        Assert.assertEquals(3, heap.poll().intValue());
        Assert.assertFalse(heap.isEmpty());
        Assert.assertEquals(5, heap.size());
        Assert.assertEquals(8, heap.poll().intValue());
        Assert.assertEquals(15, heap.poll().intValue());
        Assert.assertEquals(16, heap.poll().intValue());
        Assert.assertEquals(20, heap.poll().intValue());
        Assert.assertEquals(30, heap.poll().intValue());
        Assert.assertTrue(heap.isEmpty());
        Assert.assertEquals(0, heap.size());
        Assert.assertTrue(heap.peek() == null);
        Assert.assertTrue(heap.poll() == null);
        Assert.assertTrue(heap.isEmpty());
        Assert.assertEquals(0, heap.size());

        heap.add(45);
        heap.add(50);
        heap.add(4);
        heap.add(12);
        heap.add(33);
        heap.add(2);
        heap.add(21);
        Assert.assertFalse(heap.isEmpty());
        Assert.assertEquals(7, heap.size());
        Integer prev = null;
        Iterator<Integer> iterator = heap.iterator();
        while (iterator.hasNext()) {
            Integer v = iterator.next();
            if (v.intValue() == 12 || v.intValue() == 2) {
                iterator.remove();
                continue;
            }
            if (prev != null) Assert.assertTrue(prev < v);
            System.out.println(v + " ");
            prev = v;
        }
        Assert.assertFalse(heap.isEmpty());
        Assert.assertEquals(5, heap.size());
        Assert.assertEquals(4, heap.peek().intValue());
    }
}
