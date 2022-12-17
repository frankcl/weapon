package xin.manong.weapon.base.secret;

/**
 * 动态秘钥监听器
 *
 * @author frankcl
 * @date 2022-12-09 10:38:26
 */
public interface DynamicSecretListener {

    /**
     * 启动监听器
     */
    default void start() {
    }

    /**
     * 订阅和处理秘钥变更
     * @param secret 秘钥信息
     */
    void process(String secret);
}
