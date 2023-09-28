package xin.manong.weapon.base.util;

/**
 * 反射参数
 *
 * @author frankcl
 * @date 2022-07-22 12:22:02
 */
public class ReflectArgs {

    public Class[] types;
    public Object[] values;

    public ReflectArgs() {
    }

    public ReflectArgs(Class[] types, Object[] values) {
        this.types = types;
        this.values = values;
        check();
    }

    /**
     * 检测参数类型及参数值一致性
     * 如果参数类型和参数值数量不一致则抛出RuntimeException
     */
    private void check() {
        if (values == null && types == null) return;
        if (values == null || types == null || types.length != values.length) {
            throw new RuntimeException("argument type and value not consistent");
        }
    }
}
