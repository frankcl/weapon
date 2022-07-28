package com.manong.weapon.base.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * JSON格式日志生成
 *
 * @author frankcl
 * @create 2019-10-10 14:49:02
 */
public class JSONLogger {

    private final static Logger logger = LoggerFactory.getLogger(JSONLogger.class);

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    public final static String KEY_LOGGER_TIME = "logger_time";

    /**
     * JSON日志
     *
     * @param featureMap 日志数据
     * @param keys 数据key集合，为null表示记录全部数据
     */
    public static void logging(Map<String, Object> featureMap, Set<String> keys) {
        JSONObject loggerMap = keys == null ? new JSONObject(featureMap) : new JSONObject();
        loggerMap.put(KEY_LOGGER_TIME, DATE_FORMAT.format(new Date()));
        Iterator<String> iterator = keys == null ? null : keys.iterator();
        while (iterator != null && iterator.hasNext()) {
            String key = iterator.next();
            if (!featureMap.containsKey(key)) continue;
            loggerMap.put(key, featureMap.get(key));
        }
        logger.info(JSON.toJSONString(loggerMap, SerializerFeature.DisableCircularReferenceDetect));
    }
}
