package xin.manong.weapon.base.kafka;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Kafka管理器
 *
 * @author frankcl
 * @date 2026-02-11 11:23:29
 */
public class KafkaAdmin {

    private static final Logger logger = LoggerFactory.getLogger(KafkaAdmin.class);

    private final KafkaAdminConfig config;
    private AdminClient client;

    public KafkaAdmin(KafkaAdminConfig config) {
        this.config = config;
    }

    /**
     * 打开
     *
     * @return 成功返回true，否则返回false
     */
    public boolean open() {
        logger.info("Kafka admin is opening ...");
        if (config == null) {
            logger.error("Kafka admin config is null");
            return false;
        }
        if (!config.check()) return false;
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.servers);
        if (config.authConfig != null) {
            properties.put(KafkaAuthConfig.SECURITY_PROTOCOL, config.authConfig.securityProtocol);
            properties.put(KafkaAuthConfig.SASL_MECHANISM, config.authConfig.saslMechanism);
            properties.put(KafkaAuthConfig.SASL_JAAS_CONFIG, config.authConfig.saslJaasConfig);
        }
        client = AdminClient.create(properties);
        logger.info("Kafka admin opening success");
        return true;
    }

    /**
     * 关闭
     */
    public void close() {
        logger.info("Kafka admin is closing ...");
        if (client != null) {
            client.close();
            client = null;
        }
        logger.info("Kafka admin closed success");
    }

    /**
     * 获取Kafka主题消费堆积数量
     *
     * @param topic 主题
     * @param groupId 分组ID
     * @return 成功返回堆积数量，否则返回-1
     */
    public long getTopicConsumeLagCount(String topic, String groupId) {
        if (StringUtils.isEmpty(topic)) {
            logger.error("Kafka topic is not config");
            return -1L;
        }
        if (StringUtils.isEmpty(groupId)) {
            logger.error("Kafka group id is not config");
            return -1L;
        }
        try {
            ListConsumerGroupOffsetsResult offsetsResult = client.listConsumerGroupOffsets(groupId);
            Map<TopicPartition, OffsetAndMetadata> groupOffsets =
                    offsetsResult.partitionsToOffsetAndMetadata().get();
            Map<TopicPartition, OffsetAndMetadata> topicOffsets = groupOffsets.entrySet().stream()
                    .filter(entry -> entry.getKey().topic().equals(topic))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (topicOffsets.isEmpty()) {
                logger.warn("Topic:{} is not found", topic);
                return -1L;
            }
            Set<TopicPartition> partitions = topicOffsets.keySet();
            ListOffsetsResult latestOffsetsResult = client.listOffsets(
                    partitions.stream().collect(Collectors.toMap(
                            tp -> tp, tp -> OffsetSpec.latest())));
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> endOffsets =
                    latestOffsetsResult.all().get();
            long totalLagCount = 0L;
            for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : topicOffsets.entrySet()) {
                TopicPartition partition = entry.getKey();
                long offset = entry.getValue().offset();
                long endOffset = endOffsets.get(partition).offset();
                totalLagCount += endOffset - offset;
            }
            return totalLagCount;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1L;
        }
    }
}
