package xin.manong.weapon.base.elasticsearch;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 索引统计
 *
 * @author frankcl
 * @date 2026-03-04 16:38:55
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElasticIndexStats {

    @JSONField(name = "index")
    @JsonProperty("index")
    public String index;
    @JSONField(name = "status")
    @JsonProperty("status")
    public String status;
    @JSONField(name = "health")
    @JsonProperty("health")
    public String health;
    @JSONField(name = "doc_count")
    @JsonProperty("doc_count")
    public long docCount;
    @JSONField(name = "storage_size_bytes")
    @JsonProperty("storage_size_bytes")
    public long storageSizeBytes;
}
