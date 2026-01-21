package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.aliyun.oss.OSSClientConfig;

import java.util.Map;

/**
 * OSS客户端集合自动配置
 *
 * @author frankcl
 * @date 2023-12-25 17:06:36
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Configuration
@ConfigurationProperties(prefix = "weapon.aliyun.oss.client")
public class OSSClientMapConfig extends OSSClientConfig {

    public Map<String, OSSClientConfig> many;
}
