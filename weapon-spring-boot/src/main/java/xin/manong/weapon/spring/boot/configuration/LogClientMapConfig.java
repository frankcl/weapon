package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.aliyun.log.LogClientConfig;

import java.util.Map;

/**
 * Log客户端集合自动配置
 *
 * @author frankcl
 * @date 2023-12-25 17:06:36
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "weapon.aliyun.log.client")
public class LogClientMapConfig extends LogClientConfig {

    public Map<String, LogClientConfig> many;
}
