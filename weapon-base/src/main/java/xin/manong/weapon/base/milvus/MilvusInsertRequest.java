package xin.manong.weapon.base.milvus;

/**
 * 数据插入请求
 *
 * @author frankcl
 * @date 2026-01-21 10:06:53
 */
public class MilvusInsertRequest<T> extends MilvusRequest {

    public T data;

    private MilvusInsertRequest() {}

    private MilvusInsertRequest(MilvusInsertRequest<T> request) {
        super(request);
        data = request.data;
    }

    /**
     * 检测有效性
     */
    public void check() {
        super.check();
        if (data == null) throw new IllegalArgumentException("Insert data is null");
    }

    /**
     * 请求构建器
     *
     * @param <T> 数据类型
     */
    public static class Builder<T> {

        private final MilvusInsertRequest<T> delegate;

        public Builder() {
            delegate = new MilvusInsertRequest<>();
        }

        /**
         * 设置数据
         *
         * @param data 数据
         * @return 构建器
         */
        public Builder<T> data(T data) {
            delegate.data = data;
            return this;
        }

        /**
         * 设置数据库
         *
         * @param database 数据库
         * @return 构建器
         */
        public Builder<T> database(String database) {
            delegate.database = database;
            return this;
        }

        /**
         * 设置集合
         *
         * @param collection 数据集合
         * @return 构建器
         */
        public Builder<T> collection(String collection) {
            delegate.collection = collection;
            return this;
        }

        /**
         * 设置分区
         *
         * @param partition 分区
         * @return 构建器
         */
        public Builder<T> partition(String partition) {
            delegate.partition = partition;
            return this;
        }

        /**
         * 构建请求对象
         *
         * @return 请求对象
         */
        public MilvusInsertRequest<T> build() {
            return new MilvusInsertRequest<>(delegate);
        }
    }
}
