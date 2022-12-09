package xin.manong.weapon.aliyun.secret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 秘钥监听器管理器
 * 管理监听器生命周期
 *
 * @author frankcl
 * @date 2022-12-09 14:11:00
 */
public class SecretListenerManager {

    private final static Logger logger = LoggerFactory.getLogger(SecretListenerManager.class);

    private static List<DynamicSecretListener> listeners = new ArrayList<>();

    /**
     * 注册动态秘钥监听器
     *
     * @param listener 动态秘钥监听器
     */
    public static void register(DynamicSecretListener listener) {
        if (listener == null) {
            logger.warn("dynamic secret listener is null, ignore registering");
            return;
        }
        Class listenerClass = listener.getClass();
        for (DynamicSecretListener registeredListener : listeners) {
            if (registeredListener == listener || listenerClass == registeredListener.getClass()) {
                logger.warn("dynamic secret listener[{}] has been registered, ignore it", listenerClass.getName());
                return;
            }
        }
        if (!listeners.isEmpty()) logger.warn("other dynamic secret listener has been registered");
        listeners.add(listener);
        logger.info("register dynamic secret listener[{}] success", listenerClass.getName());
    }
}
