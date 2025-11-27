package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.json.JsonData;
import xin.manong.weapon.base.common.RangeValue;

import java.util.ArrayList;
import java.util.List;

/**
 * ElasticSearch查询工具
 *
 * @author frankcl
 * @date 2025-11-27 15:17:32
 */
public class ElasticSearchQuery {

    /**
     * 转换term查询
     *
     * @param field 字段名
     * @param value 字段值
     * @return term查询
     */
    public static Query convertTermQuery(String field, Object value) {
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
        List<FieldValue> fieldValues = new ArrayList<>();
        for (Object value : values) fieldValues.add(FieldValue.of(value));
        return TermsQuery.of(builder -> builder.field(field).terms(f -> f.value(fieldValues)))._toQuery();
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
