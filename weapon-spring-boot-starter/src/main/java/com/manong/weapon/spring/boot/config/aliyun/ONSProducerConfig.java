package com.manong.weapon.spring.boot.config.aliyun;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云ONS消息生产配置
 *
 * @author frankcl
 * @create 2019-06-11 19:05
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.ons")
public class ONSProducerConfig {

    private final static int DEFAULT_RETRY_CNT = 3;
    private final static int DEFAULT_REQUEST_TIMEOUT_MS = 3000;

    public int retryCnt = DEFAULT_RETRY_CNT;
    public int requestTimeoutMs = DEFAULT_REQUEST_TIMEOUT_MS;
    public String serverURL;
}
