package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.aliyun.ons.ONSConsumerConfig;

import java.util.Map;

/**
 * ONS consumer集合自动配置
 *
 * @author frankcl
 * @date 2023-12-25 17:06:36
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "weapon.aliyun.ons.consumer")
public class ONSConsumerMapConfig extends ONSConsumerConfig {

    public Map<String, ONSConsumerConfig> many;
}
