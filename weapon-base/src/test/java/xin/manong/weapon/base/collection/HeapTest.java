package xin.manong.weapon.base.collection;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Random;

/**
 * @author frankcl
 * @date 2023-11-02 16:51:50
 */
public class HeapTest {

    private static final Logger logger = LoggerFactory.getLogger(HeapTest.class);

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
        Assert.assertNull(heap.peek());
        Assert.assertNull(heap.poll());
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
            if (v == 12 || v == 2) {
                iterator.remove();
                continue;
            }
            if (prev != null) Assert.assertTrue(prev < v);
            logger.info("entry: {}", v);
            prev = v;
        }
        Assert.assertFalse(heap.isEmpty());
        Assert.assertEquals(5, heap.size());
        Assert.assertEquals(4, heap.peek().intValue());
    }
}
