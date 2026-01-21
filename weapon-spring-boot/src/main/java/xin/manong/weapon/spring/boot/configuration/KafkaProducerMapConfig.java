package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.base.kafka.KafkaProduceConfig;

import java.util.Map;

/**
 * kafka producer集合自动配置
 *
 * @author frankcl
 * @date 2025-03-04 17:06:36
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Configuration
@ConfigurationProperties(prefix = "weapon.common.kafka.producer")
public class KafkaProducerMapConfig extends KafkaProduceConfig {

    public Map<String, KafkaProduceConfig> many;
}
