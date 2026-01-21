package xin.manong.weapon.base.milvus;

/**
 * Milvus数据
 *
 * @author frankcl
 * @date 2026-01-21 13:18:58
 */
public class MilvusRecord<T> {

    public Object id;
    public String primaryKey;
    public Float score;
    public T value;

    public MilvusRecord() {
    }

    private MilvusRecord(MilvusRecord<T> record) {
        id = record.id;
        primaryKey = record.primaryKey;
        score = record.score;
        value = record.value;
    }

    /**
     * 构建器
     */
    public static class Builder<T> {

        private final MilvusRecord<T> delegate;

        public Builder() {
            delegate = new MilvusRecord<>();
        }

        /**
         * 设置ID
         *
         * @param id ID
         * @return 构建器
         */
        public MilvusRecord.Builder<T> id(Object id) {
            delegate.id = id;
            return this;
        }

        /**
         * 设置主键
         *
         * @param primaryKey 主键
         * @return 构建器
         */
        public MilvusRecord.Builder<T> primaryKey(String primaryKey) {
            delegate.primaryKey = primaryKey;
            return this;
        }

        /**
         * 设置分数
         *
         * @param score 分数
         * @return 构建器
         */
        public MilvusRecord.Builder<T> score(Float score) {
            delegate.score = score;
            return this;
        }

        /**
         * 设置数据
         *
         * @param value 数据
         * @return 构建器
         */
        public MilvusRecord.Builder<T> value(T value) {
            delegate.value = value;
            return this;
        }

        /**
         * 构建Milvus数据
         *
         * @return Milvus数据
         */
        public MilvusRecord<T> build() {
            return new MilvusRecord<>(delegate);
        }
    }

}
