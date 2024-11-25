package xin.manong.weapon.base.executor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程执行调度
 * 定期执行自定义逻辑
 *
 * @author frankcl
 * @date 2024-11-13 11:52:28
 */
public abstract class ExecuteRunner implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteRunner.class);
    private static final String DEFAULT_NAME = "ExecuteRunner";

    protected volatile boolean running;
    private final long executeTimeIntervalMs;
    private final String name;
    private Thread thread;

    public ExecuteRunner(long executeTimeIntervalMs) {
        this(DEFAULT_NAME, executeTimeIntervalMs);
    }

    public ExecuteRunner(String name, long executeTimeIntervalMs) {
        this.name = StringUtils.isEmpty(name) ? DEFAULT_NAME : name;
        this.running = false;
        this.executeTimeIntervalMs = Math.max(1000L, executeTimeIntervalMs);
    }

    /**
     * 启动执行
     *
     * @return 成功返回true，否则返回false
     */
    public boolean start() {
        logger.info("{} is starting ...", name);
        running = true;
        thread = new Thread(this, name);
        thread.start();
        logger.info("{} has been started", name);
        return true;
    }

    /**
     * 停止执行
     */
    public void stop() {
        logger.info("{} is stopping", name);
        running = false;
        if (thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info("{} has been stopped", name);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while (running) {
            try {
                execute();
                logger.info("finish one loop processing, sleep {} seconds", executeTimeIntervalMs / 1000);
                Thread.sleep(executeTimeIntervalMs);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 执行逻辑
     *
     * @throws Exception 异常
     */
    public abstract void execute() throws Exception;
}
