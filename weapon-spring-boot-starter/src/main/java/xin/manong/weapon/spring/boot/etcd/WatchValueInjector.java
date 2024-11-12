package xin.manong.weapon.spring.boot.etcd;

import org.apache.commons.lang3.StringUtils;
import xin.manong.weapon.base.etcd.EtcdClient;
import xin.manong.weapon.base.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * WatchValue注入器
 *
 * @author frankcl
 * @date 2024-11-12 16:24:35
 */
public class WatchValueInjector {

    /**
     * 组装etcd key
     *
     * @param watchValue 监听值注解
     * @return 组装后etcd key
     */
    public static String integrateKey(WatchValue watchValue) {
        String namespace = watchValue.namespace() == null ? "" : watchValue.namespace();
        while (namespace.endsWith("/")) namespace = namespace.substring(0, namespace.length() - 1);
        String key = watchValue.key();
        while (key.startsWith("/")) key = key.substring(1);
        return String.format("%s/%s", namespace, key);
    }

    /**
     * 注入数据
     *
     * @param object 注入对象
     * @param field 注入字段
     * @param value 字段值
     */
    public static void inject(Object object, Field field, String value) {
        Class<?> fieldClass = field.getType();
        Object v;
        if (fieldClass == Map.class) {
            Type type = field.getGenericType();
            Class<?> keyClass = type instanceof ParameterizedType ?
                    (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0] : Object.class;
            Class<?> valueClass = type instanceof ParameterizedType ?
                    (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[1] : Object.class;
            v = EtcdClient.transformMap(value, keyClass, valueClass);
        } else if (fieldClass == List.class) {
            Type type = field.getGenericType();
            Class<?> valueClass = type instanceof ParameterizedType ?
                    (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0] : Object.class;
            v = EtcdClient.transformList(value, valueClass);
        } else {
            v = EtcdClient.transform(value, fieldClass);
        }
        ReflectUtil.setFieldValue(object, field.getName(), v);
    }
}
