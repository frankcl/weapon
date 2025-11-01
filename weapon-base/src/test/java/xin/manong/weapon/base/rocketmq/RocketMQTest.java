package xin.manong.weapon.base.rocketmq;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author frankcl
 * @date 2025-10-31 23:19:59
 */
public class RocketMQTest {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQTest.class);

    private String topic;
    private RocketMQProducer producer;
    private RocketMQConsumer consumer;

    @Before
    public void setUp() throws Exception {
        topic = "PROD_DARWIN_FETCH_URL";

        RocketMQProducerConfig producerConfig = new RocketMQProducerConfig();
        producerConfig.endpoints = "rmq-cn-1wy4i653l01.cn-hangzhou.rmq.aliyuncs.com:8080";
        producerConfig.instanceId = "rmq-cn-1wy4i653l01";
        producerConfig.username = "5KI7G528ctRzk0tO";
        producerConfig.password = "";
        producer = new RocketMQProducer(producerConfig);
        Assert.assertTrue(producer.init());

        RocketMQConsumerConfig consumerConfig = new RocketMQConsumerConfig();
        consumerConfig.endpoints = "rmq-cn-1wy4i653l01.cn-hangzhou.rmq.aliyuncs.com:8080";
        consumerConfig.instanceId = "rmq-cn-1wy4i653l01";
        consumerConfig.username = "5KI7G528ctRzk0tO";
        consumerConfig.password = "";
        consumerConfig.consumeId = "GID-PROD-DARWIN-FETCH-URL";
        consumerConfig.addSubscribe(new RocketMQSubscribe(topic));
        consumerConfig.messageListener = (MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            for (MessageExt messageExt : list) logger.info(new String(messageExt.getBody(), StandardCharsets.UTF_8));
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        };
        consumer = new RocketMQConsumer(consumerConfig);
        Assert.assertTrue(consumer.start());
    }

    @After
    public void tearDown() throws Exception {
        consumer.stop();
        producer.destroy();
    }

    @Test
    public void testSendReceive() throws Exception {
        Message message = new Message(topic, null, "test_key", "测试消息".getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = producer.send(message);
        Assert.assertNotNull(sendResult);
        logger.info("MessageID:{}", sendResult.getMsgId());
        Thread.sleep(10000);
    }
}
