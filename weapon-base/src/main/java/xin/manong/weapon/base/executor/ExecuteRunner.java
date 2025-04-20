package xin.manong.weapon.base.executor;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.event.ErrorEvent;
import xin.manong.weapon.base.event.EventListener;
import xin.manong.weapon.base.util.RandomID;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程执行调度
 * 定期执行自定义逻辑
 *
 * @author frankcl
 * @date 2024-11-13 11:52:28
 */
public abstract class ExecuteRunner implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteRunner.class);
    private static final String KEY_PREFIX = "ExecuteRunner_";

    @Getter
    protected volatile boolean running;
    private final long executeTimeIntervalMs;
    @Getter
    private final String id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
    protected final List<EventListener> eventListeners;
    private Thread thread;

    public ExecuteRunner(long executeTimeIntervalMs) {
        this(buildRandomId(), executeTimeIntervalMs);
    }

    public ExecuteRunner(String id, long executeTimeIntervalMs) {
        this.id = StringUtils.isEmpty(id) ? buildRandomId() : id;
        this.running = false;
        this.executeTimeIntervalMs = Math.max(1000L, executeTimeIntervalMs);
        this.eventListeners = new ArrayList<>();
    }

    /**
     * 构建随机key
     *
     * @return 随机key
     */
    private static String buildRandomId() {
        return String.format("%s%s", KEY_PREFIX, RandomID.build());
    }

    /**
     * 启动执行
     *
     * @return 成功返回true，否则返回false
     */
    public boolean start() {
        if (thread != null && thread.isAlive()) stop();
        logger.info("{} is starting ...", id);
        running = true;
        thread = new Thread(this, id);
        thread.start();
        logger.info("{} has been started", id);
        return true;
    }

    /**
     * 停止执行
     */
    public void stop() {
        logger.info("{} is stopping", id);
        running = false;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info("{} has been stopped", id);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(executeTimeIntervalMs);
                long startTime = System.currentTimeMillis();
                execute();
                long processTime = System.currentTimeMillis() - startTime;
                if (processTime >= executeTimeIntervalMs) continue;
                logger.info("finish one round processing, sleep {} seconds", executeTimeIntervalMs / 1000);
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                notifyErrorEvent(new ErrorEvent(e.getMessage(), e));
            }
        }
    }

    /**
     * 通知错误事件
     *
     * @param errorEvent 错误事件
     */
    protected void notifyErrorEvent(ErrorEvent errorEvent) {
        for (EventListener eventListener : eventListeners) eventListener.onError(errorEvent);
    }

    /**
     * 添加事件监听器
     *
     * @param eventListener 事件监听器
     */
    public void addEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }

    /**
     * 移除事件监听器
     *
     * @param eventListener 事件监听器
     */
    public void removeEventListener(EventListener eventListener) {
        eventListeners.remove(eventListener);
    }

    /**
     * 执行逻辑
     *
     * @throws Exception 异常
     */
    public abstract void execute() throws Exception;
}
