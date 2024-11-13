package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.aliyun.ons.ONSProducerConfig;

import java.util.Map;

/**
 * ONS producer集合自动配置
 *
 * @author frankcl
 * @date 2023-12-25 17:06:36
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "weapon.aliyun.ons.producer")
public class ONSProducerMapConfig extends ONSProducerConfig {

    public Map<String, ONSProducerConfig> many;
}
