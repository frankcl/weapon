package com.manong.weapon.spring.boot;

import com.manong.weapon.aliyun.ons.ONSProducer;
import com.manong.weapon.aliyun.oss.OSSClient;
import com.manong.weapon.aliyun.ots.OTSClient;
import com.manong.weapon.aliyun.secret.AliyunSecret;
import com.manong.weapon.spring.boot.config.aliyun.AliyunSecretConfig;
import com.manong.weapon.spring.boot.config.aliyun.ONSProducerConfig;
import com.manong.weapon.spring.boot.config.aliyun.OSSClientConfig;
import com.manong.weapon.spring.boot.config.aliyun.OTSClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * spring boot starter自动配置支持
 *
 * @author frankcl
 * @date 2022-08-25 13:41:45
 */
@Configuration
@EnableConfigurationProperties(value = { AliyunSecretConfig.class, OSSClientConfig.class,
        OTSClientConfig.class, ONSProducerConfig.class })
public class WeaponAutoConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(WeaponAutoConfiguration.class);

    @Resource
    private AliyunSecretConfig secretConfig;
    @Resource
    private OSSClientConfig ossClientConfig;
    @Resource
    private OTSClientConfig otsClientConfig;
    @Resource
    private ONSProducerConfig onsProducerConfig;

    @Bean
    @ConditionalOnProperty(prefix = "weapon.aliyun.secret", value = { "access-key", "secret-key" })
    public AliyunSecret buildAliyunSecret() {
        AliyunSecret secret = new AliyunSecret();
        secret.accessKey = secretConfig.accessKey;
        secret.secretKey = secretConfig.secretKey;
        logger.info("auto build aliyun secret success for weapon starter");
        return secret;
    }

    @Bean
    @ConditionalOnProperty(prefix = "weapon.aliyun.oss", value = "endpoint")
    public OSSClient buildOSSClient(AliyunSecret secret) {
        com.manong.weapon.aliyun.oss.OSSClientConfig ossClientConfig =
                new com.manong.weapon.aliyun.oss.OSSClientConfig();
        ossClientConfig.aliyunSecret = secret;
        ossClientConfig.endpoint = this.ossClientConfig.endpoint;
        ossClientConfig.connectionTimeoutMs = this.ossClientConfig.connectionTimeoutMs;
        ossClientConfig.socketTimeoutMs = this.ossClientConfig.socketTimeoutMs;
        ossClientConfig.retryCnt = this.ossClientConfig.retryCnt;
        logger.info("auto build oss client success for weapon starter");
        return new OSSClient(ossClientConfig);
    }

    @Bean
    @ConditionalOnProperty(prefix = "weapon.aliyun.ots", value = { "instance", "endpoint" })
    public OTSClient buildOTSClient(AliyunSecret secret) {
        com.manong.weapon.aliyun.ots.OTSClientConfig otsClientConfig =
                new com.manong.weapon.aliyun.ots.OTSClientConfig();
        otsClientConfig.aliyunSecret = secret;
        otsClientConfig.instance = this.otsClientConfig.instance;
        otsClientConfig.endpoint = this.otsClientConfig.endpoint;
        otsClientConfig.retryCnt = this.otsClientConfig.retryCnt;
        otsClientConfig.connectionRequestTimeoutMs = this.otsClientConfig.connectionRequestTimeoutMs;
        otsClientConfig.connectionTimeoutMs = this.otsClientConfig.connectionTimeoutMs;
        otsClientConfig.socketTimeoutMs = this.otsClientConfig.socketTimeoutMs;
        logger.info("auto build ots client success for weapon starter");
        return new OTSClient(otsClientConfig);
    }

    @Bean
    @ConditionalOnProperty(prefix = "weapon.aliyun.ons", value = "serverURL")
    public ONSProducer buildONSProducer(AliyunSecret secret) {
        com.manong.weapon.aliyun.ons.ONSProducerConfig onsProducerConfig =
                new com.manong.weapon.aliyun.ons.ONSProducerConfig();
        onsProducerConfig.aliyunSecret = secret;
        onsProducerConfig.requestTimeoutMs = this.onsProducerConfig.requestTimeoutMs;
        onsProducerConfig.retryCnt = this.onsProducerConfig.retryCnt;
        onsProducerConfig.serverURL = this.onsProducerConfig.serverURL;
        return new ONSProducer(onsProducerConfig);
    }
}
