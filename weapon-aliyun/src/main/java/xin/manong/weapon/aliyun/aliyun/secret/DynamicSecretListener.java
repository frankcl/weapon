package xin.manong.weapon.aliyun.aliyun.secret;

/**
 * 动态秘钥监听器
 *
 * @author frankcl
 * @date 2022-12-09 10:38:26
 */
public interface DynamicSecretListener {

    /**
     * 订阅和处理秘钥变更
     */
    void process();
}
