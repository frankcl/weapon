package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import org.apache.commons.lang3.StringUtils;
import xin.manong.weapon.base.common.RangeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ElasticSearch查询工具
 *
 * @author frankcl
 * @date 2025-11-27 15:17:32
 */
public class ElasticSearchQuery {

    /**
     * 转换字段不存在查询
     *
     * @param field 字段名
     * @return 不存在查询
     */
    public static Query convertNotExistsQuery(String field) {
        Objects.requireNonNull(field, "Field not allowed to be null");
        BoolQuery.Builder builder = new BoolQuery.Builder();
        builder.mustNot(ExistsQuery.of(b -> b.field(field))._toQuery());
        return builder.build()._toQuery();
    }

    /**
     * 转换term查询
     *
     * @param field 字段名
     * @param value 字段值
     * @return term查询
     */
    public static Query convertTermQuery(String field, Object value) {
        Objects.requireNonNull(field, "Field not allowed to be null");
        Objects.requireNonNull(value, "Value not allowed to be null");
        if (value instanceof Boolean) {
            return TermQuery.of(builder -> builder.field(field).value((Boolean) value))._toQuery();
        } else if (value instanceof Double || value instanceof Float) {
            return TermQuery.of(builder -> builder.field(field).value(((Number) value).doubleValue()))._toQuery();
        } else if (value instanceof Long || value instanceof Integer) {
            return TermQuery.of(builder -> builder.field(field).value(((Number) value).longValue()))._toQuery();
        } else if (value instanceof String) {
            return TermQuery.of(builder -> builder.field(field).value((String) value))._toQuery();
        }
        throw new UnsupportedOperationException(String.format("不支持的数据类型:%s", value.getClass()));
    }

    /**
     * 转换terms查询
     *
     * @param field 字段名
     * @param values 字段值列表
     * @return terms查询
     * @param <T> 数据类型
     */
    public static <T> Query convertTermsQuery(String field, List<T> values) {
        Objects.requireNonNull(field, "Field not allowed to be null");
        Objects.requireNonNull(values, "Values not allowed to be null");
        List<FieldValue> fieldValues = new ArrayList<>();
        for (Object value : values) fieldValues.add(FieldValue.of(value));
        return TermsQuery.of(builder -> builder.field(field).terms(f -> f.value(fieldValues)))._toQuery();
    }

    /**
     * 转换Match查询
     *
     * @param field 字段名
     * @param query 查询值
     * @param analyzer 分词器
     * @param operator 操作符
     * @param minimumShouldMatch 最小匹配
     * @param boost 权重
     * @param fuzziness 模糊距离
     * @param prefixLength 模糊匹配前缀长度
     * @param maxExpansions 模糊匹配最大扩展数
     * @return Match查询
     */
    public static Query convertMatchQuery(String field, Object query,
                                          String analyzer, Operator operator,
                                          String minimumShouldMatch, Float boost,
                                          String fuzziness, Integer prefixLength,
                                          Integer maxExpansions) {
        Objects.requireNonNull(field, "Field not allowed to be null");
        Objects.requireNonNull(query, "Query not allowed to be null");
        MatchQuery.Builder builder = new MatchQuery.Builder();
        builder.field(field).query(FieldValue.of(query));
        if (StringUtils.isNotEmpty(analyzer)) builder.analyzer(analyzer);
        if (StringUtils.isNotEmpty(minimumShouldMatch)) builder.minimumShouldMatch(minimumShouldMatch);
        if (operator != null) builder.operator(operator);
        if (boost != null) builder.boost(boost);
        if (StringUtils.isNotEmpty(fuzziness)) builder.fuzziness(fuzziness);
        if (prefixLength != null && prefixLength > 0) builder.prefixLength(prefixLength);
        if (maxExpansions != null && maxExpansions > 0) builder.maxExpansions(maxExpansions);
        return builder.build()._toQuery();
    }

    /**
     * 构建MatchPhrase查询
     *
     * @param field 字段名
     * @param query 查询
     * @param analyzer 分词器
     * @param slop 查询距离
     * @param boost 权重
     * @return MatchPhrase查询
     */
    public static Query convertMatchPhraseQuery(String field, String query,
                                                String analyzer, Integer slop, Float boost) {
        Objects.requireNonNull(field, "Field not allowed to be null");
        Objects.requireNonNull(query, "Query not allowed to be null");
        MatchPhraseQuery.Builder builder = new MatchPhraseQuery.Builder();
        builder.field(field).query(query);
        if (boost != null) builder.boost(boost);
        if (slop != null && slop >= 0) builder.slop(slop);
        if (StringUtils.isNotEmpty(analyzer)) builder.analyzer(analyzer);
        return builder.build()._toQuery();
    }

    /**
     * 构建模糊查询
     *
     * @param field 字段名
     * @param value 字段值
     * @param fuzziness 模糊距离
     * @param prefixLength 模糊匹配前缀长度
     * @param maxExpansions 模糊匹配最大扩展数
     * @return 模糊查询
     */
    public static Query convertFuzzyQuery(String field, Object value,
                                          String fuzziness, Integer prefixLength,
                                          Integer maxExpansions) {
        Objects.requireNonNull(field, "Field not allowed to be null");
        Objects.requireNonNull(value, "Value not allowed to be null");
        FuzzyQuery.Builder builder = new FuzzyQuery.Builder();
        builder.field(field).value(FieldValue.of(value));
        if (prefixLength != null && prefixLength > 0) builder.prefixLength(prefixLength);
        if (maxExpansions != null && maxExpansions > 0) builder.maxExpansions(maxExpansions);
        if (StringUtils.isNotEmpty(fuzziness)) builder.fuzziness(fuzziness);
        return builder.build()._toQuery();
    }

    /**
     * 转换范围查询
     *
     * @param field 字段名
     * @param rangeValue 范围值
     * @return 范围查询
     * @param <T> 数据类型
     */
    public static <T extends Number> Query convertRangeQuery(String field, RangeValue<T> rangeValue) {
        Objects.requireNonNull(field, "Field not allowed to be null");
        Objects.requireNonNull(rangeValue, "RangeValue not allowed to be null");
        return RangeQuery.of(builder -> {
            builder.field(field);
            if (rangeValue.start != null) {
                if (rangeValue.includeLower) builder.gte(JsonData.of(rangeValue.start));
                else builder.gt(JsonData.of(rangeValue.start));
            }
            if (rangeValue.end != null) {
                if (rangeValue.includeUpper) builder.lte(JsonData.of(rangeValue.end));
                else builder.lt(JsonData.of(rangeValue.end));
            }
            return builder;
        })._toQuery();
    }
}
