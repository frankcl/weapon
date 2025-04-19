package xin.manong.weapon.aliyun.mns;

import com.aliyun.mns.client.CloudQueue;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.event.EventListener;
import xin.manong.weapon.base.event.RebuildEvent;
import xin.manong.weapon.base.rebuild.Rebuildable;

import java.util.ArrayList;
import java.util.List;

/**
 * MNS队列消费
 *
 * @author frankcl
 * @date 2024-01-12 11:10:15
 */
public class MNSQueueConsumer implements Rebuildable, EventListener {

    private static final Logger logger = LoggerFactory.getLogger(MNSQueueConsumer.class);

    protected MNSQueueConsumerConfig config;
    protected MNSClient mnsClient;
    @Setter
    protected MessageProcessor processor;
    private List<MNSQueueHandler> handlers;
    private final List<EventListener> eventListeners;

    public MNSQueueConsumer(MNSQueueConsumerConfig config) {
        this.config = config;
        this.eventListeners = new ArrayList<>();
    }

    /**
     * 启动消息消费器
     *
     * @return 启动成功返回true，否则返回false
     */
    public boolean start() {
        logger.info("MNS queue consumer is starting ...");
        if (config == null) {
            logger.error("MNS queue consumer config is null");
            return false;
        }
        if (!config.check()) return false;
        if (!build()) return false;
        logger.info("MNS queue consumer has been started");
        return true;
    }

    /**
     * 停止消息消费器
     */
    public void stop() {
        logger.info("MNS queue consumer is stopping ...");
        if (handlers != null) {
            for (MNSQueueHandler handler : handlers) handler.stop();
            handlers.clear();
        }
        logger.info("MNS queue consumer has been stopped");
    }

    @Override
    public void onRebuild(@NotNull RebuildEvent<?> rebuildEvent) {
        if (rebuildEvent.getBuildTarget() == null || rebuildEvent.getBuildTarget() != mnsClient) return;
        for (EventListener eventListener : eventListeners) {
            eventListener.onRebuild(new RebuildEvent<>(this));
        }
        if (handlers != null) {
            for (MNSQueueHandler handler : handlers) handler.stop();
            handlers.clear();
        }
        build();
    }

    /**
     * 添加重建监听器
     *
     * @param eventListener 重建监听器
     */
    public void addRebuildListener(EventListener eventListener) {
        if (eventListener == null) return;
        eventListeners.add(eventListener);
    }

    /**
     * 设置MNS客户端
     *
     * @param mnsClient MNS客户端
     */
    public void setMnsClient(MNSClient mnsClient) {
        this.mnsClient = mnsClient;
        this.mnsClient.addRebuildListener(this);
    }

    /**
     * 构建消息处理器
     *
     * @return 构建成功返回true，否则返回false
     */
    private boolean build() {
        if (mnsClient == null) {
            logger.error("MNS client is not set");
            return false;
        }
        if (processor == null) {
            logger.error("message processor is not set");
            return false;
        }
        CloudQueue queue = mnsClient.getQueue(config.getQueueName());
        handlers = new ArrayList<>();
        for (int i = 0; i < config.threadNum; i++) {
            String name = String.format("MNSQueueHandler-%d", i);
            MNSQueueHandler handler = new MNSQueueHandler(name, queue, processor);
            handler.start();
            handlers.add(handler);
        }
        return true;
    }
}
