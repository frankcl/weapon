package xin.manong.weapon.alarm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 报警信息生产发送
 * 实际报警系统继承扩展此类，实现报警发送接口
 *
 * @author frankcl
 * @date 2022-12-09 15:46:48
 */
public abstract class AlarmProducer {

    private final static Logger logger = LoggerFactory.getLogger(AlarmProducer.class);

    private final static int ALARM_QUEUE_SIZE = 200;

    BlockingQueue<Alarm> queue;
    protected AlarmConfig config;
    private final AlarmCombiner combiner;

    public AlarmProducer(AlarmConfig config) {
        this.config = config;
        this.queue = new ArrayBlockingQueue<>(ALARM_QUEUE_SIZE);
        this.combiner = new AlarmCombiner(this);
    }

    /**
     * 启动报警生产发送服务
     *
     * @return 启动成功返回true，否则返回false
     */
    public final boolean start() {
        logger.info("alarm producer is starting ...");
        if (config == null || !config.check()) {
            logger.error("config is invalid");
            return false;
        }
        if (!init()) {
            logger.error("init alarm producer failed");
            return false;
        }
        combiner.start();
        logger.info("alarm producer has been started");
        return true;
    }

    /**
     * 停止报警生产发送服务
     */
    public final void stop() {
        logger.info("alarm producer is stopping ...");
        combiner.stop();
        destroy();
        logger.info("alarm producer has been stopped");
    }

    /**
     * 异步投递报警
     *
     * @param alarm 报警信息
     * @return 投递成功返回true，否则返回false
     */
    public final boolean submit(Alarm alarm) {
        if (alarm == null || StringUtils.isEmpty(alarm.content)) {
            logger.warn("alarm is invalid, ignore it");
            return false;
        }
        if (!queue.offer(alarm)) {
            logger.warn("alarm queue is full, ignore it");
            return false;
        }
        return true;
    }

    /**
     * 初始化
     * 子类实现覆盖此方法实现初始化操作
     *
     * @return 成功返回true，否则返回false
     */
    protected boolean init() {
        return true;
    };

    /**
     * 销毁
     * 子类实现覆盖此方法实现销毁操作
     */
    protected void destroy() {
    }

    /**
     * 同步发送报警
     *
     * @param alarm 报警信息
     * @return 发送成功返回true，否则返回false
     */
    public abstract boolean send(Alarm alarm);
}
