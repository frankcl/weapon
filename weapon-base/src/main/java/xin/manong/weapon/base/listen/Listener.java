package xin.manong.weapon.base.listen;

import org.jetbrains.annotations.NotNull;

/**
 * 监听器接口定义
 *
 * @author frankcl
 * @date 2022-12-09 10:38:26
 */
public interface Listener {

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
    default void onChange(@NotNull ChangeEvent event) {}

    /**
     * 处理重建事件
     *
     * @param event 重建事件
     */
    default void onRebuild(@NotNull RebuildEvent event) {}

    /**
     * 处理事件
     *
     * @param event 事件
     */
    default void onEvent(@NotNull Event event) {
        if (event instanceof ChangeEvent) onChange((ChangeEvent) event);
        else if (event instanceof RebuildEvent) onRebuild((RebuildEvent) event);
        else throw new UnsupportedOperationException("unsupported event: " + event.getClass().getName());
    }
}
