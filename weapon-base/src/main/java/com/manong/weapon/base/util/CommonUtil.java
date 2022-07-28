package com.manong.weapon.base.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 通用工具
 *
 * @author frankcl
 * @create 2020-01-08 15:36:09
 */
public class CommonUtil {

    private final static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    private final static String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
     * 取小数点后n位数字
     *
     * @param v 浮点数
     * @param n 小数点后保留位数
     * @return 浮点数
     */
    public static double round(double v, int n) {
        if (n < 0) throw new RuntimeException(String.format("n[%d] must be greater than 0", n));
        BigDecimal bigDecimal = new BigDecimal(v);
        return bigDecimal.setScale(n, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
