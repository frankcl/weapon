package xin.manong.weapon.spring.boot.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xin.manong.weapon.base.milvus.MilvusClientConfig;

import java.util.Map;

/**
 * Milvus客户端集合自动配置
 *
 * @author frankcl
 * @date 2026-01-12 10:06:36
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Configuration
@ConfigurationProperties(prefix = "weapon.common.milvus.client")
public class MilvusClientMapConfig extends MilvusClientConfig {

    public Map<String, MilvusClientConfig> many;
}
