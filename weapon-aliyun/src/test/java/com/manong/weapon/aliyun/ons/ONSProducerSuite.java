package com.manong.weapon.aliyun.ons;

import com.alibaba.fastjson2.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.manong.weapon.aliyun.secret.KeySecret;
import com.manong.weapon.base.util.FileUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * @author frankcl
 * @date 2022-07-25 17:52:57
 */
public class ONSProducerSuite {

    private String secretFile = this.getClass().getResource("/secret").getPath();
    private ONSProducer producer;

    @Before
    public void setUp() {
        String content = FileUtil.read(secretFile, Charset.forName("UTF-8"));
        KeySecret keySecret = JSON.parseObject(content, KeySecret.class);
        ONSProducerConfig config = new ONSProducerConfig();
        config.keySecret = keySecret;
        config.serverURL = "http://onsaddr.mq-internet-access.mq-internet.aliyuncs.com:80";
        producer = new ONSProducer(config);
        Assert.assertTrue(producer.init());
    }

    @After
    public void tearDown() {
        producer.destroy();
    }

    @Test
    public void testSendMessage() {
        Message message = new Message("TEST_MEDIA_PRODUCE_OUTPUT_DATA", "*",
                "test_key", "test_body".getBytes());
        SendResult sendResult = producer.send(message);
        Assert.assertTrue(sendResult != null && sendResult.getMessageId() != null);
    }

    @Test
    public void testSendMessageToUnknownTopic() {
        Message message = new Message("TEST_UNKNOWN_TOPIC", "*",
                "test_key", "test_body".getBytes());
        SendResult sendResult = producer.send(message);
        Assert.assertTrue(sendResult == null);
    }
}
