package xin.manong.weapon.aliyun.secret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.event.EventListener;
import xin.manong.weapon.base.event.Priority;

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
    private static EventListener currentEventListener = null;

    /**
     * 注册动态秘钥监听器
     * 1. 如果缺失监听器则成功注册
     * 2. 如果存在监听器，注册监听器优先级高于当前监听器，则成功注册
     *
     * @param eventListener 动态秘钥监听器
     */
    static void register(EventListener eventListener) {
        if (eventListener == null) return;
        int priority = getPriority(eventListener);
        if (currentEventListener != null && priority >= currentPriority) return;
        synchronized (ListenerRegistry.class) {
            if (currentEventListener != null && priority >= currentPriority) return;
            if (currentEventListener != null) {
                currentEventListener.destroy();
                logger.info("Unregister dynamic secret listener: {}", currentEventListener.getClass().getName());
            }
            currentPriority = priority;
            currentEventListener = eventListener;
            logger.info("Register dynamic secret listener success: {}", eventListener.getClass().getName());
        }
    }

    /**
     * 启动动态秘钥监听器
     */
    static void start() {
        if (currentEventListener == null) return;
        try {
            currentEventListener.init();
            logger.info("Start dynamic secret listener success: {}", currentEventListener.getClass().getName());
        } catch (Exception e) {
            logger.error("Start dynamic secret listener failed: {}", currentEventListener.getClass().getName());
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 停止动态秘钥监听器
     */
    static void stop() {
        if (currentEventListener == null) return;
        currentEventListener.destroy();
    }

    /**
     * 获取监听器优先级
     *
     * @param eventListener 监听器
     * @return 优先级
     */
    private static int getPriority(EventListener eventListener) {
        Priority priority = eventListener.getClass().getAnnotation(Priority.class);
        return priority != null ? priority.value() : DEFAULT_PRIORITY;
    }
}
