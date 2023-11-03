package xin.manong.weapon.base.collection;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.Random;

/**
 * @author frankcl
 * @date 2023-10-19 15:29:38
 */
public class SkipListSuite {

    @Test
    public void testCompress() {
        Long totalTime = 0L;
        Random random = new Random();
        SkipList<Integer, Integer> skipList = new SkipList<>(31);
        for (int i = 0; i < 100000; i++) {
            Integer key = random.nextInt(1000000);
            Long startTime = System.currentTimeMillis();
            skipList.add(key, key);
            totalTime += System.currentTimeMillis() - startTime;
        }
        System.out.println("TotalTime: " + totalTime);
        for (Entry<Integer, Integer> entry : skipList) {
            System.out.println(entry);
        }
    }

    @Test
    public void testSkipListOperations() {
        SkipList<String, String> skipList = new SkipList<>();
        Assert.assertTrue(skipList.add("456", "456"));
        Assert.assertTrue(skipList.add("123", "123"));
        Assert.assertTrue(skipList.add("1234", "1234"));
        Assert.assertTrue(skipList.add("7", "7"));
        Assert.assertTrue(skipList.add("3445", "3445"));
        Assert.assertTrue(skipList.add("888", "888"));
        Assert.assertTrue(skipList.get("222") == null);
        Assert.assertFalse(skipList.isEmpty());
        Assert.assertEquals(6, skipList.size());
        Assert.assertEquals("7", skipList.get("7"));
        Assert.assertEquals("123", skipList.getFirst().getKey());
        Assert.assertEquals("123", skipList.getFirst().getValue());
        Assert.assertEquals("888", skipList.getLast().getKey());
        Assert.assertEquals("888", skipList.getLast().getValue());
        Entry<String, String> removeEntry = skipList.removeFirst();
        Assert.assertTrue(removeEntry != null);
        Assert.assertEquals("123", removeEntry.getKey());
        Assert.assertEquals("123", removeEntry.getValue());
        removeEntry = skipList.removeLast();
        Assert.assertTrue(removeEntry != null);
        Assert.assertEquals("888", removeEntry.getKey());
        Assert.assertEquals("888", removeEntry.getValue());
        Assert.assertEquals("1234", skipList.remove("1234"));
        Assert.assertFalse(skipList.isEmpty());
        Assert.assertEquals(3, skipList.size());
        Assert.assertFalse(skipList.add("7", "777"));

        Iterator<Entry<String, String>> iterator = skipList.iterator();
        {
            Assert.assertTrue(iterator.hasNext());
            Entry<String, String> entry = iterator.next();
            Assert.assertEquals("3445", entry.getKey());
            Assert.assertEquals("3445", entry.getValue());
            iterator.remove();
        }
        {
            Assert.assertTrue(iterator.hasNext());
            Entry<String, String> entry = iterator.next();
            Assert.assertEquals("456", entry.getKey());
            Assert.assertEquals("456", entry.getValue());
        }
        {
            Assert.assertTrue(iterator.hasNext());
            Entry<String, String> entry = iterator.next();
            Assert.assertEquals("7", entry.getKey());
            Assert.assertEquals("777", entry.getValue());
        }
        {
            Assert.assertFalse(iterator.hasNext());
        }

        Iterator<Entry<String, String>> reversedIterator = skipList.reversedIterator();
        {
            Assert.assertTrue(reversedIterator.hasNext());
            Entry<String, String> entry = reversedIterator.next();
            Assert.assertEquals("7", entry.getKey());
            Assert.assertEquals("777", entry.getValue());
            reversedIterator.remove();
        }
        {
            Assert.assertTrue(reversedIterator.hasNext());
            Entry<String, String> entry = reversedIterator.next();
            Assert.assertEquals("456", entry.getKey());
            Assert.assertEquals("456", entry.getValue());
        }
        {
            Assert.assertFalse(reversedIterator.hasNext());
        }
    }
}
