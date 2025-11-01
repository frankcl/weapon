package xin.manong.weapon.base.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RocketMQ消息生产者
 *
 * @author frankcl
 * @date 2025-10-31 21:26:36
 */
public class RocketMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQProducer.class);

    private final RocketMQProducerConfig config;
    private DefaultMQProducer producer;

    public RocketMQProducer(RocketMQProducerConfig config) {
        this.config = config;
    }

    /**
     * 初始化
     *
     * @return 成功返回true，否则返回false
     */
    public boolean init() {
        logger.info("RocketMQ producer start init ...");
        if (config == null || !config.check()) {
            logger.error("RocketMQ producer config is invalid");
            return false;
        }
        try {
            producer = new DefaultMQProducer(config.producerGroup, config.buildRPCHook());
            producer.setNamesrvAddr(config.endpoints);
            producer.setNamespaceV2(config.instanceId);
            producer.setRetryTimesWhenSendFailed(config.retryCnt);
            producer.setSendMsgTimeout(config.sendMsgTimeout);
            producer.start();
            logger.info("RocketMQ producer init success");
            return true;
        } catch (Exception e) {
            logger.error("RocketMQ producer init failed", e);
            return false;
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        logger.info("RocketMQ producer start destroy ...");
        if (producer != null) producer.shutdown();
        logger.info("RocketMQ producer destroy success");
    }

    /**
     * 发送消息
     *
     * @param message 消息
     * @return 发送结果
     */
    public SendResult send(Message message) {
        try {
            return producer.send(message);
        } catch (Exception e) {
            logger.error("Send message failed", e);
            return null;
        }
    }
}
