package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.aliyun.mns.MNSClientConfig;

import java.util.Map;

/**
 * MNS客户端集合自动配置
 *
 * @author frankcl
 * @date 2024-01-12 17:06:36
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Configuration
@ConfigurationProperties(prefix = "weapon.aliyun.mns.client")
public class MNSClientMapConfig extends MNSClientConfig {

    public Map<String, MNSClientConfig> many;
}
