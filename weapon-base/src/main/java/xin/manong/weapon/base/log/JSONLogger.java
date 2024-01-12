package xin.manong.weapon.base.log;

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
    private final static int MAX_BACKUP_INDEX = 10;
    private final static String MAX_FILE_SIZE = "10MB";
    private final static String KEY_LOGGER_TIME = "__LOGGER_TIME__";
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    private Set<String> logKeys;
    private Logger slf4jLogger;

    public JSONLogger(String filename, Set<String> keys) {
        try {
            String md5 = DigestUtils.md5Hex(filename);
            String name = String.format("%s$%s", JSONLogger.class.getName(), md5);
            slf4jLogger = LoggerFactory.getLogger(name);
            Layout layout = new PatternLayout("%m%n");
            RollingFileAppender appender = new RollingFileAppender(layout, filename);
            appender.setMaxFileSize(MAX_FILE_SIZE);
            appender.setMaxBackupIndex(MAX_BACKUP_INDEX);
            org.apache.log4j.Logger logger = LogManager.getLogger(slf4jLogger.getName());
            logger.addAppender(appender);
            logger.setAdditivity(false);
            logger.setLevel(Level.INFO);
            logKeys = keys;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 提交日志
     *
     * @param featureMap 日志数据
     */
    public void commit(Map<String, Object> featureMap) {
        JSONObject loggerMap = logKeys == null ? new JSONObject(featureMap) : new JSONObject();
        loggerMap.put(KEY_LOGGER_TIME, DATE_FORMAT.format(new Date()));
        Iterator<String> iterator = logKeys == null ? null : logKeys.iterator();
        while (iterator != null && iterator.hasNext()) {
            String key = iterator.next();
            if (!featureMap.containsKey(key)) continue;
            loggerMap.put(key, featureMap.get(key));
        }
        slf4jLogger.info(JSON.toJSONString(loggerMap, SerializerFeature.DisableCircularReferenceDetect));
    }
}
