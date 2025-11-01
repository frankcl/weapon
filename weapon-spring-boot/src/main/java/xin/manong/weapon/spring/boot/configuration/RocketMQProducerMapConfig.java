package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.base.rocketmq.RocketMQProducerConfig;

import java.util.Map;

/**
 * RocketMQ producer集合自动配置
 *
 * @author frankcl
 * @date 2025-10-31 17:06:36
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "weapon.common.rocketmq.producer")
public class RocketMQProducerMapConfig extends RocketMQProducerConfig {

    public Map<String, RocketMQProducerConfig> many;
}
