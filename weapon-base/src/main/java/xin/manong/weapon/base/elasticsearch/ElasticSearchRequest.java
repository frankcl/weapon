package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import java.util.ArrayList;
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
    public List<String> indices;
    public Query query;
    public boolean trackTotalHits = false;
    public List<ElasticSortOption> sortOptions;
    public List<FieldValue> cursor;
    public List<String> includes;
    public List<String> excludes;
    public List<ElasticHighlight> highlights;

    /**
     * 添加索引
     *
     * @param index 索引
     */
    public void addIndex(String index) {
        if (indices == null) indices = new ArrayList<>();
        if (!indices.contains(index)) indices.add(index);
    }

    /**
     * 添加排序选项
     *
     * @param sortOption 排序选项
     */
    public void addSortOption(ElasticSortOption sortOption) {
        if (sortOptions == null) sortOptions = new ArrayList<>();
        sortOptions.add(sortOption);
    }

    /**
     * 添加返回包含字段
     *
     * @param include 返回包含字段
     */
    public void addInclude(String include) {
        if (includes == null) includes = new ArrayList<>();
        includes.add(include);
    }

    /**
     * 添加返回排除字段
     *
     * @param exclude 返回排除字段
     */
    public void addExclude(String exclude) {
        if (excludes == null) excludes = new ArrayList<>();
        excludes.add(exclude);
    }

    /**
     * 添加高亮请求
     *
     * @param highlight 高亮请求
     */
    public void addHighlight(ElasticHighlight highlight) {
        if (highlights == null) highlights = new ArrayList<>();
        highlights.add(highlight);
    }
}
