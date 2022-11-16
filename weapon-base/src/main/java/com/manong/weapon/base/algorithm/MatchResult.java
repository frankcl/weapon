package com.manong.weapon.base.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * 匹配结果
 *
 * @author frankcl
 * @date 2022-11-12 14:52:58
 */
public class MatchResult {

    public List<Integer> positions;
    public String pattern;

    public MatchResult(String pattern) {
        this.positions = new ArrayList<>();
        this.pattern = pattern;
    }
}
