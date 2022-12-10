package xin.manong.weapon.alarm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 报警发送抽象类
 * 不同报警系统继承此类，实现报警接口
 *
 * @author frankcl
 * @date 2022-12-09 15:46:48
 */
public abstract class AlarmSender {

    private final static Logger logger = LoggerFactory.getLogger(AlarmSender.class);

    private final static int ALARM_QUEUE_SIZE = 200;

    AlarmConfig config;
    BlockingQueue<Alarm> alarmQueue;
    private AlarmMonitor monitor;

    public AlarmSender(AlarmConfig config) {
        this.config = config;
        alarmQueue = new ArrayBlockingQueue<>(ALARM_QUEUE_SIZE);
        monitor = new AlarmMonitor(this);
    }

    /**
     * 启动报警发送服务
     *
     * @return 启动成功返回true，否则返回false
     */
    public final boolean start() {
        logger.info("alarm sender is starting ...");
        if (config == null || !config.check()) {
            logger.error("invalid alarm config");
            return false;
        }
        if (!init()) {
            logger.error("init alarm sender failed");
            return false;
        }
        monitor.start();
        logger.info("alarm sender has been started");
        return true;
    }

    /**
     * 停止报警发送服务
     */
    public final void stop() {
        logger.info("alarm sender is stopping ...");
        monitor.stop();
        destroy();
        logger.info("alarm sender has been stopped");
    }

    /**
     * 异步投递报警
     *
     * @param alarm 报警信息
     * @return 投递成功返回true，否则返回false
     */
    public final boolean submit(Alarm alarm) {
        if (alarm == null || StringUtils.isEmpty(alarm.content)) {
            logger.warn("alarm is null or alarm content is empty, ignore it");
            return false;
        }
        if (!alarmQueue.offer(alarm)) {
            logger.warn("async alarm queue is full, ignore it");
            return false;
        }
        return true;
    }

    /**
     * 钉钉webhook URL签名
     *
     * @return webhook签名URL
     */
    protected final String signDingTalkWebHookURL() {
        if (StringUtils.isEmpty(config.dingWebHookURL)) return null;
        if (StringUtils.isEmpty(config.dingWebHookSecret)) {
            logger.warn("ding talk web hook secret is empty");
            return config.dingWebHookURL;
        }
        Long timestamp = System.currentTimeMillis();
        try {
            String sha256 = "HmacSHA256", utf8Encoding = "UTF-8";
            Mac mac = Mac.getInstance(sha256);
            mac.init(new SecretKeySpec(config.dingWebHookSecret.getBytes(utf8Encoding), sha256));
            byte[] bytes = mac.doFinal(String.format("%d\n%s", timestamp,
                    config.dingWebHookSecret).getBytes(utf8Encoding));
            String sign = URLEncoder.encode(new String(Base64.getEncoder().encode(bytes)), utf8Encoding);
            return String.format("%s&timestamp=%d&sign=%s", config.dingWebHookURL, timestamp, sign);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return config.dingWebHookURL;
        }
    }

    /**
     * 初始化
     * 子类实现覆盖此方法实现初始化操作
     *
     * @return 成功返回true，否则返回false
     */
    public boolean init() {
        return true;
    };

    /**
     * 销毁
     * 子类实现覆盖此方法实现销毁操作
     */
    public void destroy() {
    }

    /**
     * 同步发送报警
     *
     * @param alarm 报警信息
     * @return 发送成功返回true，否则返回false
     */
    public abstract boolean send(Alarm alarm);
}
