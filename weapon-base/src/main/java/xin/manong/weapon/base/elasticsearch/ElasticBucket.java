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
     * 统计名称
     */
    @JSONField(name = "name")
    @JsonProperty("name")
    public String name;

    /**
     * 统计数量
     */
    @JSONField(name = "count")
    @JsonProperty("count")
    public Long count;
    /**
     * 子分桶统计结果
     */
    @JSONField(name = "buckets")
    @JsonProperty("buckets")
    public List<ElasticBucket<?>> buckets;
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

    /**
     * 平铺bucket map
     */
    public void flatBucketMap() {
        if (bucketMap == null || bucketMap.isEmpty()) return;
        if (bucketMap.size() > 1) throw new UnsupportedOperationException("Unsupported flat bucket map");
        for (List<ElasticBucket<?>> bucketList : bucketMap.values()) {
            buckets = bucketList;
            for (ElasticBucket<?> bucket : bucketList) bucket.flatBucketMap();
        }
        bucketMap = null;
    }
}
