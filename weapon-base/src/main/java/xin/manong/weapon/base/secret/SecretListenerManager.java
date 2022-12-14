package xin.manong.weapon.base.secret;

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
     * @return 注册成功返回true，否则返回false
     */
    public static boolean register(DynamicSecretListener listener) {
        if (listener == null) {
            logger.warn("dynamic secret listener is null, ignore registering");
            return false;
        }
        Class listenerClass = listener.getClass();
        synchronized (SecretListenerManager.class) {
            for (DynamicSecretListener registeredListener : listeners) {
                if (registeredListener == listener || listenerClass == registeredListener.getClass()) {
                    logger.warn("dynamic secret listener[{}] has been registered, ignore it", listenerClass.getName());
                    return false;
                }
            }
            if (!listeners.isEmpty()) {
                logger.error("other dynamic secret listener has been registered");
                return false;
            }
            listeners.add(listener);
            logger.info("register dynamic secret listener[{}] success", listenerClass.getName());
            return true;
        }
    }
}
