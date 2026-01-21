package xin.manong.weapon.aliyun.log;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志搜索请求
 *
 * @author frankcl
 * @date 2023-05-17 17:27:55
 */
public class LogSearchRequest {

    private static final Logger logger = LoggerFactory.getLogger(LogSearchRequest.class);

    public static final Long DEFAULT_OFFSET = 0L;
    public static final Long DEFAULT_LINES = 20L;

    public String project;
    public String logStore;
    public String topic;
    public String query;
    public Long offset = DEFAULT_OFFSET;
    public Long lines = DEFAULT_LINES;
    public Long startTime;
    public Long stopTime;
    public Boolean reverse = false;

    /**
     * 检测搜索请求有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(project)) {
            logger.error("Project is empty");
            return false;
        }
        if (StringUtils.isEmpty(logStore)) {
            logger.error("Log store is empty");
            return false;
        }
        if (StringUtils.isEmpty(query)) {
            logger.error("Search query is empty");
            return false;
        }
        if (stopTime == null || stopTime <= 0) stopTime = System.currentTimeMillis();
        if (startTime == null || startTime <= 0) startTime = stopTime - 86400000L;
        if (offset == null || offset < 0L) offset = DEFAULT_OFFSET;
        if (lines == null || lines <= 0L) lines = DEFAULT_LINES;
        if (reverse == null) reverse = false;
        return true;
    }
}
