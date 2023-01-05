package xin.manong.weapon.base.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * kafka数据处理接口
 *
 * @author frankcl
 * @date 2023-01-05 17:39:21
 */
public interface KafkaRecordProcessor {

    /**
     * 处理kafka数据，处理失败抛出异常
     *
     * @param consumerRecord 数据
     * @throws Exception
     */
    void process(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception;
}
