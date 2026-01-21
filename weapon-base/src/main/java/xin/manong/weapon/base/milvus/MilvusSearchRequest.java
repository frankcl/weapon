package xin.manong.weapon.base.milvus;

import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.vector.request.data.BaseVector;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 数据搜索请求
 *
 * @author frankcl
 * @date 2026-01-21 10:06:53
 */
public class MilvusSearchRequest extends MilvusRequest {

    public int offset = 0;
    public int limit = 10;
    public BaseVector vector;
    public String field;
    public String filter;
    public IndexParam.MetricType metricType;
    public List<String> outputFields;
    public List<String> partitions;
    public Map<String, Object> paramMap;

    private MilvusSearchRequest() {}

    private MilvusSearchRequest(MilvusSearchRequest request) {
        super(request);
        offset = request.offset;
        limit = request.limit;
        vector = request.vector;
        field = request.field;
        filter = request.filter;
        metricType = request.metricType;
        partitions = request.partitions;
        outputFields = request.outputFields;
        paramMap = request.paramMap;
    }

    /**
     * 检测有效性
     */
    public void check() {
        super.check();
        if (StringUtils.isEmpty(field)) throw new IllegalArgumentException("Ann field is empty");
        if (vector == null) throw new IllegalArgumentException("Search vector is empty");
        if (metricType == null) metricType = IndexParam.MetricType.COSINE;
        if (paramMap == null) paramMap = new HashMap<>();
        if (StringUtils.isNotEmpty(partition)) {
            if (partitions == null) partitions = new ArrayList<>();
            if (!partitions.contains(partition)) partitions.add(partition);
        }
        if (offset < 0) offset = 0;
        if (limit <= 0) limit = 10;
    }

    /**
     * 请求构建器
     */
    public static class Builder {

        private final MilvusSearchRequest delegate;

        public Builder() {
            delegate = new MilvusSearchRequest();
        }

        /**
         * 设置搜索向量
         *
         * @param vector 搜索向量
         * @return 构建器
         */
        public Builder vector(BaseVector vector) {
            delegate.vector = vector;
            return this;
        }

        /**
         * 设置数据偏移
         *
         * @param offset 数据偏移
         * @return 构建器
         */
        public Builder offset(int offset) {
            delegate.offset = offset;
            return this;
        }

        /**
         * 设置数量
         *
         * @param limit 数量
         * @return 构建器
         */
        public Builder limit(int limit) {
            delegate.limit = limit;
            return this;
        }

        /**
         * 设置向量字段
         *
         * @param field 向量字段
         * @return 构建器
         */
        public Builder field(String field) {
            delegate.field = field;
            return this;
        }

        /**
         * 设置过滤条件
         *
         * @param filter 过滤条件
         * @return 构建器
         */
        public Builder filter(String filter) {
            delegate.filter = filter;
            return this;
        }

        /**
         * 设置度量类型
         *
         * @param metricType 度量类型
         * @return 构建器
         */
        public Builder metricType(IndexParam.MetricType metricType) {
            delegate.metricType = metricType;
            return this;
        }

        /**
         * 设置输出字段
         *
         * @param outputFields 输出字段
         * @return 构建器
         */
        public Builder outputFields(List<String> outputFields) {
            delegate.outputFields = outputFields;
            return this;
        }

        /**
         * 设置分区
         *
         * @param partitions 分区
         * @return 构建器
         */
        public Builder partitions(List<String> partitions) {
            delegate.partitions = partitions;
            return this;
        }

        /**
         * 设置搜索参数
         *
         * @param paramMap 搜索参数
         * @return 构建器
         */
        public Builder paramMap(Map<String, Object> paramMap) {
            delegate.paramMap = paramMap;
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
        public MilvusSearchRequest build() {
            return new MilvusSearchRequest(delegate);
        }
    }
}
