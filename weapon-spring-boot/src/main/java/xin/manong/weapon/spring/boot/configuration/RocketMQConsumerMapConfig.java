package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.base.rocketmq.RocketMQConsumerConfig;

import java.util.Map;

/**
 * RocketMQ consumer集合自动配置
 *
 * @author frankcl
 * @date 2025-10-31 17:06:36
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "weapon.common.rocketmq.consumer")
public class RocketMQConsumerMapConfig extends RocketMQConsumerConfig {

    public Map<String, RocketMQConsumerConfig> many;
}
