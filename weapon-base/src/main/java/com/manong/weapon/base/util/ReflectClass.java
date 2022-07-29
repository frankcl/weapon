package com.manong.weapon.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射类包装
 *
 * @author frankcl
 * @create 2019-05-27 16:23
 */
class ReflectClass {

    private final static Logger logger = LoggerFactory.getLogger(ReflectClass.class);

    private Class clazz;
    private Map<String, Constructor> constructorMap;
    private Map<String, Field> fieldMap;
    private Map<String, Method> methodMap;

    public ReflectClass(Class clazz) {
        this.clazz = clazz;
        constructorMap = new HashMap<>();
        fieldMap = new HashMap<>();
        methodMap = new HashMap<>();
    }

    /**
     * 根据字段名获取字段
     *
     * @param fieldName 字段名
     * @return 如果成功返回字段，否则抛出RuntimeException
     */
    public Field getField(String fieldName) {
        try {
            if (fieldMap.containsKey(fieldName)) return fieldMap.get(fieldName);
            synchronized (fieldMap) {
                if (fieldMap.containsKey(fieldName)) return fieldMap.get(fieldName);
                Field field = deepGetField(fieldName);
                fieldMap.put(fieldName, field);
                return field;
            }
        } catch (Exception e) {
            logger.error("get field[{}] failed for class[{}]", fieldName, clazz.getName());
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据方法签名获取方法
     *
     * @param methodName 方法名
     * @param parameterTypes 方法参数类型
     * @return 如果方法存咋返回方法对象，否则抛出RuntimeException
     */
    public Method getMethod(String methodName, Class... parameterTypes) {
        String key = getMethodKey(methodName, parameterTypes);
        try {
            if (methodMap.containsKey(key)) return methodMap.get(key);
            synchronized (methodMap) {
                if (methodMap.containsKey(key)) return methodMap.get(key);
                Method method = deepGetMethod(methodName, parameterTypes);
                method.setAccessible(true);
                methodMap.put(key, method);
                return method;
            }
        } catch (Exception e) {
            logger.error("get method[{}] failed for class[{}]", key, clazz.getName());
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取构造器方法
     *
     * @param parameterTypes 构造器参数类型
     * @return 如果成功返回构造器实例，否则抛出RuntimeException
     */
    public Constructor getConstructor(Class... parameterTypes) {
        String key = getMethodKey("", parameterTypes);
        try {
            if (constructorMap.containsKey(key)) return constructorMap.get(key);
            synchronized (constructorMap) {
                if (constructorMap.containsKey(key)) return constructorMap.get(key);
                Constructor constructor = clazz.getConstructor(parameterTypes);
                constructor.setAccessible(true);
                constructorMap.put(key, constructor);
                return constructor;
            }
        } catch (Exception e) {
            logger.error("get constructor[{}] failed for class[{}]", key, clazz.getName());
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据字段名获取字段
     * 深度搜索父类字段
     *
     * @param fieldName 字段名
     * @return 字段
     * @throws NoSuchFieldException 如果字段不存在抛出该异常
     */
    private Field deepGetField(String fieldName) throws NoSuchFieldException {
        Class c = clazz;
        while (c != null) {
            try {
                return c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    /**
     * 获取方法
     * 深度搜索父类方法
     *
     * @param methodName 方法名
     * @param parameterTypes 方法参数
     * @return 方法
     * @throws NoSuchMethodException 如果方法不存在抛出该异常
     */
    private Method deepGetMethod(String methodName, Class... parameterTypes) throws NoSuchMethodException {
        Class c = clazz;
        while (c != null) {
            try {
                return c.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchMethodException(getMethodKey(methodName, parameterTypes));
    }

    /**
     * 根据方法签名获取方法key，用于方法缓存map key
     *
     * @param methodName 方法名
     * @param parameterTypes 方法参数类型
     * @return 方法key
     */
    private String getMethodKey(String methodName, Class... parameterTypes) {
        StringBuffer buffer = new StringBuffer(methodName);
        buffer.append("(");
        for (int i = 0; parameterTypes != null && i < parameterTypes.length; i++) {
            if (i > 0) buffer.append(",");
            buffer.append(parameterTypes[i].getName());
        }
        buffer.append(")");
        return buffer.toString();
    }
}
