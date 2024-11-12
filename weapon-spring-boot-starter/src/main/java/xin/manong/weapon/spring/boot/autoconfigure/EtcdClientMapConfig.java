package xin.manong.weapon.spring.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import xin.manong.weapon.base.etcd.EtcdConfig;

import java.util.Map;

/**
 * etcd客户端集合自动化配置
 *
 * @author frankcl
 * @date 2024-11-12 13:35:39
 */
@Data
@ConfigurationProperties(prefix = "weapon.common.etcd")
public class EtcdClientMapConfig {

    public Map<String, EtcdConfig> clientMap;
}
