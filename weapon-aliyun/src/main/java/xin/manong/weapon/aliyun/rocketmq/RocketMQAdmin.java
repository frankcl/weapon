package xin.manong.weapon.aliyun.rocketmq;

import com.aliyun.rocketmq20220801.Client;
import com.aliyun.rocketmq20220801.models.GetConsumerGroupLagRequest;
import com.aliyun.rocketmq20220801.models.GetConsumerGroupLagResponse;
import com.aliyun.rocketmq20220801.models.GetConsumerGroupLagResponseBody;
import com.aliyun.teaopenapi.models.Config;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.secret.DynamicSecret;
import xin.manong.weapon.base.rebuild.Rebuildable;

/**
 * 消息队列管理
 *
 * @author frankcl
 * @date 2026-02-09 17:23:13
 */
public class RocketMQAdmin implements Rebuildable {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQAdmin.class);

    private final RocketMQAdminConfig config;
    private Client client;

    public RocketMQAdmin(RocketMQAdminConfig config) {
        this.config = config;
    }

    /**
     * 构建消息管理实例
     *
     * @return 构建成功返回true，否则返回false
     */
    private boolean build() {
        com.aliyun.credentials.models.Config credentialConfig = new com.aliyun.credentials.models.Config();
        credentialConfig.setType("access_key");
        credentialConfig.setAccessKeyId(config.aliyunSecret.accessKey);
        credentialConfig.setAccessKeySecret(config.aliyunSecret.secretKey);
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client(credentialConfig);
        Config modelConfig = new Config().setCredential(credential);
        modelConfig.endpoint = config.endpoint;
        try {
            client = new Client(modelConfig);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void rebuild() {
        logger.info("RocketMQ admin is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("Secret is not changed, ignore RocketMQAdmin rebuilding");
            return;
        }
        config.aliyunSecret.accessKey = DynamicSecret.accessKey;
        config.aliyunSecret.secretKey = DynamicSecret.secretKey;
        if (!build()) throw new RuntimeException("Rebuild RocketMQ admin failed");
        logger.info("RocketMQ admin rebuild success");
    }

    /**
     * 初始化
     *
     * @return 成功返回true，否则返回false
     */
    public boolean init() {
        logger.info("RocketMQAdmin init start ...");
        if (config == null || !config.check()) {
            logger.error("RocketMQAdmin config is invalid");
            return false;
        }
        if (!build()) return false;
        logger.info("RocketMQAdmin init success");
        return true;
    }

    /**
     * 销毁
     */
    public void destroy() {
        logger.info("RocketMQAdmin destroy start ...");
        client = null;
        logger.info("RocketMQAdmin destroy success");
    }

    /**
     * 获取消息堆积数量
     *
     * @param instanceId 实例ID
     * @param topic 主题
     * @param consumerGroup 消费组ID
     * @return 堆积数量，失败返回-1
     */
    public long getTopicConsumeLagCount(String instanceId, String topic, String consumerGroup) {
        if (StringUtils.isEmpty(instanceId)) {
            logger.error("Instance id is empty");
            return -1L;
        }
        if (StringUtils.isEmpty(consumerGroup)) {
            logger.error("Consume group is empty");
            return -1L;
        }
        if (StringUtils.isEmpty(topic)) {
            logger.error("Topic is empty");
            return -1L;
        }
        GetConsumerGroupLagRequest request = new GetConsumerGroupLagRequest().setTopicName(topic);
        try {
            GetConsumerGroupLagResponse response = client.getConsumerGroupLag(instanceId, consumerGroup, request);
            if (response.statusCode != 200 || !response.body.success) {
                logger.error("Get consumer group lag failed for code:{}, message:{}",
                        response.statusCode, response.body == null ? null : response.body.message);
                return -1L;
            }
            GetConsumerGroupLagResponseBody.GetConsumerGroupLagResponseBodyDataTotalLag total =
                    response.body.data.totalLag;
            return total.readyCount + total.inflightCount;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1L;
        }
    }
}
