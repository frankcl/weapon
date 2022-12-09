package xin.manong.weapon.base.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 文本相似度工具
 *
 * @author frankcl
 * @date 2022-07-20 10:50:06
 */
public class Similarity {

    private static Segment segment = createSegment();

    /**
     * 创建分词器
     *
     * @return 分词器
     */
    private static Segment createSegment() {
        Segment segment = HanLP.newSegment();
        segment.enableOffset(true);
        return segment;
    }

    /**
     * 分词：去除停用词
     *
     * @param text 文本
     * @return 分词列表
     */
    private static List<String> analyze(String text) {
        List<String> words = new ArrayList<>();
        List<Term> terms = segment.seg(text);
        for (Term term : terms) {
            if (term.nature.firstChar() == 'w') continue;
            if (term.nature.firstChar() != 'm' && CoreStopWordDictionary.contains(term.word)) continue;
            words.add(term.word);
        }
        return words;
    }

    /**
     * 根据最长公共字符串原理计算文本相似度
     * 分数计算原理：公共字符越多，文本越相似，分数阈值[0.0, 1.0]
     *
     * @param s1 输入文本
     * @param s2 输入文本
     * @return 相似性分数，阈值[0.0, 1.0]
     */
    public static double coefficientLCS(String s1, String s2) {
        boolean empty1 = StringUtils.isEmpty(s1);
        boolean empty2 = StringUtils.isEmpty(s2);
        if (empty1 && empty2) return 1.0d;
        if (empty1 || empty2) return 0d;
        int d[][] = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) d[i][j] = d[i - 1][j - 1] + 1;
                else d[i][j] = Math.max(d[i - 1][j], d[i][j - 1]);
            }
        }
        return d[s1.length()][s2.length()] * 2.0d / (s1.length() + s2.length());
    }

    /**
     * 根据Jaccard相似系数计算文本相似度
     * 分数计算原理：重合分词越多，文本越相似，分数阈值[0.0, 1.0]
     *
     * @param s1 输入文本
     * @param s2 输入文本
     * @return 相似性分数，阈值[0.0, 1.0]
     */
    public static double coefficientJaccard(String s1, String s2) {
        boolean empty1 = StringUtils.isEmpty(s1);
        boolean empty2 = StringUtils.isEmpty(s2);
        if (empty1 && empty2) return 1.0d;
        if (empty1 || empty2) return 0d;
        if (s1.equals(s2)) return 1.0d;
        Set<String> words1 = new HashSet<>(analyze(s1));
        Set<String> words2 = new HashSet<>(analyze(s2));
        Set<String> unionWords = new HashSet<>();
        unionWords.addAll(words1);
        unionWords.addAll(words2);
        if (unionWords.isEmpty()) return 0d;
        return (words1.size() + words2.size() - unionWords.size()) * 1.0d / unionWords.size();
    }
}
