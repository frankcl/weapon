package xin.manong.weapon.base.secret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动态秘钥监听器
 *
 * @author frankcl
 * @date 2022-12-09 10:38:26
 */
public interface DynamicSecretListener {

    Logger logger = LoggerFactory.getLogger(DynamicSecretListener.class);

    /**
     * 启动监听器
     */
    default void start() {
        logger.info("dynamic secret listener[{}] has been started", this.getClass().getName());
    }

    /**
     * 订阅和处理秘钥变更
     * @param secret 秘钥信息
     */
    void process(String secret);
}
