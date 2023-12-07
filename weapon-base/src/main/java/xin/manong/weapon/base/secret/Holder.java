package xin.manong.weapon.base.secret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 秘钥监听器holder
 *
 * @author frankcl
 * @date 2022-12-09 14:11:00
 */
public class Holder {

    private final static Logger logger = LoggerFactory.getLogger(Holder.class);

    private static DynamicSecretListener holdListener = null;

    /**
     * hold动态秘钥监听器
     *
     * @param listener 动态秘钥监听器
     * @return 成功返回true，否则返回false
     */
    public static boolean hold(DynamicSecretListener listener) {
        if (listener == null) {
            logger.warn("provided listener is null");
            return false;
        }
        if (holdListener != null) {
            logger.error("other listener[{}] has been held", holdListener.getClass().getName());
            return false;
        }
        synchronized (Holder.class) {
            if (holdListener != null) {
                logger.error("other listener[{}] has been held", holdListener.getClass().getName());
                return false;
            }
            holdListener = listener;
            logger.info("hold listener[{}] success", listener.getClass().getName());
            return true;
        }
    }
}
