package xin.manong.weapon.base.elasticsearch;

/**
 * ElasticSearch数据记录
 *
 * @author frankcl
 * @date 2025-10-21 16:05:39
 */
public class ElasticRecord<T> {

    public Long seqNo;
    public Long primaryTerm;
    public Long version;
    public T value;

    public ElasticRecord(Long version, T value) {
        this.version = version;
        this.value = value;
    }

    public ElasticRecord(Long seqNo, Long primaryTerm, T value) {
        this.seqNo = seqNo;
        this.primaryTerm = primaryTerm;
        this.value = value;
    }

    public ElasticRecord(Long seqNo, Long primaryTerm, Long version, T value) {
        this(seqNo, primaryTerm, value);
        this.version = version;
    }
}
