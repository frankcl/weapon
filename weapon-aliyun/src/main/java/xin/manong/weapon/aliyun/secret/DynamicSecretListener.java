package xin.manong.weapon.aliyun.secret;

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
    boolean start();

    /**
     * 处理变更秘钥
     *
     * @param changedSecret 秘钥信息
     */
    void process(String changedSecret);
}
