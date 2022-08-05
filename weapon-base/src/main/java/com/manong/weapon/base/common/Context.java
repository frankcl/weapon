package com.manong.weapon.base.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文：用于流程中透传信息
 *
 * @author frankcl
 * @create 2019-10-09 16:49:17
 */
public class Context {

    public Map<String, Object> featureMap;

    public Context() {
        featureMap = new HashMap<>();
    }

    /**
     * 是否包含feature
     *
     * @param key feature key
     * @return 如果包含返回true，否则返回false
     */
    public boolean contains(String key) {
        return featureMap.containsKey(key);
    }

    /**
     * 添加feature
     *
     * @param key feature key
     * @param value feature value
     */
    public void put(String key, Object value) {
        featureMap.put(key, value);
    }

    /**
     * 获取feature
     *
     * @param key feature key
     * @return 如果feature存在，返回feature value，否则返回null
     */
    public Object get(String key) {
        return featureMap.getOrDefault(key, null);
    }

    /**
     * 删除feature
     *
     * @param key feature key
     */
    public void remove(String key) {
        if (featureMap.containsKey(key)) featureMap.remove(key);
    }

    /**
     * 清理上下文
     */
    public void sweep() {
        featureMap.clear();
        featureMap = new HashMap<>();
    }

    /**
     * 获取内部feature map
     *
     * @return feature map对象
     */
    public Map<String, Object> getFeatureMap() {
        return featureMap;
    }
}
