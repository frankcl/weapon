package xin.manong.weapon.base.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.weapon.base.util.RandomID;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author frankcl
 * @date 2025-03-03 20:35:23
 */
public class KafkaTest {

    private final String topic = "test_topic";
    private KafkaProducer producer;
    private KafkaConsumeGroup consumeGroup;

    @Before
    public void setUp() throws Exception {
        String username = "admin";
        String password = "admin-123456";
        String servers = "localhost:9092";

        KafkaAuthConfig authConfig = new KafkaAuthConfig();
        authConfig.securityProtocol = KafkaAuthConfig.PROTOCOL_SASL_PLAINTEXT;
        authConfig.saslMechanism = "PLAIN";
        authConfig.saslJaasConfig = String.format(
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                username, password);
        KafkaProduceConfig produceConfig = new KafkaProduceConfig();
        produceConfig.authConfig = authConfig;
        produceConfig.servers = servers;
        producer = new KafkaProducer(produceConfig);
        Assert.assertTrue(producer.init());

        KafkaConsumeConfig consumeConfig = new KafkaConsumeConfig();
        consumeConfig.servers = servers;
        consumeConfig.authConfig = authConfig;
        consumeConfig.consumeThreadNum = 2;
        consumeConfig.maxFetchWaitTimeMs = 1000L;
        consumeConfig.name = "test_consumer";
        consumeConfig.groupId = "test_group";
        consumeConfig.topics = new ArrayList<>() {{ add(topic); }};
        consumeGroup = new KafkaConsumeGroup(consumeConfig);
        consumeGroup.setProcessor(new DummyRecordProcessor());
        Assert.assertTrue(consumeGroup.start());
    }

    @After
    public void tearDown() throws Exception {
        if (consumeGroup != null) consumeGroup.stop();
        if (producer != null) producer.destroy();
    }

    @Test
    public void testProduce() throws Exception {
        for (int i = 0; i < 10; i++) {
            String key = String.format("key-%s", RandomID.build());
            String message = String.format("message-%s", RandomID.build());
            RecordMetadata recordMetadata = producer.send(key,
                    message.getBytes(StandardCharsets.UTF_8), topic);
            Assert.assertNotNull(recordMetadata);
            Thread.sleep(1000);
        }
        Thread.sleep(5000);
    }
}
