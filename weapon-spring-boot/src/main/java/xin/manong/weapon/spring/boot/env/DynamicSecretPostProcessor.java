package xin.manong.weapon.spring.boot.env;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.etcd.jetcd.watch.WatchResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.*;
import xin.manong.weapon.aliyun.secret.DynamicSecret;
import xin.manong.weapon.base.etcd.EtcdClient;
import xin.manong.weapon.base.etcd.EtcdConfig;
import xin.manong.weapon.base.util.JSONUtil;
import xin.manong.weapon.base.util.MapUtil;
import xin.manong.weapon.spring.boot.configuration.EtcdClientMapConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 环境后处理器：负责阿里云秘钥加载和监听
 *
 * @author frankcl
 * @date 2024-11-23 10:38:35
 */
public class DynamicSecretPostProcessor implements EnvironmentPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DynamicSecretPostProcessor.class);
    private static final String ETCD_CONFIG = "weapon.common.etcd.client";
    private static final String ETCD_KEY_AK_SK = "weapon/aliyun/ak-sk";

    protected EtcdClient etcdClient;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        EtcdClientMapConfig clientMapConfig = parseEtcdConfig(environment);
        if (clientMapConfig == null) {
            logger.error("Etcd client map config is not found");
            throw new IllegalStateException("Etcd client map config is not found");
        }
        EtcdConfig etcdConfig = clientMapConfig.many == null || clientMapConfig.many.isEmpty() ?
                clientMapConfig : clientMapConfig.many.get("default");
        etcdClient = new EtcdClient(etcdConfig);
        String value = etcdClient.get(ETCD_KEY_AK_SK);
        DynamicSecretInjector.inject(value);
        if (StringUtils.isEmpty(DynamicSecret.accessKey) ||
                StringUtils.isEmpty(DynamicSecret.secretKey)) {
            logger.error("Access key or secret key are not config");
            throw new IllegalStateException("Access key or secret key are not config");
        }
        Consumer<WatchResponse> consumer = new DynamicSecretConsumer();
        etcdClient.addWatch(ETCD_KEY_AK_SK, consumer);
    }

    /**
     * 解析etcd配置
     *
     * @param environment 配置环境
     * @return etcd配置
     */
    @SuppressWarnings("unchecked")
    private EtcdClientMapConfig parseEtcdConfig(ConfigurableEnvironment environment) {
        Map<String, Object> flattenMap = new HashMap<>();
        MutablePropertySources propertySources = environment.getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (propertySource instanceof OriginTrackedMapPropertySource originPropertySource) {
                for (Map.Entry<String, Object> entry : originPropertySource.getSource().entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof OriginTrackedValue) value = ((OriginTrackedValue) value).getValue();
                    flattenMap.put(entry.getKey(), value);
                }
            }
        }
        Map<String, Object> multiMap = MapUtil.flattenMapToMultiMap(flattenMap);
        JSONObject json = JSON.parseObject(JSON.toJSONString(multiMap));
        Map<String, Object> configMap = (Map<String, Object>) JSONUtil.get(json, ETCD_CONFIG);
        return JSON.parseObject(JSON.toJSONString(configMap), EtcdClientMapConfig.class);
    }
}
