package xin.manong.weapon.base.milvus;

import org.apache.commons.lang3.StringUtils;

/**
 * Milvus请求
 *
 * @author frankcl
 * @date 2026-01-21 09:44:54
 */
public class MilvusRequest {

    public String database;
    public String collection;
    public String partition;

    protected MilvusRequest() {
    }

    protected MilvusRequest(MilvusRequest request) {
        database = request.database;
        collection = request.collection;
        partition = request.partition;
    }

    /**
     * 检测有效性
     */
    public void check() {
        if (StringUtils.isEmpty(collection)) {
            throw new IllegalArgumentException("Collection is empty");
        }
    }
}
