package xin.manong.weapon.aliyun.secret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动态秘钥监听器注册
 * 保留优先级最高的秘钥监听器
 *
 * @author frankcl
 * @date 2022-12-09 14:11:00
 */
public class ListenerRegistry {

    private final static Logger logger = LoggerFactory.getLogger(ListenerRegistry.class);

    private static final int DEFAULT_PRIORITY = 1000;
    private static int currentPriority = DEFAULT_PRIORITY;
    private static Listener currentListener = null;

    /**
     * 注册动态秘钥监听器
     * 1. 如果缺失监听器则成功注册
     * 2. 如果存在监听器，注册监听器优先级高于当前监听器，则成功注册
     *
     * @param listener 动态秘钥监听器
     */
    static void register(Listener listener) {
        if (listener == null) return;
        int priority = getPriority(listener);
        if (currentListener != null && priority >= currentPriority) return;
        synchronized (ListenerRegistry.class) {
            if (currentListener != null && priority >= currentPriority) return;
            if (currentListener != null) {
                logger.info("unregister dynamic secret listener: {}", currentListener.getClass().getName());
            }
            currentPriority = priority;
            currentListener = listener;
            logger.info("register dynamic secret listener success: {}", listener.getClass().getName());
        }
    }

    /**
     * 启动动态秘钥监听器
     */
    static void start() {
        if (currentListener == null) return;
        try {
            currentListener.start();
            logger.info("start dynamic secret listener success: {}", currentListener.getClass().getName());
        } catch (Exception e) {
            logger.error("start dynamic secret listener failed: {}", currentListener.getClass().getName());
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取监听器优先级
     *
     * @param listener 监听器
     * @return 优先级
     */
    private static int getPriority(Listener listener) {
        Priority priority = listener.getClass().getAnnotation(Priority.class);
        return priority != null ? priority.value() : DEFAULT_PRIORITY;
    }
}
