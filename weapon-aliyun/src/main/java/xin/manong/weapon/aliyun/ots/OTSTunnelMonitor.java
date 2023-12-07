package xin.manong.weapon.aliyun.ots;

import com.alicloud.openservices.tablestore.TunnelClient;
import com.alicloud.openservices.tablestore.model.tunnel.ChannelInfo;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelRequest;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.alarm.Alarm;
import xin.manong.weapon.alarm.AlarmProducer;
import xin.manong.weapon.alarm.AlarmLevel;

import java.util.List;

/**
 * OTS通道消费监控器
 *
 * @author frankcl
 * @create 2019-06-19 16:30
 */
public class OTSTunnelMonitor implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelMonitor.class);

    private final static long DEFAULT_CHECK_TIME_INTERVAL_MS = 600000;

    private volatile boolean running = false;
    private long checkTimeIntervalMs = DEFAULT_CHECK_TIME_INTERVAL_MS;
    private String appName;
    private OTSTunnelConfig tunnelConfig;
    private TunnelClient tunnelClient;
    private AlarmProducer alarmProducer;
    private Thread workThread;

    public OTSTunnelMonitor(OTSTunnelConfig tunnelConfig, TunnelClient tunnelClient) {
        this.tunnelConfig = tunnelConfig;
        this.tunnelClient = tunnelClient;
    }

    /**
     * 启动监控
     */
    public void start() {
        logger.info("OTSTunnel monitor is starting ...");
        running = true;
        workThread = new Thread(this, "TunnelMonitor");
        workThread.start();
        logger.info("tunnel monitor has been started");
    }

    /**
     * 停止监控
     */
    public void stop() {
        logger.info("tunnel monitor is stopping ...");
        running = false;
        if (workThread.isAlive()) workThread.interrupt();
        try {
            workThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("tunnel monitor has been stopped");
    }

    @Override
    public void run() {
        while (running) {
            for (OTSTunnelWorkerConfig workerConfig : tunnelConfig.workerConfigs) check(workerConfig);
            logger.info("tunnel monitor is running, sleep {} ms", checkTimeIntervalMs);
            try {
                Thread.sleep(checkTimeIntervalMs);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 检测数据通道是否存在消费延迟
     *
     * @param workerConfig 通道worker配置
     */
    private void check(OTSTunnelWorkerConfig workerConfig) {
        DescribeTunnelRequest request = new DescribeTunnelRequest(workerConfig.table, workerConfig.tunnel);
        DescribeTunnelResponse response = tunnelClient.describeTunnel(request);
        List<ChannelInfo> channels = response.getChannelInfos();
        int delayChannelNum = 0;
        long currentTimestamp = System.currentTimeMillis();
        for (ChannelInfo channel : channels) {
            long consumeTimestamp = channel.getChannelConsumePoint().getTime();
            if (consumeTimestamp <= 0) continue;
            long timeInterval = currentTimestamp - consumeTimestamp;
            if (timeInterval < workerConfig.maxConsumeDelayMs) continue;
            logger.warn("consume delay[{}] for channel[{}] in tunnel[{}] of table[{}]", timeInterval,
                    channel.getChannelId(), workerConfig.tunnel, workerConfig.table, timeInterval);
            delayChannelNum++;
        }
        if (delayChannelNum > 0) {
            Alarm alarm = new Alarm(String.format("OTS通道[%s:%s]数据堆积: 堆积channel数量[%d], 超过最大消费延时[%d]ms",
                    workerConfig.table, workerConfig.tunnel, delayChannelNum, workerConfig.maxConsumeDelayMs),
                    AlarmLevel.ERROR).setAppName(appName).setTitle("OTS通道数据堆积报警");
            if (alarmProducer != null) alarmProducer.send(alarm);
        }
    }

    /**
     * 设置报警发送器
     *
     * @param alarmProducer 报警发送器
     */
    public void setAlarmSender(AlarmProducer alarmProducer) {
        this.alarmProducer = alarmProducer;
    }

    /**
     * 设置所属应用名
     *
     * @param appName 所属应用名
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }
}
