package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.aliyun.rocketmq.RocketMQAdminConfig;

/**
 * RocketMQ 管理自动配置
 *
 * @author frankcl
 * @date 2025-10-31 17:06:36
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Configuration
@ConfigurationProperties(prefix = "weapon.common.rocketmq.admin")
public class RocketMQAdminMapConfig extends RocketMQAdminConfig {
}
