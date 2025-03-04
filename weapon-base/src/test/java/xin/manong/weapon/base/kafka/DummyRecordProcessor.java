package xin.manong.weapon.base.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author frankcl
 * @date 2025-03-03 21:03:18
 */
public class DummyRecordProcessor implements KafkaRecordProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DummyRecordProcessor.class);

    @Override
    public void process(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        logger.info("consume message, key[{}], value[{}] for topic[{}], offset[{}] and partition[{}]",
                new String(consumerRecord.key(), StandardCharsets.UTF_8),
                new String(consumerRecord.value(), StandardCharsets.UTF_8),
                consumerRecord.topic(), consumerRecord.offset(), consumerRecord.partition());
    }
}
