package xin.manong.weapon.spring.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import xin.manong.weapon.aliyun.datahub.DataHubClientConfig;

import java.util.Map;

/**
 * dataHub客户端集合自动配置
 *
 * @author frankcl
 * @date 2023-12-25 17:06:36
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.datahub")
public class DataHubClientMapConfig {

    public Map<String, DataHubClientConfig> clientMap;
}
