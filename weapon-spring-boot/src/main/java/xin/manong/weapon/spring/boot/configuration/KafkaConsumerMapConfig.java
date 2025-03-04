package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.base.kafka.KafkaConsumeConfig;

import java.util.Map;

/**
 * kafka consumer集合自动配置
 *
 * @author frankcl
 * @date 2025-03-04 17:06:36
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "weapon.common.kafka.consumer")
public class KafkaConsumerMapConfig extends KafkaConsumeConfig {

    public Map<String, KafkaConsumeConfig> many;
}
