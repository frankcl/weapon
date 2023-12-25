package xin.manong.weapon.spring.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import xin.manong.weapon.aliyun.ots.OTSTunnelConfig;

import java.util.Map;

/**
 * OTS通道集合自动配置
 *
 * @author frankcl
 * @date 2023-12-25 17:06:36
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.ots")
public class OTSTunnelMapConfig {

    public Map<String, OTSTunnelConfig> tunnelMap;
}
