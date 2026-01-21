package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.base.etcd.EtcdConfig;

import java.util.Map;

/**
 * etcd客户端集合自动化配置
 *
 * @author frankcl
 * @date 2024-11-12 13:35:39
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Configuration
@ConfigurationProperties(prefix = "weapon.common.etcd.client")
public class EtcdClientMapConfig extends EtcdConfig {

    public Map<String, EtcdConfig> many;
}
