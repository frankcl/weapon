package xin.manong.weapon.base.base.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author frankcl
 * @create 2019-10-10 10:56:05
 */
public class ByteArrayUtilSuite {

    @Test
    public void testByteArrayToBinString() {
        byte[] byteArray = new byte[2];
        byteArray[0] = (byte) 0xff;
        byteArray[1] = (byte) 0xf3;
        Assert.assertEquals("1111111111110011", ByteArrayUtil.byteArrayToBinString(byteArray));
    }

    @Test
    public void testByteArraysToHexStrings() throws Exception {
        List<byte[]> byteArrays = new ArrayList<>();
        {
            byte[] byteArray = new byte[2];
            byteArray[0] = (byte) 0xff;
            byteArray[1] = (byte) 0xf3;
            byteArrays.add(byteArray);
        }
        {
            byte[] byteArray = new byte[2];
            byteArray[0] = (byte) 0x23;
            byteArray[1] = (byte) 0x85;
            byteArrays.add(byteArray);
        }
        List<String> strings = ByteArrayUtil.byteArraysToHexStrings(byteArrays);
        Assert.assertEquals(2, strings.size());
        Assert.assertEquals("fff3", strings.get(0));
        Assert.assertEquals("2385", strings.get(1));
    }

    @Test
    public void testHexStringByteArray() throws Exception {
        byte[] hash = new byte[2];
        hash[0] = (byte) 0xff;
        hash[1] = (byte) 0x32;
        String str = ByteArrayUtil.byteArrayToHexString(hash);
        Assert.assertEquals("ff32", str);
        byte[] bytes = ByteArrayUtil.hexStringToByteArray(str);
        Assert.assertEquals(bytes[0], hash[0]);
        Assert.assertEquals(bytes[1], hash[1]);
    }

    @Test
    public void testSelect() {
        {
            byte[] byteArray = new byte[4];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (i + 1);
            }
            List<byte[]> selectList = ByteArrayUtil.select(byteArray, 2);
            Assert.assertEquals(6, selectList.size());
            Assert.assertEquals(1, selectList.get(0)[0]);
            Assert.assertEquals(2, selectList.get(0)[1]);
            Assert.assertEquals(1, selectList.get(1)[0]);
            Assert.assertEquals(3, selectList.get(1)[1]);
            Assert.assertEquals(1, selectList.get(2)[0]);
            Assert.assertEquals(4, selectList.get(2)[1]);
            Assert.assertEquals(2, selectList.get(3)[0]);
            Assert.assertEquals(3, selectList.get(3)[1]);
            Assert.assertEquals(2, selectList.get(4)[0]);
            Assert.assertEquals(4, selectList.get(4)[1]);
            Assert.assertEquals(3, selectList.get(5)[0]);
            Assert.assertEquals(4, selectList.get(5)[1]);
            Assert.assertEquals(0, ByteArrayUtil.select(byteArray, 5).size());
            Assert.assertEquals(0, ByteArrayUtil.select(byteArray, 0).size());
            Assert.assertEquals(0, ByteArrayUtil.select(byteArray, -1).size());
        }
        {
            byte[] byteArray = new byte[8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (i + 1);
            }
            List<byte[]> selectList = ByteArrayUtil.select(byteArray, 3);
            Assert.assertEquals(56, selectList.size());
        }
    }

    @Test
    public void testDistance() {
        byte[] byteArray1 = new byte[2];
        byteArray1[0] = (byte) 0xff;
        byteArray1[1] = (byte) 0x0f;
        byte[] byteArray2 = new byte[2];
        byteArray2[0] = (byte) 0xef;
        byteArray2[1] = (byte) 0x13;
        Assert.assertEquals(4, ByteArrayUtil.distance(byteArray1, byteArray2));
    }
}
