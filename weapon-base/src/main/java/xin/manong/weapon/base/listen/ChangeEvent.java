package xin.manong.weapon.base.listen;

/**
 * 数据变更事件
 *
 * @author frankcl
 * @date 2024-11-14 14:35:08
 */
public class ChangeEvent extends Event {

    /**
     * 以前值
     */
    public String prev;
    /**
     * 当前值
     */
    public String current;

    public ChangeEvent(String prev, String current) {
        this.prev = prev;
        this.current = current;
    }
}
