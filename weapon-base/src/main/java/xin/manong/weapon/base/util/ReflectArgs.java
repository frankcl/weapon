package xin.manong.weapon.base.util;

/**
 * 反射参数
 *
 * @author frankcl
 * @date 2022-07-22 12:22:02
 */
public class ReflectArgs {

    public Class[] argTypes;
    public Object[] argValues;

    public ReflectArgs() {
    }

    public ReflectArgs(Class[] argTypes, Object[] argValues) {
        this.argTypes = argTypes;
        this.argValues = argValues;
        check();
    }

    /**
     * 检测参数类型及参数值一致性
     * 如果参数类型和参数值数量不一致则抛出RuntimeException
     */
    private void check() {
        if (argValues == null && argTypes == null) return;
        if (argValues == null || argTypes == null || argTypes.length != argValues.length) {
            throw new RuntimeException("argument type and value not consistent");
        }
    }
}
