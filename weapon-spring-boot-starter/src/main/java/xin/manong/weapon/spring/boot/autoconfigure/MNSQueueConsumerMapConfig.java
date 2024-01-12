package xin.manong.weapon.spring.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import xin.manong.weapon.aliyun.mns.MNSQueueConsumerConfig;

import java.util.Map;

/**
 * MNS队列consumer集合自动配置
 *
 * @author frankcl
 * @date 2024-01-12 17:06:36
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.mns")
public class MNSQueueConsumerMapConfig {

    public Map<String, MNSQueueConsumerConfig> consumerMap;
}
