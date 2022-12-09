package xin.manong.weapon.aliyun.aliyun.common;

/**
 * 重新构建接口定义
 * 需要动态重新构建对象实现该接口
 *
 * @author frankcl
 * @date 2022-10-31 15:06:18
 */
public interface Rebuildable {

    /**
     * 重新构建
     */
    default void rebuild() {
    }
}
