package xin.manong.weapon.base.milvus;

/**
 * 数据更新插入请求
 *
 * @author frankcl
 * @date 2026-01-21 10:06:53
 */
public class MilvusUpsertRequest<T> extends MilvusRequest {

    public boolean partialUpdate = false;
    public T data;

    private MilvusUpsertRequest() {}

    private MilvusUpsertRequest(MilvusUpsertRequest<T> request) {
        super(request);
        data = request.data;
        partialUpdate = request.partialUpdate;
    }

    /**
     * 检测有效性
     */
    public void check() {
        super.check();
        if (data == null) throw new IllegalArgumentException("Upsert data is null");
    }

    /**
     * 请求构建器
     *
     * @param <T> 数据类型
     */
    public static class Builder<T> {

        private final MilvusUpsertRequest<T> delegate;

        public Builder() {
            delegate = new MilvusUpsertRequest<>();
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
         * 设置部分更新
         *
         * @param partialUpdate 部分更新
         * @return 构建器
         */
        public Builder<T> partialUpdate(boolean partialUpdate) {
            delegate.partialUpdate = partialUpdate;
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
        public MilvusUpsertRequest<T> build() {
            return new MilvusUpsertRequest<>(delegate);
        }
    }
}
