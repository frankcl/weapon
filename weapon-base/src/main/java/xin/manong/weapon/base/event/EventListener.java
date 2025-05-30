package xin.manong.weapon.base.event;

import org.jetbrains.annotations.NotNull;

/**
 * 监听器接口定义
 *
 * @author frankcl
 * @date 2022-12-09 10:38:26
 */
public interface EventListener {

    /**
     * 初始化监听器
     *
     * @exception Exception 启动失败抛出异常
     */
    default void init() throws Exception {}

    /**
     * 销毁监听器
     */
    default void destroy() {}

    /**
     * 处理变更事件
     *
     * @param event 变更事件
     */
    default void onChange(@NotNull ChangeEvent<?> event) {}

    /**
     * 处理重建事件
     *
     * @param event 重建事件
     */
    default void onRebuild(@NotNull RebuildEvent<?> event) {}

    /**
     * 处理错误事件
     *
     * @param event 错误事件
     */
    default void onError(@NotNull ErrorEvent event) {}

    /**
     * 处理启动事件
     *
     * @param event 启动事件
     */
    default void onStart(@NotNull StartEvent event) {}

    /**
     * 处理停止事件
     *
     * @param event 停止事件
     */
    default void onStop(@NotNull StopEvent event) {}

    /**
     * 处理事件
     *
     * @param event 事件
     */
    default void onEvent(@NotNull Event event) {
        if (event instanceof ChangeEvent) onChange((ChangeEvent<?>) event);
        else if (event instanceof RebuildEvent) onRebuild((RebuildEvent<?>) event);
        else if (event instanceof ErrorEvent) onError((ErrorEvent) event);
        else throw new UnsupportedOperationException("unsupported event: " + event.getClass().getName());
    }
}
