package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch._types.Refresh;

import java.io.Serializable;

/**
 * ES选项
 *
 * @author frankcl
 * @date 2025-10-24 13:44:28
 */
public class ElasticOption implements Serializable {

    public Long seqNo;
    public Long primaryTerm;
    public Refresh refresh;

    public ElasticOption() {
        refresh = Refresh.False;
    }

    public ElasticOption(Refresh refresh) {
        this.refresh = refresh;
    }

    public ElasticOption(Long seqNo, Long primaryTerm, Refresh refresh) {
        this(refresh);
        this.seqNo = seqNo;
        this.primaryTerm = primaryTerm;
    }
}
