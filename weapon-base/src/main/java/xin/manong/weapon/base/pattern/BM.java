package xin.manong.weapon.base.pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 字符串单模匹配算法：BoyerMoore算法实现
 *
 * @author frankcl
 * @date 2022-11-19 13:29:55
 */
public class BM {

    private final static Logger logger = LoggerFactory.getLogger(BM.class);

    /* 模式字符串 */
    private String pattern;
    /* 好后缀表 */
    private int[] bmGS;
    /* 后缀长度表 */
    private int[] bmSL;
    /* 坏字符表 */
    private Map<Character, Integer> bmBC;
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);

    public BM(String pattern) {
        build(pattern);
    }

    /**
     * 重建匹配模式
     * 模式非法抛出异常
     *
     * @param pattern 重建匹配模式
     */
    public void rebuild(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            logger.error("Rebuild pattern is empty");
            throw new IllegalArgumentException("重建匹配模式为空");
        }
        build(pattern);
    }

    /**
     * 匹配文本
     *
     * @param text 待匹配文本
     * @return 匹配上返回匹配结果，否则返回null
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public MatchResult search(String text) {
        if (text == null || text.isEmpty()) {
            logger.warn("Search text is empty");
            return null;
        }
        try {
            readWriteLock.readLock().lock();
            MatchResult result = new MatchResult(pattern);
            int n = text.length(), m = pattern.length();
            for (int i = 0; i <= n - m; ) {
                int j = m - 1;
                for (; j >= 0 && pattern.charAt(j) == text.charAt(i + j); j--) ;
                if (j < 0) {
                    result.positions.add(i);
                    i++;
                } else {
                    char c = text.charAt(i + j);
                    i += Math.max(bmBC.containsKey(c) ? bmBC.get(c) - m + 1 + j : m, bmGS[j]);
                }
            }
            return result.positions.isEmpty() ? null : result;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * 根据模式构建坏字符表，好后缀表及后缀长度表
     * 构建失败抛出异常
     *
     * @param pattern 模式串
     */
    private void build(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            logger.error("Pattern is empty");
            throw new IllegalArgumentException("匹配模式为空");
        }
        try {
            readWriteLock.writeLock().lock();
            this.pattern = pattern;
            buildBC(pattern);
            quickBuildSL(pattern);
            buildGS(pattern);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * 构建坏字符表
     *
     * @param pattern 模式串
     */
    private void buildBC(String pattern) {
        bmBC = new HashMap<>();
        int len = pattern.length();
        for (int i = 0; i < len - 1; i++) {
            char c = pattern.charAt(i);
            bmBC.put(c, len - 1 - i);
        }
    }

    /**
     * 构建后缀长度表
     *
     * @param pattern 模式串
     */
    private void buildSL(String pattern) {
        int len = pattern.length();
        bmSL = new int[len];
        bmSL[len - 1] = len;
        for (int i = len - 2; i >= 0; i--) {
            int j = i;
            while (j >= 0 && pattern.charAt(j) == pattern.charAt(len - 1 - i + j)) j--;
            bmSL[i] = i - j;
        }
    }

    /**
     * 快速构建后缀长度表
     *
     * @param pattern 模式串
     */
    private void quickBuildSL(String pattern) {
        int len = pattern.length();
        bmSL = new int[len];
        bmSL[len - 1] = len;
        for (int i = len - 2, j = 0, k = len - 1; i >= 0; i--) {
            if (i > k && bmSL[i + len - 1 - j] < i - k) bmSL[i] = bmSL[i + len - 1 - j];
            else {
                if (i < k) k = i;
                j = i;
                while (k >= 0 && pattern.charAt(k) == pattern.charAt(k + len - 1 - j)) k--;
                bmSL[i] = j - k;
            }
        }
    }

    /**
     * 构建好后缀表
     *
     * @param pattern 模式串
     */
    private void buildGS(String pattern) {
        int len = pattern.length();
        bmGS = new int[len];
        for (int i = 0; i < len; i++) bmGS[i] = len;
        for (int i = len - 1, j = 0; i >= 0; i--) {
            if (bmSL[i] != i + 1) continue;
            for (; j < len - 1 - i; j++) {
                if (bmGS[j] == len) bmGS[j] = len - 1 - i;
            }
        }
        for (int i = 0; i < len - 1; i++) {
            bmGS[len - 1 - bmSL[i]] = len - 1 - i;
        }
    }
}
