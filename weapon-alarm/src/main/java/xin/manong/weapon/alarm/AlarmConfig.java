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

    private final static int DEFAULT_MIN_ASYNC_ALARM_NUM = 10;
    private final static long DEFAULT_MIN_ASYNC_ALARM_TIME_INTERVAL_MS = 300000;

    public int minAsyncAlarmNum = DEFAULT_MIN_ASYNC_ALARM_NUM;
    public long minAsyncAlarmTimeIntervalMs = DEFAULT_MIN_ASYNC_ALARM_TIME_INTERVAL_MS;
    public String alarmSenderClass;
    public String dingWebHookURL;
    public String dingWebHookSecret;
    public Map<String, Object> alarmSenderConfig;
    public List<AlarmReceiver> alarmReceivers;
    public Map<AlarmChannel, List<String>> channelReceiverMap;

    /**
     * 检测报警配置合法性
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if ((StringUtils.isEmpty(dingWebHookURL) && !StringUtils.isEmpty(dingWebHookSecret)) ||
                (!StringUtils.isEmpty(dingWebHookURL) && StringUtils.isEmpty(dingWebHookSecret))) {
            logger.error("ding web hook info is not enough");
            return false;
        }
        if (StringUtils.isEmpty(alarmSenderClass)) {
            logger.error("alarm sender class name is empty");
            return false;
        }
        if (alarmReceivers == null || alarmReceivers.isEmpty()) {
            logger.error("alarm receiver info is empty");
            return false;
        }
        if (channelReceiverMap == null || channelReceiverMap.isEmpty()) {
            logger.error("alarm channel receiver mapping is empty");
            return false;
        }
        for (AlarmReceiver alarmReceiver : alarmReceivers) {
            if (!StringUtils.isEmpty(alarmReceiver.uid)) continue;
            logger.error("alarm receiver uid is empty");
            return false;
        }
        for (Map.Entry<AlarmChannel, List<String>> entry : channelReceiverMap.entrySet()) {
            AlarmChannel alarmChannel = entry.getKey();
            List<String> receiverIDs = entry.getValue();
            if (receiverIDs == null || receiverIDs.isEmpty()) {
                logger.error("alarm receiver info is empty for channel[{}]", alarmChannel.name());
                return false;
            }
            for (String receiverID : receiverIDs) {
                AlarmReceiver alarmReceiver = new AlarmReceiver();
                alarmReceiver.uid = receiverID;
                if (!alarmReceivers.contains(alarmReceiver)) {
                    logger.error("alarm receiver info is not found for uid[{}] and channel[{}]",
                            receiverID, alarmChannel.name());
                    return false;
                }
            }
        }
        if (minAsyncAlarmNum <= 0) minAsyncAlarmNum = DEFAULT_MIN_ASYNC_ALARM_NUM;
        if (minAsyncAlarmTimeIntervalMs <= 0) minAsyncAlarmTimeIntervalMs = DEFAULT_MIN_ASYNC_ALARM_TIME_INTERVAL_MS;
        return true;
    }
}
