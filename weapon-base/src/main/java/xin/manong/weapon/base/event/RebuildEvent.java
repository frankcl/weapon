package xin.manong.weapon.base.event;

import lombok.Getter;

/**
 * 对象重建事件
 *
 * @author frankcl
 * @date 2024-11-15 18:21:13
 */
@Getter
public class RebuildEvent<T> implements Event {

    /**
     * 重建目标对象
     */
    private final T buildTarget;

    public RebuildEvent(T buildTarget) {
        this.buildTarget = buildTarget;
    }
}
