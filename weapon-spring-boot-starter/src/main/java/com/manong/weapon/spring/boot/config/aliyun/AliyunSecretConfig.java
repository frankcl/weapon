package com.manong.weapon.spring.boot.config.aliyun;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云秘钥配置
 *
 * @author frankcl
 * @date 2022-08-25 13:38:06
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.secret")
public class AliyunSecretConfig {

    public String accessKey;
    public String secretKey;
}
