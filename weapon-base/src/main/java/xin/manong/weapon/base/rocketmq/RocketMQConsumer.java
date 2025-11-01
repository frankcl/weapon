package xin.manong.weapon.base.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RocketMQ消息消费者
 *
 * @author frankcl
 * @date 2025-10-31 22:05:31
 */
public class RocketMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQConsumer.class);

    protected final RocketMQConsumerConfig config;
    private DefaultMQPushConsumer consumer;

    public RocketMQConsumer(RocketMQConsumerConfig config) {
        this.config = config;
    }

    public boolean start() {
        logger.info("RocketMQ consumer is starting ...");
        if (config == null || !config.check()) {
            logger.error("RocketMQ consumer config is invalid");
            return false;
        }
        try {
            consumer = new DefaultMQPushConsumer(config.buildRPCHook());
            consumer.setNamesrvAddr(config.endpoints);
            consumer.setNamespaceV2(config.instanceId);
            consumer.setConsumerGroup(config.consumeId);
            consumer.setConsumeThreadMin(config.consumeThreadNum);
            consumer.setConsumeThreadMax(config.consumeThreadNum);
            consumer.setConsumeMessageBatchMaxSize(config.consumeMessageBatchMaxSize);
            consumer.setMessageModel(MessageModel.CLUSTERING);
            for (RocketMQSubscribe subscribe : config.subscribes) consumer.subscribe(subscribe.topic, subscribe.tags);
            if (config.messageListener instanceof MessageListenerConcurrently) {
                consumer.registerMessageListener((MessageListenerConcurrently) config.messageListener);
            } else if (config.messageListener instanceof MessageListenerOrderly) {
                consumer.registerMessageListener((MessageListenerOrderly) config.messageListener);
            } else {
                logger.error("RocketMQ consumer message listener is not supported");
                return false;
            }
            consumer.start();
            logger.info("RocketMQ consumer start success");
            return true;
        } catch (Exception e) {
            logger.error("RocketMQ consumer start fail", e);
            return false;
        }
    }

    public void stop() {
        logger.info("RocketMQ consumer is stopping ...");
        if (consumer != null) consumer.shutdown();
        logger.info("RocketMQ consumer stop success");
    }
}
