package xin.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frankcl
 * @date 2023-08-04 11:17:32
 */
public class EqualsUtilSuite {

    @Test
    public void testMapEquals() {
        byte[] bytes1 = new byte[2];
        bytes1[0] = (byte) 0xff;
        bytes1[1] = (byte) 0xef;
        byte[] bytes2 = new byte[2];
        bytes2[0] = (byte) 0xff;
        bytes2[1] = (byte) 0xef;
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        Map<String, Object> map3 = new HashMap<>();
        map1.put("1", 1L);
        map1.put("2", "abc");
        map1.put("3", bytes1);
        map2.put("1", 1L);
        map2.put("2", "abc");
        map2.put("3", bytes2);
        map3.put("1", 1L);
        map3.put("2", "abcd");
        map3.put("3", bytes2);
        Assert.assertTrue(EqualsUtil.mapEquals(map1, map2));
        Assert.assertFalse(EqualsUtil.mapEquals(map1, map3));
    }

    @Test
    public void testListEquals() {
        List<Object> list1 = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        List<Object> list3 = new ArrayList<>();
        list1.add("abc");
        list1.add(2.0d);
        list2.add("abc");
        list2.add(new Double(2.0));
        list3.add("abc");
        list3.add(2.0d);
        list3.add(2L);
        Assert.assertTrue(EqualsUtil.listEquals(list1, list2));
        Assert.assertFalse(EqualsUtil.listEquals(list1, list3));
    }

    @Test
    public void testArrayEquals() {
        Object[] array1 = new Object[2];
        Object[] array2 = new Object[2];
        Object[] array3 = new Object[2];
        array1[0] = 1L;
        array1[1] = "abc";
        array2[0] = 1L;
        array2[1] = "abc";
        array3[0] = 1L;
        array3[1] = "abcd";
        Assert.assertTrue(EqualsUtil.arrayEquals(array1, array2));
        Assert.assertFalse(EqualsUtil.arrayEquals(array1, array3));
    }

    @Test
    public void testObjectEquals() {
        Assert.assertTrue(EqualsUtil.objectEquals(1L, 1L));
        Assert.assertFalse(EqualsUtil.objectEquals(1L, 1));
        Assert.assertFalse(EqualsUtil.objectEquals(null, 1));
        Assert.assertTrue(EqualsUtil.objectEquals("abc", new String("abc")));
    }
}
