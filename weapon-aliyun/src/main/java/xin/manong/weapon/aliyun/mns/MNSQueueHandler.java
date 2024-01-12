package xin.manong.weapon.aliyun.mns;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MNS队列消费处理
 *
 * @author frankcl
 * @date 2024-01-12 11:50:38
 */
public class MNSQueueHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MNSQueueHandler.class);

    private static final int WAIT_SECONDS = 3;

    private volatile boolean running;
    private String name;
    private Thread workThread;
    private CloudQueue queue;
    private MessageProcessor processor;

    public MNSQueueHandler(String name, CloudQueue queue, MessageProcessor processor) {
        this.running = false;
        this.name = name;
        this.queue = queue;
        this.processor = processor;
    }

    /**
     * 启动
     */
    public void start() {
        logger.info("MNS queue handler[{}] is starting ...", name);
        running = true;
        workThread = new Thread(this, name);
        workThread.start();
        logger.info("MNS queue handler[{}] has been started", name);
    }

    /**
     * 停止
     */
    public void stop() {
        logger.info("MNS queue handler[{}] is stopping ...", name);
        running = false;
        if (workThread.isAlive()) workThread.interrupt();
        try {
            workThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("MNS queue handler[{}] has been stopped", name);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Message message = queue.popMessage(WAIT_SECONDS);
                if (message == null) continue;
                if (processor.process(message)) {
                    queue.deleteMessage(message.getReceiptHandle());
                    continue;
                }
                logger.warn("process message failed for id[{}]", message.getMessageId());
            } catch (Throwable e) {
                logger.error("process message failed");
                logger.error(e.getMessage(), e);
            }
        }
    }
}
