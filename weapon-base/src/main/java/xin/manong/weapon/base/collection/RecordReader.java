package xin.manong.weapon.base.collection;

/**
 * 数据读取接口
 *
 * @author frankcl
 * @date 2023-04-27 14:37:22
 */
interface RecordReader<T> {

    /**
     * 打开数据读取器
     *
     * @return 成功返回true，否则返回false
     */
    boolean open();

    /**
     * 关闭数据读取器
     */
    void close();

    /**
     * 读取数据
     *
     * @return 成功返回数据，否则返回null
     */
    T read();

    /**
     * 获取当前数据，不移动读取游标
     *
     * @return 成功返回数据，否则返回null
     */
    T peak();
}
