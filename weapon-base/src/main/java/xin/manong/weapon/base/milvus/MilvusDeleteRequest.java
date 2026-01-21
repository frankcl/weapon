package xin.manong.weapon.base.milvus;

/**
 * 数据插入请求
 *
 * @author frankcl
 * @date 2026-01-21 10:06:53
 */
public class MilvusDeleteRequest extends MilvusRequest {

    public Object id;

    private MilvusDeleteRequest() {}

    private MilvusDeleteRequest(MilvusDeleteRequest request) {
        super(request);
        id = request.id;
    }

    /**
     * 检测有效性
     */
    public void check() {
        super.check();
        if (id == null) throw new IllegalArgumentException("Delete data id is null");
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
         * @param id 数据ID
         * @return 构建器
         */
        public Builder id(Object id) {
            delegate.id = id;
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
