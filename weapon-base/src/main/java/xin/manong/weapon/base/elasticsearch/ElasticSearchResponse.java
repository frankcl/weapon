package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索响应结果
 *
 * @author frankcl
 * @date 2025-09-11 14:03:37
 */
public class ElasticSearchResponse<T> {

    public Integer from = 0;
    public Integer size = 10;
    public Long total;
    public List<T> records;
    public List<FieldValue> cursor;

    public ElasticSearchResponse() {
        total = 0L;
        records = new ArrayList<>();
    }
}
