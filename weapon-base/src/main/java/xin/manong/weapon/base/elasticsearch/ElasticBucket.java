package xin.manong.weapon.base.elasticsearch;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import java.util.List;
import java.util.Map;

/**
 * 分桶统计结果
 *
 * @author frankcl
 * @date 2025-09-13 21:13:51
 */
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElasticBucket<T> {

    /**
     * 统计key
     */
    @JSONField(name = "key")
    @JsonProperty("key")
    public T key;
    /**
     * 统计数量
     */
    @JSONField(name = "count")
    @JsonProperty("count")
    public Long count;
    /**
     * 子分桶统计结果
     */
    @JSONField(name = "bucket_map")
    @JsonProperty("bucket_map")
    public Map<String, List<ElasticBucket<?>>> bucketMap;

    public ElasticBucket(T key, Long count) {
        this.key = key;
        this.count = count;
    }
}
