package com.manong.weapon.base.algorithm;

import com.manong.weapon.base.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 多模匹配：WuManber算法实现
 *
 * @author frankcl
 * @date 2022-11-12 14:48:33
 */
public class WM {

    private final static Logger logger = LoggerFactory.getLogger(WM.class);

    private final static int MAX_TABLE_SIZE = 100003;

    /* 块长度 */
    private int B;
    /* 最小模式长度 */
    private int m;
    /* 前缀长度 */
    private int p;
    /* 匹配模式列表 */
    private List<String> patterns;
    private Map<String, Integer> shiftTable;
    private Map<String, Integer> auxShiftTable;
    private Map<String, Map<String, List<Integer>>> hashTable;
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);

    public WM(List<String> patterns) {
        B = 2;
        p = 3;
        m = Integer.MAX_VALUE;
        build(patterns);
    }

    public WM(List<String> patterns, int B) {
        this.B = B > 3 ? 3 : B;
        this.p = 3;
        this.m = Integer.MAX_VALUE;
        build(patterns);
    }

    /**
     * 构建匹配模型
     * 构建失败抛出异常
     *
     * @param patterns 匹配模式列表
     */
    private void build(List<String> patterns) {
        List<String> tempPatterns = new ArrayList<>();
        if (patterns != null) tempPatterns.addAll(patterns);
        Iterator<String> iterator = tempPatterns.iterator();
        while (iterator.hasNext()) {
            String pattern = iterator.next();
            if (pattern == null || pattern.equals("")) iterator.remove();
        }
        if (tempPatterns.isEmpty()) {
            logger.error("match patterns are empty");
            throw new RuntimeException("匹配模式为空");
        }
        try {
            readWriteLock.writeLock().lock();
            this.patterns = tempPatterns;
            for (String pattern : this.patterns) m = Math.min(m, pattern.length());
            if (m == 0 || m == Integer.MAX_VALUE) {
                logger.error("invalid min pattern size[{}]", m);
                throw new RuntimeException(String.format("非法最小模式长度[%d]", m));
            }
            if (B > m) {
                logger.warn("block size[{}] is longer than min pattern size[{}], set block size[{}]", B, m, m);
                B = m;
            }
            if (p > m) {
                logger.warn("prefix[{}] is longer than min pattern size[{}], set prefix[{}]", p, m, m);
                p = m;
            }
            int prefixTableSize = CommonUtil.findNextPrime(patterns.size() * 10);
            int tableSize = (m - B + 1) * this.patterns.size() * 5;
            tableSize = CommonUtil.findNextPrime(tableSize);
            if (tableSize > MAX_TABLE_SIZE) tableSize = MAX_TABLE_SIZE;
            shiftTable = new HashMap<>(tableSize);
            auxShiftTable = new HashMap<>(tableSize);
            hashTable = new HashMap<>(tableSize);
            for (int i = 0; i < patterns.size(); i++) {
                String pattern = patterns.get(i);
                String prefix = pattern.substring(0, p);
                for (int k = 0; k < m - B + 1; k++) {
                    String block = pattern.substring(k, k + B);
                    int shiftLen = m - k - B;
                    int auxShiftLen = shiftLen == 0 ? m - B + 1 : shiftLen;
                    if (!shiftTable.containsKey(block)) shiftTable.put(block, shiftLen);
                    else shiftTable.put(block, Math.min(shiftLen, shiftTable.get(block)));
                    if (shiftLen == 0) {
                        buildHashTable(block, prefixTableSize, prefix, i);
                        auxShiftTable.put(block, auxShiftTable.containsKey(block) ?
                                Math.min(auxShiftLen, auxShiftTable.get(block)) : auxShiftLen);
                    }
                }
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * 构建后缀hash表
     *
     * @param block 块
     * @param prefixTableSize 前缀表大小
     * @param prefix 前缀
     * @param patternIndex 模式下标
     */
    private void buildHashTable(String block, int prefixTableSize, String prefix, int patternIndex) {
        if (!hashTable.containsKey(block)) hashTable.put(block, new HashMap<>(prefixTableSize));
        Map<String, List<Integer>> prefixTable = hashTable.get(block);
        if (!prefixTable.containsKey(prefix)) prefixTable.put(prefix, new ArrayList<>());
        prefixTable.get(prefix).add(patternIndex);
    }

    /**
     * 重新构建匹配模型
     * 构建失败抛出异常
     *
     * @param patterns 匹配模式列表
     */
    public void rebuild(List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            logger.error("rebuild patterns are empty");
            throw new RuntimeException("重建模式不能为空");
        }
        build(patterns);
    }

    /**
     * 搜索匹配
     *
     * @param text 匹配文本
     * @return 匹配结果列表
     */
    public List<MatchResult> search(String text) {
        List<MatchResult> matchResults = new ArrayList<>();
        if (text == null || text.equals("")) {
            logger.warn("search text is empty");
            return matchResults;
        }
        try {
            readWriteLock.readLock().lock();
            for (int i = m - B; i < text.length(); ) {
                if (i + B > text.length()) break;
                String block = text.substring(i, i + B);
                if (!shiftTable.containsKey(block)) {
                    i += m - B + 1;
                    continue;
                }
                int shiftLen = shiftTable.get(block);
                if (shiftLen != 0) {
                    i += shiftLen;
                    continue;
                }
                Map<String, List<Integer>> prefixTable = hashTable.get(block);
                String prefix = text.substring(i - m + B, i - m + B + p);
                if (prefixTable.containsKey(prefix)) {
                    for (Integer index : prefixTable.get(prefix)) {
                        String pattern = patterns.get(index);
                        MatchResult matchResult = match(pattern, text, i);
                        if (matchResult != null) matchResults.add(matchResult);
                    }
                }
                i += auxShiftTable.get(block);
            }
            return matchResults;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * 模式匹配
     *
     * @param pattern 模式
     * @param text 匹配文本
     * @param pos 起始位置
     * @return 匹配成功返回结果，否则返回null
     */
    private MatchResult match(String pattern, String text, int pos) {
        int from = pos - m + B;
        for (int i = p, j = 0; i < pattern.length(); i++, j++) {
            int k = from + p + j;
            if (k >= text.length() || pattern.charAt(i) != text.charAt(k)) return null;
        }
        return new MatchResult(from, pattern);
    }
}
