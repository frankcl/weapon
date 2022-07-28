package com.manong.weapon.base.util;

/**
 * 反射参数
 *
 * @author frankcl
 * @date 2022-07-22 12:22:02
 */
public class ReflectParams {

    public Class[] types;
    public Object[] values;

    public ReflectParams() {
    }

    public ReflectParams(Class[] types, Object[] values) {
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
            throw new RuntimeException("parameter type and value not consistent");
        }
    }
}
