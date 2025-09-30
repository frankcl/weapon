package xin.manong.weapon.base.elasticsearch;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ES高亮数据接口
 *
 * @author frankcl
 * @date 2025-09-30 15:14:58
 */
public interface ElasticHighlightRecord extends Serializable {

    /**
     * 注入高亮结果
     *
     * @param highlightMap 高亮结果
     */
    void injectHighlight(Map<String, List<String>> highlightMap);
}
