package xin.manong.weapon.alarm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 报警配置信息
 *
 * @author frankcl
 * @date 2022-12-09 16:16:29
 */
public class AlarmConfig {

    private final static Logger logger = LoggerFactory.getLogger(AlarmConfig.class);

    private final static int DEFAULT_BATCH_ALARM_NUM = 10;
    private final static long DEFAULT_ASYNC_ALARM_INTERVAL_MS = 300000;

    /* 批处理报警数量 */
    public int batchAlarmNum = DEFAULT_BATCH_ALARM_NUM;
    /* 异步报警时间间隔，单位：毫秒 */
    public long asyncAlarmIntervalMs = DEFAULT_ASYNC_ALARM_INTERVAL_MS;
    /* 报警发送生产全限定类名 */
    public String producerClass;
    /* 报警发送生产配置信息 */
    public Map<String, Object> producerConfig;
    /* 报警接收人信息 */
    public Map<AlarmChannel, List<String>> channelReceiverMap;

    /**
     * 检测报警配置合法性
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(producerClass)) {
            logger.error("alarm producer class is empty");
            return false;
        }
        if (channelReceiverMap == null || channelReceiverMap.isEmpty()) {
            logger.error("alarm channel receiver mapping is empty");
            return false;
        }
        for (Map.Entry<AlarmChannel, List<String>> entry : channelReceiverMap.entrySet()) {
            AlarmChannel alarmChannel = entry.getKey();
            List<String> receivers = entry.getValue();
            if (receivers == null || receivers.isEmpty()) {
                logger.error("alarm receivers are empty for channel[{}]", alarmChannel.name());
                return false;
            }
        }
        if (batchAlarmNum <= 0) batchAlarmNum = DEFAULT_BATCH_ALARM_NUM;
        if (asyncAlarmIntervalMs <= 0) asyncAlarmIntervalMs = DEFAULT_ASYNC_ALARM_INTERVAL_MS;
        return true;
    }
}
