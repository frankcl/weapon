package xin.manong.weapon.base.milvus;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 数据插入请求
 *
 * @author frankcl
 * @date 2026-01-21 10:06:53
 */
public class MilvusDeleteRequest extends MilvusRequest {

    public List<Object> ids;
    public String filter;

    private MilvusDeleteRequest() {}

    private MilvusDeleteRequest(MilvusDeleteRequest request) {
        super(request);
        ids = request.ids;
        filter = request.filter;
    }

    /**
     * 检测有效性
     */
    public void check() {
        super.check();
        if ((ids == null || ids.isEmpty()) && StringUtils.isEmpty(filter)) {
            throw new IllegalArgumentException("Delete data ids and filter are both not set");
        }
        if ((ids != null && !ids.isEmpty()) && StringUtils.isNotEmpty(filter)) {
            throw new IllegalArgumentException("Delete data ids and filter can not be both set");
        }
    }

    /**
     * 请求构建器
     */
    public static class Builder {

        private final MilvusDeleteRequest delegate;

        public Builder() {
            delegate = new MilvusDeleteRequest();
        }

        /**
         * 设置数据ID
         *
         * @param ids 数据ID
         * @return 构建器
         */
        public Builder id(List<Object> ids) {
            delegate.ids = ids;
            return this;
        }

        /**
         * 设置过滤表达式
         *
         * @param filter 过滤表达式
         * @return 构建器
         */
        public Builder filterExpression(String filter) {
            delegate.filter = filter;
            return this;
        }

        /**
         * 设置数据库
         *
         * @param database 数据库
         * @return 构建器
         */
        public Builder database(String database) {
            delegate.database = database;
            return this;
        }

        /**
         * 设置集合
         *
         * @param collection 数据集合
         * @return 构建器
         */
        public Builder collection(String collection) {
            delegate.collection = collection;
            return this;
        }

        /**
         * 设置分区
         *
         * @param partition 分区
         * @return 构建器
         */
        public Builder partition(String partition) {
            delegate.partition = partition;
            return this;
        }

        /**
         * 构建请求对象
         *
         * @return 请求对象
         */
        public MilvusDeleteRequest build() {
            return new MilvusDeleteRequest(delegate);
        }
    }
}
