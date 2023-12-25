package xin.manong.weapon.spring.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import xin.manong.weapon.aliyun.ons.ONSConsumerConfig;

import java.util.Map;

/**
 * ONS consumer集合自动配置
 *
 * @author frankcl
 * @date 2023-12-25 17:06:36
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.ons")
public class ONSConsumerMapConfig {

    public Map<String, ONSConsumerConfig> consumerMap;
}
