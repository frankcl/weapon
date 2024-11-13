package xin.manong.weapon.base.util;

import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.List;

/**
 * 字节数组工具
 *
 * @author frankcl
 * @date 2019-10-09 16:03:41
 */
public class ByteArrayUtil {

    /**
     * 获取byte数组的二进制字符串表示
     * 位数不足前置补0
     *
     * @param byteArray byte数组
     * @return 二进制字符串
     */
    public static String byteArrayToBinary(byte[] byteArray) {
        StringBuilder buffer = new StringBuilder();
        for (byte b : byteArray) {
            int s = b & 0xff;
            for (int i = 7; i >= 0; i--) {
                buffer.append(((s >>> i) & 0x01) != 0 ? '1' : '0');
            }
        }
        return buffer.toString();
    }

    /**
     * 字节数组转换十六进制字符串
     *
     * @param byteArray 字节数组
     * @return 十六进制字符串
     */
    public static String byteArrayToHex(byte[] byteArray) {
        return Hex.encodeHexString(byteArray);
    }

    /**
     * 十六进制字符串转换字节数组
     *
     * @param hex 十六进制字符串
     * @return 字节数组
     * @throws Exception 异常
     */
    public static byte[] hexToByteArray(String hex) throws Exception {
        return Hex.decodeHex(hex.toCharArray());
    }

    /**
     * byte数组列表转换十六进制字符串列表
     *
     * @param byteArrays byte数组列表
     * @return 十六进制字符串列表
     */
    public static List<String> byteArraysToHex(List<byte[]> byteArrays) {
        List<String> stringList = new ArrayList<>();
        for (byte[] byteArray : byteArrays) stringList.add(byteArrayToHex(byteArray));
        return stringList;
    }

    /**
     * 从目标字节数组中选择num个字节，返回所有组合可能
     *
     * @param byteArray 目标字节数组
     * @param n 选择数量
     * @return 组合结果
     */
    public static List<byte[]> select(byte[] byteArray, int n) {
        return select(byteArray, 0, n);
    }

    /**
     * 从字节数组中选择n个字节，返回所有组合可能
     *
     * @param byteArray 字节数组
     * @param from 起始字节下标
     * @param n 选择数量
     * @return 组合结果
     */
    private static List<byte[]> select(byte[] byteArray, int from, int n) {
        List<byte[]> selectList = new ArrayList<>();
        if (byteArray == null) return selectList;
        if (from < 0 || from >= byteArray.length) return selectList;
        if (n <= 0 || from + n > byteArray.length) return selectList;
        List<byte[]> restSelectList = select(byteArray, from + 1, n - 1);
        if (restSelectList.isEmpty()) {
            byte[] nBytes = new byte[1];
            nBytes[0] = byteArray[from];
            selectList.add(nBytes);
        } else {
            for (byte[] bytes : restSelectList) {
                byte[] nBytes = new byte[bytes.length + 1];
                nBytes[0] = byteArray[from];
                System.arraycopy(bytes, 0, nBytes, 1, bytes.length);
                selectList.add(nBytes);
            }
        }
        selectList.addAll(select(byteArray, from + 1, n));
        return selectList;
    }

    /**
     * 计算字节海明距离
     *
     * @param byte1 字节
     * @param byte2 字节
     * @return 距离
     */
    public static int distance(byte byte1, byte byte2) {
        int count = 0;
        int xor = (byte1 ^ byte2) & 0xFF;
        for (int i = 0; i < 8; i++) {
            if ((xor & 0x01) != 0) count++;
            xor = xor >>> 1;
        }
        return count;
    }

    /**
     * 计算字节数组海明距离
     *
     * @param byteArray1 字节数组
     * @param byteArray2 字节数组
     * @return 距离
     */
    public static int distance(byte[] byteArray1, byte[] byteArray2) {
        if (byteArray1.length != byteArray2.length) {
            throw new IllegalArgumentException(String.format("array length is not consistent[%d vs %d]",
                    byteArray1.length, byteArray2.length));
        }
        int count = 0;
        for (int i = 0; i < byteArray1.length; i++) {
            count += distance(byteArray1[i], byteArray2[i]);
        }
        return count;
    }
}
