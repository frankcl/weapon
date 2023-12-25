package xin.manong.weapon.spring.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import xin.manong.weapon.aliyun.log.LogClientConfig;

import java.util.Map;

/**
 * Log客户端集合自动配置
 *
 * @author frankcl
 * @date 2023-12-25 17:06:36
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.log")
public class LogClientMapConfig {

    public Map<String, LogClientConfig> clientMap;
}
