package xin.manong.weapon.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Kafka认证配置
 *
 * @author frankcl
 * @date 2025-03-13 17:06:36
 */
@Configuration
@ConfigurationProperties(prefix = "weapon.common.kafka.auth")
public class KafkaAuthConfig extends xin.manong.weapon.base.kafka.KafkaAuthConfig {
}
