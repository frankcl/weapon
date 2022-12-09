package xin.manong.weapon.spring.boot.boot.config.aliyun;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import xin.manong.weapon.aliyun.aliyun.secret.AliyunSecret;

/**
 * 阿里云秘钥配置
 *
 * @author frankcl
 * @date 2022-08-25 13:38:06
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.secret")
public class AliyunSecretConfig {

    private final static Logger logger = LoggerFactory.getLogger(AliyunSecretConfig.class);

    public String accessKey;
    public String secretKey;

    @Bean
    @ConditionalOnProperty(prefix = "weapon.aliyun.secret", value = { "access-key", "secret-key" })
    public AliyunSecret buildAliyunSecret() {
        AliyunSecret secret = new AliyunSecret();
        secret.accessKey = accessKey;
        secret.secretKey = secretKey;
        logger.info("build aliyun secret success for weapon starter");
        return secret;
    }
}
