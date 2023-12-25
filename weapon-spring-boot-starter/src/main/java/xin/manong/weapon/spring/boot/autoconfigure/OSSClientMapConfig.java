package xin.manong.weapon.spring.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import xin.manong.weapon.aliyun.oss.OSSClientConfig;

import java.util.Map;

/**
 * OSS客户端集合自动配置
 *
 * @author frankcl
 * @date 2023-12-25 17:06:36
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.oss")
public class OSSClientMapConfig {

    public Map<String, OSSClientConfig> clientMap;
}
