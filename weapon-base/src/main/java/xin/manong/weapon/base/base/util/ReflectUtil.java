package xin.manong.weapon.base.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 反射工具
 *
 * @author frankcl
 * @create 2019-05-27 16:23
 */
public class ReflectUtil {

    private final static Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    private static Map<Class, ReflectClass> reflectClassMap = new HashMap<>();

    /**
     * 获取目标对象指定字段值
     *
     * @param object 目标对象
     * @param fieldName 字段名
     * @return 如果字段存在返回字段值，否则抛出RuntimeException
     */
    public static Object getFieldValue(Object object, String fieldName) {
        try {
            ReflectClass reflectClass = getReflectClass(object.getClass());
            Field field = reflectClass.getField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            logger.error("get value failed for field[{}] of class[{}]", fieldName, object.getClass().getName());
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置目标对象指定字段值
     *
     * @param object 目标对象
     * @param fieldName 字段名
     * @param fieldValue 字段值
     */
    public static void setFieldValue(Object object, String fieldName, Object fieldValue) {
        try {
            ReflectClass reflectClass = getReflectClass(object.getClass());
            Field field = reflectClass.getField(fieldName);
            field.setAccessible(true);
            field.set(object, fieldValue);
        } catch (Exception e) {
            logger.error("set value failed for field[{}] of class[{}]", fieldName, object.getClass().getName());
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 方法调用
     *
     * @param methodName 方法名
     * @param object 目标对象
     * @param params 方法参数
     * @return 如果调用成功返回结果，否则抛出RuntimeException
     */
    public static Object invoke(String methodName, Object object, ReflectParams params) {
        ReflectClass reflectClass = getReflectClass(object.getClass());
        Method method = reflectClass.getMethod(methodName, params == null ? null : params.types);
        try {
            return method.invoke(object, params == null ? null : params.values);
        } catch (Exception e) {
            logger.error("invoke method[{}] failed for class[{}]", method.getName(), object.getClass().getName());
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建对象实例
     *
     * @param className 类全限定名
     * @param params 构造参数
     * @return 如果成功返回实例对象，否则抛出RuntimeException
     */
    public static Object newInstance(String className, ReflectParams params) {
        try {
            Class clazz = Class.forName(className);
            return newInstance(clazz, params);
        } catch (Exception e) {
            logger.error("create instance failed for class[{}]", className);
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建对象实例
     *
     * @param clazz 类实例
     * @param params 构造参数
     * @return 如果成功返回实例对象，否则抛出RuntimeException
     */
    public static Object newInstance(Class clazz, ReflectParams params) {
        ReflectClass reflectClass = getReflectClass(clazz);
        Constructor constructor = reflectClass.getConstructor(params == null ? null : params.types);
        try {
            return constructor.newInstance(params == null ? null : params.values);
        } catch (Exception e) {
            logger.error("create instance failed for class[{}]", clazz.getName());
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象所有字段
     *
     * @param object 对象
     * @return 字段数组
     */
    public static Field[] getFields(Object object) {
        List<Field> fields = new ArrayList<>();
        if (object == null) return fields.toArray(new Field[0]);
        Class clazz = object.getClass();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * 获取ReflectClass
     *
     * @param clazz class对象
     * @return 如果成功返回ReflectClass，否则抛出RuntimeException
     */
    private static ReflectClass getReflectClass(Class clazz) {
        if (reflectClassMap.containsKey(clazz)) return reflectClassMap.get(clazz);
        synchronized (reflectClassMap) {
            if (reflectClassMap.containsKey(clazz)) return reflectClassMap.get(clazz);
            ReflectClass reflectClass = new ReflectClass(clazz);
            reflectClassMap.put(clazz, reflectClass);
            return reflectClass;
        }
    }
}
