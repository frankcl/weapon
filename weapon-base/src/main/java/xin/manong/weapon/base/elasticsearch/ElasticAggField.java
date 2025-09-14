package xin.manong.weapon.base.elasticsearch;

/**
 * 聚合字段
 *
 * @author frankcl
 * @date 2025-09-14 18:32:26
 */
public class ElasticAggField {

    public String name;
    public String path;
    public boolean nested;

    public ElasticAggField(String name) {
        this.name = name;
        nested = false;
    }

    public ElasticAggField(String name, String path, boolean nested) {
        this.name = name;
        this.path = path;
        this.nested = nested;
    }
}
