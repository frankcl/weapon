package xin.manong.weapon.base.event;

import lombok.Getter;

/**
 * 数据变更事件
 *
 * @author frankcl
 * @date 2024-11-14 14:35:08
 */
@Getter
public class ChangeEvent<T> implements Event {

    /**
     * 改变之前
     */
    private final T before;
    /**
     * 改变之后
     */
    private final T after;

    public ChangeEvent(T before, T after) {
        this.before = before;
        this.after = after;
    }
}
