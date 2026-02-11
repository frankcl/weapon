package xin.manong.weapon.base.kafka;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2026-02-11 12:01:03
 */
public class KafkaAdminTest {

    private KafkaAdmin admin;

    @Before
    public void setUp() throws Exception {
        String username = "client";
        String password = "";
        String servers = "localhost:9092";

        KafkaAuthConfig authConfig = new KafkaAuthConfig();
        authConfig.securityProtocol = KafkaAuthConfig.PROTOCOL_SASL_PLAINTEXT;
        authConfig.saslMechanism = "SCRAM-SHA-256";
        authConfig.saslJaasConfig = String.format(
                "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";",
                username, password);
        KafkaAdminConfig adminConfig = new KafkaAdminConfig();
        adminConfig.authConfig = authConfig;
        adminConfig.servers = servers;
        admin = new KafkaAdmin(adminConfig);
        Assert.assertTrue(admin.open());
    }

    @After
    public void tearDown() throws Exception {
        admin.close();
    }

    @Test
    public void testGetTopicConsumeLagCount() {
        long count = admin.getTopicConsumeLagCount("test_topic", "test_group");
        Assert.assertEquals(count, 0);
    }
}
