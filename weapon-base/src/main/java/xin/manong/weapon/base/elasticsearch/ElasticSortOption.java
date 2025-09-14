package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 排序选项
 *
 * @author frankcl
 * @date 2025-09-11 17:37:36
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElasticSortOption {

    @JSONField(name = "field")
    @JsonProperty("field")
    public String field;
    @JSONField(name = "sort_order")
    @JsonProperty("sort_order")
    public SortOrder sortOrder = SortOrder.Asc;
}
