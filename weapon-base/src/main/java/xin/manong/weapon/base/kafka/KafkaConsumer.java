package xin.manong.weapon.base.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * kafka消费线程
 *
 * @author frankcl
 * @date 2023-01-05 16:16:07
 */
public class KafkaConsumer implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private volatile boolean running;
    private final String name;
    private org.apache.kafka.clients.consumer.KafkaConsumer<byte[], byte[]> consumer;
    private final KafkaRecordProcessor processor;
    private final KafkaConsumeConfig config;
    private Thread consumeThread;

    public KafkaConsumer(String name, KafkaConsumeConfig config,
                         KafkaRecordProcessor processor) {
        this.name = name;
        this.config = config;
        this.processor = processor;
        this.running = false;
    }

    /**
     * 启动kafka消费线程
     *
     * @return 启动成功返回true，否则返回false
     */
    public boolean start() {
        logger.info("Kafka consumer:{} is starting ...", name);
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.servers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, config.groupId);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        if (config.maxFetchWaitTimeMs != null && config.maxFetchWaitTimeMs > 0) {
            properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, String.valueOf(config.maxFetchWaitTimeMs));
        }
        if (config.authConfig != null) {
            properties.put(KafkaAuthConfig.SECURITY_PROTOCOL, config.authConfig.securityProtocol);
            properties.put(KafkaAuthConfig.SASL_MECHANISM, config.authConfig.saslMechanism);
            properties.put(KafkaAuthConfig.SASL_JAAS_CONFIG, config.authConfig.saslJaasConfig);
        }
        consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(properties);
        running = true;
        consumeThread = new Thread(this, name);
        consumeThread.start();
        logger.info("Kafka consumer:{} has been started", name);
        return true;
    }

    /**
     * 停止kafka消费线程
     */
    public void stop() {
        logger.info("Kafka consumer:{} is stopping ...", name);
        running = false;
        if (consumeThread != null && consumeThread.isAlive()) consumeThread.interrupt();
        try {
            if (consumeThread != null) consumeThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        if (consumer != null) consumer.close();
        logger.info("Kafka consumer:{} has been stopped", name);
    }

    @Override
    public void run() {
        consumer.subscribe(config.topics);
        while (running) {
            try {
                ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofSeconds(3));
                if (records == null || records.isEmpty()) continue;
                for (TopicPartition partition : records.partitions()) {
                    List<ConsumerRecord<byte[], byte[]>> partitionRecords = records.records(partition);
                    for (ConsumerRecord<byte[], byte[]> partitionRecord : partitionRecords) {
                        processor.process(partitionRecord);
                        Map<TopicPartition, OffsetAndMetadata> offsets = Collections.singletonMap(
                                partition, new OffsetAndMetadata(partitionRecord.offset() + 1));
                        consumer.commitAsync(offsets, (partitionOffsetMap, exception) -> {
                            if (exception == null) return;
                            partitionOffsetMap.forEach((p, offsetMeta) -> logger.warn(
                                    "Commit failed for topic:{}, partition:{} and offset:{}",
                                    p.topic(), p.partition(), offsetMeta.offset()));
                        });
                    }
                }
            } catch (Throwable e) {
                logger.error("Process kafka message failed");
                logger.error(e.getMessage(), e);
            }
        }
    }
}
