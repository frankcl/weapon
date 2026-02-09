package xin.manong.weapon.aliyun.rocketmq;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2026-02-09 18:32:19
 */
public class RocketMQAdminTest {

    private RocketMQAdmin rocketMQAdmin;

    @Before
    public void setUp() throws Exception {
        RocketMQAdminConfig config = new RocketMQAdminConfig();
        config.endpoint = "rocketmq.cn-hangzhou.aliyuncs.com";
        config.dynamic = false;
        config.aliyunSecret.accessKey = "";
        config.aliyunSecret.secretKey = "";
        rocketMQAdmin = new RocketMQAdmin(config);
        Assert.assertTrue(rocketMQAdmin.init());
    }

    @After
    public void tearDown() throws Exception {
        rocketMQAdmin.destroy();
    }

    @Test
    public void testGetConsumerGroupLag() {
        long count = rocketMQAdmin.getTopicConsumeLagCount("rmq-cn-1wy4i653l01",
                "PROD-DARWIN-DISPATCH-RECORD", "GID-PROD-PPN-STREAM");
        Assert.assertTrue(count > 0);
    }
}
