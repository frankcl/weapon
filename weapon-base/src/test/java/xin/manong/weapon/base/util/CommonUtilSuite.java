package xin.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author frankcl
 * @create 2020-01-08 15:45:29
 */
public class CommonUtilSuite {

    @Test
    public void testCharacterOccurrence() {
        Assert.assertEquals(0, CommonUtil.characterOccurrence("adfa", 'g'));
        Assert.assertEquals(2, CommonUtil.characterOccurrence("adfa", 'a'));
        Assert.assertEquals(0, CommonUtil.characterOccurrence(null, 'a'));
    }

    @Test
    public void testStringToTime() {
        Assert.assertEquals(1538486588000L, CommonUtil.stringToTime("2018-10-02 21:23:08", null).longValue());
    }

    @Test
    public void testTimeToString() {
        Assert.assertEquals("2020-10-01 00:00:00", CommonUtil.timeToString(1601481600000L, null));
    }

    @Test
    public void testWrongTimeFormat() {
        Assert.assertTrue(null == CommonUtil.stringToTime("test", null));
    }

    @Test
    public void testGetHost() {
        Assert.assertEquals("www.sina.com.cn", CommonUtil.getHost("http://www.sina.com.cn/a"));
    }

    @Test
    public void testGetHostInvalidURL() {
        Assert.assertEquals("", CommonUtil.getHost("abc"));
    }

    @Test
    public void testIsLetter() {
        Assert.assertTrue(CommonUtil.isLetter('c'));
        Assert.assertTrue(CommonUtil.isLetter('X'));
        Assert.assertFalse(CommonUtil.isLetter('1'));
    }

    @Test
    public void testIsDigit() {
        Assert.assertFalse(CommonUtil.isDigit('X'));
        Assert.assertTrue(CommonUtil.isDigit('1'));
    }

    @Test
    public void testIsSpace() {
        Assert.assertTrue(CommonUtil.isSpace(' '));
        Assert.assertTrue(CommonUtil.isSpace('\t'));
        Assert.assertTrue(CommonUtil.isSpace('\n'));
        Assert.assertTrue(CommonUtil.isSpace('\r'));
        Assert.assertFalse(CommonUtil.isSpace('1'));
    }

    @Test
    public void testRound() {
        Assert.assertEquals(2.1d, CommonUtil.round(2.1d, 2), 0.1d);
        Assert.assertEquals(2.11d, CommonUtil.round(2.11333d, 2), 0.01d);
        Assert.assertEquals(2d, CommonUtil.round(2.1333d, 0), 0.01d);
    }

    @Test(expected = RuntimeException.class)
    public void testRoundInvalidN() {
        CommonUtil.round(2.1d, -1);
    }

    @Test
    public void testIsPrime() {
        Assert.assertFalse(CommonUtil.isPrime(-1));
        Assert.assertFalse(CommonUtil.isPrime(0));
        Assert.assertFalse(CommonUtil.isPrime(1));
        Assert.assertFalse(CommonUtil.isPrime(55));
        Assert.assertTrue(CommonUtil.isPrime(2));
        Assert.assertTrue(CommonUtil.isPrime(3));
        Assert.assertTrue(CommonUtil.isPrime(101));
        Assert.assertTrue(CommonUtil.isPrime(19));
    }

    @Test
    public void testFindNextPrime() {
        Assert.assertEquals(2, CommonUtil.findNextPrime(-1));
        Assert.assertEquals(2, CommonUtil.findNextPrime(1));
        Assert.assertEquals(3, CommonUtil.findNextPrime(2));
        Assert.assertEquals(103, CommonUtil.findNextPrime(102));
        Assert.assertEquals(100003, CommonUtil.findNextPrime(99999));
    }

    @Test
    public void testIsIP() {
        Assert.assertFalse(CommonUtil.isIP(null));
        Assert.assertFalse(CommonUtil.isIP(""));
        Assert.assertFalse(CommonUtil.isIP("123.345"));
        Assert.assertFalse(CommonUtil.isIP("www.sina.com.cn"));
        Assert.assertTrue(CommonUtil.isIP("123.345.56.12"));
    }

    @Test
    public void testIsPrimitiveType() {
        Assert.assertTrue(CommonUtil.isPrimitiveType(123));
        Assert.assertTrue(CommonUtil.isPrimitiveType(123L));
        Assert.assertTrue(CommonUtil.isPrimitiveType(new Short((short) 1)));
        Assert.assertTrue(CommonUtil.isPrimitiveType(1.0));
        Assert.assertTrue(CommonUtil.isPrimitiveType(1.0d));
        Assert.assertTrue(CommonUtil.isPrimitiveType(false));
        Assert.assertTrue(CommonUtil.isPrimitiveType("abc"));
        Assert.assertTrue(CommonUtil.isPrimitiveType((byte) 0x1f));
        Assert.assertTrue(CommonUtil.isPrimitiveType('c'));
        Assert.assertFalse(CommonUtil.isPrimitiveType(new Object()));
        Assert.assertFalse(CommonUtil.isPrimitiveType(new HashMap<>()));
        Assert.assertFalse(CommonUtil.isPrimitiveType(null));
    }
}
