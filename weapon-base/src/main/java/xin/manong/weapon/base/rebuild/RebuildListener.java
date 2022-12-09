package xin.manong.weapon.base.rebuild;

/**
 * 重建监听器
 *
 * @author frankcl
 * @date 2022-11-02 10:44:35
 */
public interface RebuildListener {

    /**
     * 通知监听对象重建事件
     *
     * @param rebuildObject 重建对象
     */
    default void notifyRebuildEvent(Rebuildable rebuildObject) {
    }
}
