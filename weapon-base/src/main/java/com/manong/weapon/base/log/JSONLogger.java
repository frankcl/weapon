package com.manong.weapon.base.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    private final static String MAX_FILE_SIZE = "10MB";
    private final static String KEY_LOGGER_TIME = "logger_time";
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    private Set<String> KEYS;
    private Logger LOGGER;

    public JSONLogger(String filename, Set<String> keys) {
        try {
            String md5 = DigestUtils.md5Hex(filename);
            String name = String.format("%s$%s", JSONLogger.class.getName(), md5);
            LOGGER = LoggerFactory.getLogger(name);
            Layout layout = new PatternLayout("%m%n");
            RollingFileAppender appender = new RollingFileAppender(layout, filename);
            appender.setMaxFileSize(MAX_FILE_SIZE);
            appender.setMaxBackupIndex(10);
            org.apache.log4j.Logger logger = LogManager.getLogger(LOGGER.getName());
            logger.addAppender(appender);
            logger.setAdditivity(false);
            logger.setLevel(Level.INFO);
            KEYS = keys;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON日志
     *
     * @param featureMap 日志数据
     */
    public void logging(Map<String, Object> featureMap) {
        JSONObject loggerMap = KEYS == null ? new JSONObject(featureMap) : new JSONObject();
        loggerMap.put(KEY_LOGGER_TIME, DATE_FORMAT.format(new Date()));
        Iterator<String> iterator = KEYS == null ? null : KEYS.iterator();
        while (iterator != null && iterator.hasNext()) {
            String key = iterator.next();
            if (!featureMap.containsKey(key)) continue;
            loggerMap.put(key, featureMap.get(key));
        }
        LOGGER.info(JSON.toJSONString(loggerMap, SerializerFeature.DisableCircularReferenceDetect));
    }
}
