package xin.manong.weapon.base.kafka;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka消费组
 *
 * @author frankcl
 * @date 2023-01-05 16:16:24
 */
public class KafkaConsumeGroup {

    private final static Logger logger = LoggerFactory.getLogger(KafkaConsumeGroup.class);

    private final KafkaConsumeConfig config;
    private KafkaConsumer[] consumers;
    @Setter
    private KafkaRecordProcessor processor;

    public KafkaConsumeGroup(KafkaConsumeConfig config) {
        this.config = config;
    }

    public KafkaConsumeGroup(KafkaConsumeConfig config, KafkaRecordProcessor processor) {
        this(config);
        this.processor = processor;
    }

    /**
     * 启动kafka消费组
     *
     * @return 启动成功返回true，否则返回false
     */
    public boolean start() {
        logger.info("kafka consume group[{}] is starting ...", config == null ? null : config.name);
        if (config == null || !config.check()) {
            logger.error("kafka consume config is invalid");
            return false;
        }
        if (processor == null) {
            logger.error("kafka record processor is null");
            return false;
        }
        consumers = new KafkaConsumer[config.consumeThreadNum];
        for (int i = 0; i < config.consumeThreadNum; i++) {
            String name = String.format("%s_%d", config.name, i);
            consumers[i] = new KafkaConsumer(name, config, processor);
            if (!consumers[i].start()) {
                logger.error("start kafka consumer[{}] failed", name);
                return false;
            }
        }
        logger.info("kafka consume group[{}] has been started", config.name);
        return true;
    }

    /**
     * 停止kafka消费组
     */
    public void stop() {
        logger.info("kafka consume group[{}] is stopping ...", config.name);
        for (int i = 0; consumers != null && i < consumers.length; i++) {
            consumers[i].stop();
        }
        logger.info("kafka consume group[{}] has been stopped", config.name);
    }
}
