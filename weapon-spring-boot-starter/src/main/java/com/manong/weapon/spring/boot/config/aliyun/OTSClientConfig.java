package com.manong.weapon.spring.boot.config.aliyun;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云OTS客户端配置
 *
 * @author frankcl
 * @create 2019-05-28 20:30
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.ots")
public class OTSClientConfig {

    private final static int DEFAULT_RETRY_CNT = 3;
    private final static int DEFAULT_CONNECTION_TIMEOUT_MS = 5000;
    private final static int DEFAULT_CONNECTION_REQUEST_TIMEOUT_MS = 5000;
    private final static int DEFAULT_SOCKET_TIMEOUT_MS = 5000;

    public int retryCnt = DEFAULT_RETRY_CNT;
    public int connectionTimeoutMs = DEFAULT_CONNECTION_TIMEOUT_MS;
    public int connectionRequestTimeoutMs = DEFAULT_CONNECTION_REQUEST_TIMEOUT_MS;
    public int socketTimeoutMs = DEFAULT_SOCKET_TIMEOUT_MS;
    public String endpoint;
    public String instance;
}
