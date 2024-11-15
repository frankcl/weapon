package xin.manong.weapon.base.listen;

/**
 * 对象重建事件
 *
 * @author frankcl
 * @date 2024-11-15 18:21:13
 */
public class RebuildEvent extends Event {

    /**
     * 重建目标对象
     */
    public Object target;

    public RebuildEvent(Object target) {
        this.target = target;
    }
}
