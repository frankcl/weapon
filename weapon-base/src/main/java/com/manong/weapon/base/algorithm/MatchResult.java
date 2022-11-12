package com.manong.weapon.base.algorithm;

/**
 * 匹配结果
 *
 * @author frankcl
 * @date 2022-11-12 14:52:58
 */
public class MatchResult {

    public int pos = -1;
    public String pattern;

    public MatchResult(int pos, String pattern) {
        this.pos = pos;
        this.pattern = pattern;
    }
}
