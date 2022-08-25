package com.manong.weapon.spring.boot.config.aliyun;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云OSS客户端配置
 *
 * @author frankcl
 * @date 2022-08-25 13:48:20
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.oss")
public class OSSClientConfig {

    private final static int DEFAULT_RETRY_CNT = 3;
    private final static int DEFAULT_CONNECTION_TIMEOUT_MS = 3000;
    private final static int DEFAULT_SOCKET_TIMEOUT_MS = 10000;

    public int retryCnt = DEFAULT_RETRY_CNT;
    public int connectionTimeoutMs = DEFAULT_CONNECTION_TIMEOUT_MS;
    public int socketTimeoutMs = DEFAULT_SOCKET_TIMEOUT_MS;
    public String endpoint;
}
