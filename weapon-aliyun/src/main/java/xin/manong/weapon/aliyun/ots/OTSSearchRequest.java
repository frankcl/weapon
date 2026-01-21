package xin.manong.weapon.aliyun.ots;

import com.alicloud.openservices.tablestore.model.search.query.Query;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * OTS搜索请求
 *
 * @author frankcl
 * @date 2023-02-28 15:29:29
 */
public class OTSSearchRequest {

    private final static Logger logger = LoggerFactory.getLogger(OTSSearchRequest.class);

    private final static int DEFAULT_OFFSET = 0;
    private final static int DEFAULT_LIMIT = 20;
    private final static int DEFAULT_SEARCH_TIMEOUT_MS = 10000;

    public Integer offset = DEFAULT_OFFSET;
    public Integer limit = DEFAULT_LIMIT;
    public Integer searchTimeoutMs = DEFAULT_SEARCH_TIMEOUT_MS;
    public String tableName;
    public String indexName;
    public List<String> returnColumns;
    public Query query;

    public static class Builder {
        private final OTSSearchRequest request;

        public Builder() {
            request = new OTSSearchRequest();
        }

        /**
         * 设置表名
         *
         * @param tableName 表名
         * @return Builder
         */
        public Builder tableName(String tableName) {
            request.tableName = tableName;
            return this;
        }

        /**
         * 设置索引名
         *
         * @param indexName 索引名
         * @return Builder
         */
        public Builder indexName(String indexName) {
            request.indexName = indexName;
            return this;
        }

        /**
         * 设置返回列列表
         *
         * @param returnColumns 返回列列表
         * @return Builder
         */
        public Builder returnColumns(List<String> returnColumns) {
            request.returnColumns = returnColumns;
            return this;
        }

        /**
         * 设置搜索条件
         *
         * @param query 搜索条件
         * @return Builder
         */
        public Builder query(Query query) {
            request.query = query;
            return this;
        }

        /**
         * 设置数据偏移
         *
         * @param offset 数据偏移
         * @return Builder
         */
        public Builder offset(Integer offset) {
            request.offset = offset;
            return this;
        }

        /**
         * 设置返回数量
         *
         * @param limit 返回数量
         * @return Builder
         */
        public Builder limit(Integer limit) {
            request.limit = limit;
            return this;
        }

        /**
         * 设置搜索超时
         *
         * @param searchTimeoutMs 搜索超时
         * @return Builder
         */
        public Builder searchTimeoutMs(Integer searchTimeoutMs) {
            request.searchTimeoutMs = searchTimeoutMs;
            return this;
        }

        /**
         * 构建请求对象
         *
         * @return 请求对象
         */
        public OTSSearchRequest build() {
            OTSSearchRequest replica = new OTSSearchRequest();
            replica.tableName = request.tableName;
            replica.indexName = request.indexName;
            replica.searchTimeoutMs = request.searchTimeoutMs;
            replica.returnColumns = request.returnColumns;
            replica.query = request.query;
            replica.offset = request.offset;
            replica.limit = request.limit;
            return replica;
        }
    }

    /**
     * 检测搜索请求合法性
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (offset == null || offset < 0) offset = DEFAULT_OFFSET;
        if (limit == null || limit <= 0) limit = DEFAULT_LIMIT;
        if (searchTimeoutMs == null || searchTimeoutMs <= 0) limit = DEFAULT_SEARCH_TIMEOUT_MS;
        if (StringUtils.isEmpty(tableName)) {
            logger.error("Table name is empty");
            return false;
        }
        if (StringUtils.isEmpty(indexName)) {
            logger.error("Index name is empty");
            return false;
        }
        if (query == null) {
            logger.error("Search query is null");
            return false;
        }
        return true;
    }
}
