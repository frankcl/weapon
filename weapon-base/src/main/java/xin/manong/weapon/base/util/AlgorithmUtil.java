package xin.manong.weapon.base.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

/**
 * 算法工具
 *
 * @author frankcl
 * @date 2023-04-26 10:28:28
 */
public class AlgorithmUtil {

    private static final Logger logger = LoggerFactory.getLogger(AlgorithmUtil.class);

    /**
     * 计算最大回文子串
     *
     * @param s 输入字符串
     * @return 如果存在返回最大回文子串，否则返回空字符串
     */
    public static String computeMaxPalindromeString(String s) {
        if (StringUtils.isEmpty(s)) return "";
        int maxLen = 0, m = 0, n = 0;
        boolean[][] matrix = new boolean[s.length()][s.length()];
        for (int i = 0; i < s.length(); i++) matrix[i][i] = true;
        for (int len = 2; len <= s.length(); len++) {
            for (int i = 0; i < s.length(); i++) {
                int j = i + len - 1;
                if (j >= s.length()) continue;
                int u = i + 1, v = j - 1;
                if (!(s.charAt(i) == s.charAt(j) && (u >= v || (u < v && matrix[u][v])))) continue;
                matrix[i][j] = true;
                if (j - i + 1 <= maxLen) continue;
                maxLen = j - i + 1;
                m = i; n = j;
            }
        }
        if (maxLen <= 0) return "";
        return s.substring(m, n + 1);
    }

    /**
     * 计算最长公共子串
     *
     * @param s1 输入字符串
     * @param s2 输入字符串
     * @return 如果存在返回公共子串，否则返回空字符串
     */
    public static String computeMaxCommonString(String s1, String s2) {
        if (StringUtils.isEmpty(s1) || StringUtils.isEmpty(s2)) return "";
        int pos = -1, maxLen = 0;
        int[][] distance = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) != s2.charAt(j - 1)) continue;
                distance[i][j] = distance[i - 1][j - 1] + 1;
                if (distance[i][j] <= maxLen) continue;
                maxLen = distance[i][j];
                pos = i;
            }
        }
        if (maxLen == 0) return "";
        return s1.substring(pos - maxLen, pos);
    }

    /**
     * 计算最大公共子序列
     *
     * @param s1 输入字符串
     * @param s2 输入字符串
     * @return 如果存在返回最大公共子序列，否则返回空字符串
     */
    public static String computeMaxCommonSequence(String s1, String s2) {
        if (StringUtils.isEmpty(s1) || StringUtils.isEmpty(s2)) return "";
        int m = 0, n = 0, maxLen = 0;
        int[][] distance = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                    distance[i][j] = Math.max(distance[i - 1][j], distance[i][j - 1]);
                    continue;
                }
                distance[i][j] = distance[i - 1][j - 1] + 1;
                if (distance[i][j] <= maxLen) continue;
                maxLen = distance[i][j];
                m = i; n = j;
            }
        }
        if (maxLen == 0) return "";
        StringBuffer buffer = new StringBuffer();
        while (distance[m][n] > 0) {
            if (s1.charAt(m - 1) == s2.charAt(n - 1)) {
                buffer.append(s1.charAt(m - 1));
                m--; n--;
                continue;
            }
            if (distance[m - 1][n] >= distance[m][n - 1]) m--;
            else n--;
        }
        return buffer.reverse().toString();
    }

    /**
     * 二分查找
     *
     * @param objects 有序列表
     * @param object 查找对象
     * @param comparator 比较器
     * @return 成功返回下标，否则返回-1
     * @param <T>
     */
    public static <T> int binarySearch(List<T> objects, T object, Comparator<T> comparator) {
        if (object == null) {
            logger.warn("search object is null");
            return -1;
        }
        if (objects == null || objects.isEmpty()) {
            logger.warn("search object list is empty");
            return -1;
        }
        if (comparator == null) {
            logger.warn("comparator is null");
            return -1;
        }
        int start = 0, end = objects.size() - 1, mid = (start + end) / 2;
        while (true) {
            T middleObject = objects.get(mid);
            int compareResult = comparator.compare(object, middleObject);
            if (compareResult == 0) return mid;
            if (compareResult < 0) end = mid - 1;
            else start = mid + 1;
            if (start > end) break;
            mid = (start + end) / 2;
        }
        return -1;
    }

    /**
     * 计算最大公约数
     *
     * @param m 输入数字
     * @param n 输入数字
     * @return 最大公约数
     */
    public static int computeGCD(int m, int n) {
        if (m < 0 || n < 0) {
            logger.error("input num must be greater than zero");
            throw new RuntimeException("input num must be greater than zero");
        }
        if (m == 0) return n;
        if (n == 0) return m;
        if (m < n) {
            int temp = m;
            m = n;
            n = temp;
        }
        while (m % n != 0) {
            int temp = m % n;
            m = n;
            n = temp;
        }
        return n;
    }

    /**
     * 计算最小公倍数
     *
     * @param m 输入数字
     * @param n 输入数字
     * @return 最小公倍数
     */
    public static int computeLCM(int m, int n) {
        if (m < 0 || n < 0) {
            logger.error("input num must be greater than zero");
            throw new RuntimeException("input num must be greater than zero");
        }
        return m * n / computeGCD(m, n);
    }
}
