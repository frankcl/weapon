package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.aliyun.dashscope.EmbeddingClientConfig;

import java.util.Map;

/**
 * Embedding客户端集合自动配置
 *
 * @author frankcl
 * @date 2026-02-05 10:06:36
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Configuration
@ConfigurationProperties(prefix = "weapon.aliyun.embedding.client")
public class EmbeddingClientMapConfig extends EmbeddingClientConfig {

    public Map<String, EmbeddingClientConfig> many;
}
