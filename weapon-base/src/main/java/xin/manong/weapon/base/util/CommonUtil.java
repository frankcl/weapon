package xin.manong.weapon.base.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 通用工具
 *
 * @author frankcl
 * @create 2020-01-08 15:36:09
 */
public class CommonUtil {

    private final static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    private final static Pattern IP_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
    private final static String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 计算字符串中指定字符出现次数
     *
     * @param s 字符串
     * @param character 字符
     * @return 字符出现次数
     */
    public static int characterOccurrence(String s, char character) {
        int n = 0;
        if (StringUtils.isEmpty(s)) return n;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == character) n++;
        return n;
    }

    /**
     * 字符串转换时间戳
     *
     * @param string 时间字符串
     * @param format 时间格式，如果为空使用默认格式
     * @return 成功返回毫秒时间戳，否则返回null
     */
    public static Long stringToTime(String string, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(StringUtils.isEmpty(format) ? DEFAULT_TIME_FORMAT : format);
        try {
            return dateFormat.parse(string).getTime();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 时间戳转换字符串
     *
     * @param timestamp 毫秒时间戳
     * @param format 时间格式
     * @return 成功返回字符串表示，否则返回null
     */
    public static String timeToString(long timestamp, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(StringUtils.isEmpty(format) ? DEFAULT_TIME_FORMAT : format);
        return dateFormat.format(new Date(timestamp));
    }

    /**
     * 获取URL中host
     *
     * @param str URL字符串
     * @return 如果url合法返回host，否则返回空字符串
     */
    public static String getHost(String str) {
        try {
            URL url = new URL(str);
            return url.getHost();
        } catch (MalformedURLException e) {
            logger.warn("malformed url[{}]", str);
            logger.warn(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 计算最长回文子串
     *
     * @param s 输入字符串
     * @return 如果存在返回最大回文子串，否则返回空字符串
     */
    public static String computeLPS(String s) {
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
    public static String computeLCS(String s1, String s2) {
        if (StringUtils.isEmpty(s1) || StringUtils.isEmpty(s2)) return "";
        int pos = -1, maxLen = 0;
        int[][] d = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) != s2.charAt(j - 1)) continue;
                d[i][j] = d[i - 1][j - 1] + 1;
                if (d[i][j] <= maxLen) continue;
                maxLen = d[i][j];
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
    public static String computeMCS(String s1, String s2) {
        if (StringUtils.isEmpty(s1) || StringUtils.isEmpty(s2)) return "";
        int m = 0, n = 0, maxLen = 0;
        int[][] d = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                    d[i][j] = Math.max(d[i - 1][j], d[i][j - 1]);
                    continue;
                }
                d[i][j] = d[i - 1][j - 1] + 1;
                if (d[i][j] <= maxLen) continue;
                maxLen = d[i][j];
                m = i; n = j;
            }
        }
        if (maxLen == 0) return "";
        StringBuffer sb = new StringBuffer();
        while (d[m][n] > 0) {
            if (s1.charAt(m - 1) == s2.charAt(n - 1)) {
                sb.append(s1.charAt(m - 1));
                m--; n--;
                continue;
            }
            if (d[m - 1][n] >= d[m][n - 1]) m--;
            else n--;
        }
        return sb.reverse().toString();
    }

    /**
     * 判断字符是否为字母
     *
     * @param c 字符
     * @return 如果是字母返回true，否则返回false
     */
    public static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /**
     * 判断字符是否为数字
     *
     * @param c 字符
     * @return 如果是数字返回true，否则返回false
     */
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * 判断字符是否为空白字符
     *
     * @param c 字符
     * @return 如果是空白字符返回true，否则返回false
     */
    public static boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    /**
     * 判断是否为IP
     *
     * @param str 字符串
     * @return 符合IP规范返回true，否则返回false
     */
    public static boolean isIP(String str) {
        if (StringUtils.isEmpty(str)) return false;
        return IP_PATTERN.matcher(str).matches();
    }

    /**
     * 取小数点后n位数字
     *
     * @param v 浮点数
     * @param n 小数点后保留位数
     * @return 浮点数
     */
    public static double round(double v, int n) {
        if (n < 0) throw new IllegalArgumentException(String.format("n[%d] must be greater than 0", n));
        BigDecimal bigDecimal = new BigDecimal(v);
        return bigDecimal.setScale(n, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 判断一个整数是否为质数
     *
     * @param n 整数
     * @return 质数返回true，否则返回false
     */
    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        int m = (int) Math.ceil(Math.sqrt(n));
        for (int i = 2; i <= m; i++) {
            if (n % i == 0 && n != 2) return false;
        }
        return true;
    }

    /**
     * 找到比n大的第一个质数
     *
     * @param n
     * @return 比n大的第一个质数
     */
    public static int findNextPrime(int n) {
        if (n <= 1) return 2;
        while (true) if (isPrime(++n)) return n;
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
            throw new IllegalArgumentException("input num must be greater than zero");
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
            throw new IllegalArgumentException("input num must be greater than zero");
        }
        return m * n / computeGCD(m, n);
    }

    /**
     * 判断对象是否为Java原型
     *
     * @param object 对象
     * @return 是原型返回true，否则返回false
     */
    public static boolean isPrimitiveType(Object object) {
        if (object == null) return false;
        if (object instanceof Integer) return true;
        if (object instanceof Short) return true;
        if (object instanceof Long) return true;
        if (object instanceof Float) return true;
        if (object instanceof Double) return true;
        if (object instanceof String) return true;
        if (object instanceof Boolean) return true;
        if (object instanceof Character) return true;
        if (object instanceof Byte) return true;
        return false;
    }

    /**
     * 从列表中选择m个元素进行组合
     *
     * @param elements 元素列表
     * @param m 选择数量
     * @return 组合结果
     * @param <V>
     */
    public static <V> List<List<V>> combination(List<V> elements, int m) {
        if (elements == null || m <= 0 || m > elements.size()) return new ArrayList<>();
        return combination(elements, 0, m);
    }

    /**
     * 从列表中选择m个元素进行组合
     *
     * @param elements 元素列表
     * @param from 起始下标
     * @param m 选择数量
     * @return 组合结果
     * @param <V>
     */
    private static <V> List<List<V>> combination(List<V> elements, int from, int m) {
        List<List<V>> results = new ArrayList<>();
        if (m == 0 || from >= elements.size() || from + m > elements.size()) return results;
        V element = elements.get(from);
        List<List<V>> selects = combination(elements, from + 1, m - 1);
        for (List<V> select : selects) select.add(0, element);
        if (selects.isEmpty()) {
            List<V> select = new ArrayList<>();
            select.add(element);
            selects.add(select);
        }
        results.addAll(selects);
        results.addAll(combination(elements, from + 1, m));
        return results;
    }

    /**
     * 从列表elements中选择m个元素进行排列
     *
     * @param elements 元素列表
     * @param m 选择数量
     * @return 排列结果
     * @param <V>
     */
    public static <V> List<List<V>> permutation(List<V> elements, int m) {
        List<List<V>> results = new ArrayList<>();
        if (elements == null || m <= 0 || m > elements.size()) return results;
        permutation(elements, m, new ArrayList<>(), results);
        return results;
    }

    /**
     * 从列表elements中选择m个元素进行排列
     *
     * @param elements 元素列表
     * @param m 选择数量
     * @param selects 当前选择结果
     * @param results 排列结果
     * @param <V>
     */
    private static <V> void permutation(List<V> elements, int m,
                                        List<V> selects, List<List<V>> results) {
        if (selects.size() == m) {
            results.add(new ArrayList<>(selects));
            return;
        }
        for (V element : elements) {
            if (selects.indexOf(element) != -1) continue;
            selects.add(element);
            permutation(elements, m, selects, results);
            selects.remove(selects.size() - 1);
        }
    }
}
