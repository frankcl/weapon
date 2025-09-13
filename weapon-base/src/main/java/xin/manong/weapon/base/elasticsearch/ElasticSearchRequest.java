package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import java.util.List;

/**
 * 搜索请求
 *
 * @author frankcl
 * @date 2025-09-11 14:00:49
 */
public class ElasticSearchRequest {

    public int from = 0;
    public int size = 10;
    public String index;
    public Query query;
    public List<ElasticSortOption> sortOptions;
    public List<FieldValue> cursor;
    public List<String> includes;
    public List<String> excludes;
}
