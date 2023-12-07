package xin.manong.weapon.alarm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 报警信息合并
 * 负责异步合并和发送报警信息
 *
 * @author frankcl
 * @date 2022-12-09 15:46:21
 */
public class AlarmCombiner implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(AlarmCombiner.class);

    private final static String NAME = "AlarmCombiner";

    private volatile boolean running = false;
    private AlarmConfig config;
    private AlarmProducer producer;
    private Thread thread;
    private BlockingQueue<Alarm> queue;

    public AlarmCombiner(AlarmProducer producer) {
        this.config = producer.config;
        this.producer = producer;
        this.queue = producer.queue;
    }

    /**
     * 启动报警监控
     */
    public final void start() {
        logger.info("alarm combiner is starting ...");
        if (running || (thread != null && thread.isAlive())) {
            logger.warn("alarm combiner has been started");
            return;
        }
        running = true;
        thread = new Thread(this, NAME);
        thread.start();
        logger.info("alarm combiner has been started");
    }

    /**
     * 停止报警监控
     */
    public final void stop() {
        logger.info("alarm combiner is stopping ...");
        if (!running || thread == null || !thread.isAlive()) {
            logger.warn("alarm combiner has been stopped");
            return;
        }
        running = false;
        if (thread != null && thread.isAlive()) thread.interrupt();
        try {
            if (thread != null) thread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("alarm combiner has been stopped");
    }

    @Override
    public final void run() {
        while (running) {
            try {
                List<Alarm> alarms = batchGet();
                if (alarms.isEmpty()) {
                    logger.info("no alarm, sleep {} ms", config.asyncAlarmIntervalMs);
                    Thread.sleep(config.asyncAlarmIntervalMs);
                    continue;
                }
                List<Alarm> combinedAlarms = combine(alarms);
                for (Alarm alarm : combinedAlarms) producer.send(alarm);
                logger.info("process alarm num[{}], sleep {} ms", alarms.size(), config.asyncAlarmIntervalMs);
                Thread.sleep(config.asyncAlarmIntervalMs);
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
    private List<Alarm> batchGet() throws InterruptedException {
        List<Alarm> alarms = new ArrayList<>();
        while (true) {
            Alarm alarm = queue.poll(3, TimeUnit.SECONDS);
            if (alarm == null) break;
            alarms.add(alarm);
            if (alarms.size() >= config.batchAlarmNum) break;
        }
        return alarms;
    }

    /**
     * 合并相同类型报警
     *
     * @param alarms 报警列表
     * @return 合并后报警列表
     */
    private List<Alarm> combine(List<Alarm> alarms) {
        String appName = "", title = "";
        Map<AlarmLevel, Map<String, Integer>> levelMap = new HashMap<>();
        for (Alarm alarm : alarms) {
            if (!levelMap.containsKey(alarm.level)) levelMap.put(alarm.level, new HashMap<>());
            Map<String, Integer> countMap = levelMap.get(alarm.level);
            if (!countMap.containsKey(alarm.content)) countMap.put(alarm.content, 0);
            countMap.put(alarm.content, countMap.get(alarm.content) + 1);
            if (StringUtils.isEmpty(appName)) appName = alarm.appName;
            if (StringUtils.isEmpty(title)) title = alarm.title;
        }
        List<Alarm> combinedAlarms = new ArrayList<>();
        for (Map.Entry<AlarmLevel, Map<String, Integer>> entry : levelMap.entrySet()) {
            AlarmLevel level = entry.getKey();
            Map<String, Integer> countMap = entry.getValue();
            int totalCount = 0;
            for (Integer count : countMap.values()) totalCount += count;
            String message = String.join("\n", countMap.keySet());
            Alarm combinedAlarm = new Alarm(title, totalCount > 1 ?
                    String.format("合并报警数量[%d] %s", totalCount, message) : message, level);
            combinedAlarm.setAppName(appName);
            combinedAlarms.add(combinedAlarm);
        }
        return combinedAlarms;
    }
}
