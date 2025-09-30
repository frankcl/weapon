package xin.manong.weapon.base.elasticsearch;

import java.util.ArrayList;
import java.util.List;

/**
 * ES高亮字段
 *
 * @author frankcl
 * @date 2025-09-30 14:58:56
 */
public class ElasticHighlight {

    public String field;
    public Integer fragmentSize = 100;
    public Integer fragmentNum = 2;
    public List<String> preTags;
    public List<String> postTags;

    public ElasticHighlight() {}

    public ElasticHighlight(String field) {
        this.field = field;
    }

    /**
     * 添加前置标签
     *
     * @param preTag 前置标签
     */
    public void addPreTag(String preTag) {
        if (preTags == null) preTags = new ArrayList<>();
        preTags.add(preTag);
    }

    /**
     * 添加后置标签
     *
     * @param postTag 后置标签
     */
    public void addPostTag(String postTag) {
        if (postTags == null) postTags = new ArrayList<>();
        postTags.add(postTag);
    }
}
