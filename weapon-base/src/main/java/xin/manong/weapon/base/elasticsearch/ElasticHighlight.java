package xin.manong.weapon.base.elasticsearch;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * ES高亮字段
 *
 * @author frankcl
 * @date 2025-09-30 14:58:56
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElasticHighlight {

    @JSONField(name = "field")
    @JsonProperty("field")
    public String field;
    /**
     * highlight类型：unified, fvh, plain
     */
    @JSONField(name = "type")
    @JsonProperty("type")
    public String type;
    @JSONField(name = "fragment_offset")
    @JsonProperty("fragment_offset")
    public Integer fragmentOffset;
    @JSONField(name = "fragment_size")
    @JsonProperty("fragment_size")
    public Integer fragmentSize = 100;
    @JSONField(name = "fragment_num")
    @JsonProperty("fragment_num")
    public Integer fragmentNum = 2;
    @JSONField(name = "pre_tags")
    @JsonProperty("pre_tags")
    public List<String> preTags;
    @JSONField(name = "post_tags")
    @JsonProperty("post_tags")
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
