package xin.manong.weapon.base.elasticsearch;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ES高亮数据接口
 *
 * @author frankcl
 * @date 2025-09-30 15:14:58
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElasticHighlightRecord implements Serializable {

    @JSONField(name = "highlight_map")
    @JsonProperty("highlight_map")
    public Map<String, List<String>> highlightMap;

    /**
     * 注入高亮结果
     *
     * @param highlightMap 高亮结果
     */
    public void injectHighlight(Map<String, List<String>> highlightMap) {
        this.highlightMap = highlightMap;
    }
}
