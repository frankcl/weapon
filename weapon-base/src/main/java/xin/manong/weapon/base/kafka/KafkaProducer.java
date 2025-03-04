package xin.manong.weapon.base.kafka;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * kafka消息生产
 *
 * @author frankcl
 * @date 2023-01-05 18:05:17
 */
public class KafkaProducer {

    private final static Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaProduceConfig config;
    private org.apache.kafka.clients.producer.KafkaProducer<byte[], byte[]> producer;

    public KafkaProducer(KafkaProduceConfig config) {
        this.config = config;
    }

    /**
     * 初始化kafka消息生产
     *
     * @return 成功返回true，否则返回false
     */
    public boolean init() {
        logger.info("kafka producer is init ...");
        if (config == null || !config.check()) {
            logger.error("kafka producer config is invalid");
            return false;
        }
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.servers);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, String.valueOf(config.requestTimeoutMs));
        properties.put(ProducerConfig.RETRIES_CONFIG, String.valueOf(config.retryCnt));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.ByteArraySerializer");
        if (config.authConfig != null) {
            properties.put(KafkaAuthConfig.SECURITY_PROTOCOL, config.authConfig.securityProtocol);
            properties.put(KafkaAuthConfig.SASL_MECHANISM, config.authConfig.saslMechanism);
            properties.put(KafkaAuthConfig.SASL_JAAS_CONFIG, config.authConfig.saslJaasConfig);
        }
        producer = new org.apache.kafka.clients.producer.KafkaProducer<>(properties);
        logger.info("kafka producer init success");
        return true;
    }

    /**
     * 销毁kafka消息生产
     */
    public void destroy() {
        logger.info("kafka producer is destroying ...");
        if (producer != null) producer.close();
        logger.info("kafka producer has been destroyed");
    }

    /**
     * 发送消息
     *
     * @param key 消息key
     * @param message 消息内容
     * @param topic kafka topic
     * @return 发送成功返回kafka元信息，否则返回null
     */
    public RecordMetadata send(String key, byte[] message, String topic) {
        if (StringUtils.isEmpty(topic)) {
            logger.error("send kafka topic is empty");
            return null;
        }
        if (message == null || message.length == 0) {
            logger.error("send message is empty, ignore it");
            return null;
        }
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<>(topic,
                key.getBytes(StandardCharsets.UTF_8), message);
        Future<RecordMetadata> future = producer.send(record);
        try {
            return future.get();
        } catch (Exception e) {
            logger.error("get response failed for sending message");
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 发送消息
     *
     * @param message 消息内容
     * @param topic kafka topic
     * @return 发送成功返回kafka元信息，否则返回null
     */
    public RecordMetadata send(byte[] message, String topic) {
        return send(null, message, topic);
    }
}
