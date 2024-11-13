package xin.manong.weapon.base.rebuild;

/**
 * 重建监听器
 *
 * @author frankcl
 * @date 2022-11-02 10:44:35
 */
public interface RebuildListener {

    /**
     * 当监听对象发生重建时，回调该方法
     *
     * @param rebuildTarget 发生重建的对象
     */
    default void onRebuild(Rebuildable rebuildTarget) {
    }
}
