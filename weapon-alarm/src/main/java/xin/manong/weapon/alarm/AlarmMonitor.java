package xin.manong.weapon.alarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 报警监控器
 *
 * @author frankcl
 * @date 2022-12-09 15:46:21
 */
public class AlarmMonitor implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(AlarmMonitor.class);

    private final static String NAME = "AlarmMonitor";

    private boolean running = false;
    private AlarmConfig config;
    private AlarmSender alarmSender;
    private Thread monitorThread;
    private BlockingQueue<Alarm> alarmQueue;

    public AlarmMonitor(AlarmSender alarmSender) {
        this.config = alarmSender.config;
        this.alarmSender = alarmSender;
        this.alarmQueue = alarmSender.alarmQueue;
    }

    /**
     * 启动报警监控
     */
    public final void start() {
        logger.info("alarm monitor is starting ...");
        if (running || (monitorThread != null && monitorThread.isAlive())) {
            logger.warn("alarm monitor has been started, ignore request");
            return;
        }
        running = true;
        monitorThread = new Thread(this, NAME);
        monitorThread.start();
        logger.info("alarm monitor has been started");
    }

    /**
     * 停止报警监控
     */
    public final void stop() {
        logger.info("alarm monitor is stopping ...");
        if (!running || monitorThread == null || !monitorThread.isAlive()) {
            logger.warn("alarm monitor has been stopped, ignore request");
            return;
        }
        running = false;
        if (monitorThread != null && monitorThread.isAlive()) monitorThread.interrupt();
        try {
            if (monitorThread != null) monitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("alarm monitor has been stopped");
    }

    @Override
    public final void run() {
        while (running) {
            try {
                List<Alarm> alarms = combineAlarms(batchGetAlarms());
                for (Alarm alarm : alarms) alarmSender.send(alarm);
                logger.info("alarm monitor finish processing alarms, sleep {} ms", config.minAsyncAlarmTimeIntervalMs);
                Thread.sleep(config.minAsyncAlarmTimeIntervalMs);
            } catch (Throwable t) {
                logger.warn(t.getMessage(), t);
            }
        }
    }

    /**
     * 批量获取报警信息
     *
     * @return 报警列表
     */
    private List<Alarm> batchGetAlarms() {
        List<Alarm> alarms = new ArrayList<>();
        while (true) {
            try {
                Alarm alarm = alarmQueue.poll(3, TimeUnit.SECONDS);
                if (alarm == null) break;
                alarms.add(alarm);
                if (alarms.size() >= config.minAsyncAlarmNum) break;
            } catch (InterruptedException e) {
                logger.warn(e.getMessage(), e);
                break;
            }
        }
        return alarms;
    }

    /**
     * 合并相同类型报警
     *
     * @param alarms 报警列表
     * @return 合并后报警列表
     */
    private List<Alarm> combineAlarms(List<Alarm> alarms) {
        Map<AlarmStatus, Set<String>> groupAlarmMap = new HashMap<>();
        for (Alarm alarm : alarms) {
            if (!groupAlarmMap.containsKey(alarm.status)) groupAlarmMap.put(alarm.status, new HashSet<>());
            Set<String> messages = groupAlarmMap.get(alarm.status);
            messages.add(alarm.content);
        }
        List<Alarm> processedAlarms = new ArrayList<>();
        for (Map.Entry<AlarmStatus, Set<String>> entry : groupAlarmMap.entrySet()) {
            AlarmStatus status = entry.getKey();
            Set<String> messages = entry.getValue();
            Alarm combinedAlarm = new Alarm();
            combinedAlarm.status = status;
            combinedAlarm.content = String.join("\n", messages);
            if (messages.size() > 1) {
                combinedAlarm.content = String.format("合并报警数量[%d] %s", messages.size(), combinedAlarm.content);
            }
            processedAlarms.add(combinedAlarm);
        }
        return processedAlarms;
    }
}
