package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch._types.SortOrder;

/**
 * 排序选项
 *
 * @author frankcl
 * @date 2025-09-11 17:37:36
 */
public class ElasticSortOption {

    public String field;
    public SortOrder sortOrder = SortOrder.Asc;
}
