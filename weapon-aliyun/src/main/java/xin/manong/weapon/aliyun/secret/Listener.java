package xin.manong.weapon.aliyun.secret;

/**
 * 动态秘钥监听器接口
 *
 * @author frankcl
 * @date 2022-12-09 10:38:26
 */
public interface Listener {

    /**
     * 启动监听器
     *
     * @exception Exception 启动失败抛出异常
     */
    default void start() throws Exception {}

    /**
     * 处理变更秘钥
     *
     * @param event 变更事件
     */
    void onChange(ChangeEvent event);
}
