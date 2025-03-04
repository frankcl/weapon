package xin.manong.weapon.spring.boot.bean.registry;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.lang.NonNull;
import xin.manong.weapon.base.redis.*;
import xin.manong.weapon.spring.boot.configuration.RedisClientConfig;
import xin.manong.weapon.spring.boot.configuration.RedisClientMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * redis客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-12-21 10:32:05
 */
public class RedisClientDefinitionRegistry extends ApplicationContextEnvironmentAware
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(RedisClientDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.common.redis.client";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, RedisClientConfig> configMap = new HashMap<>();
        try {
            RedisClientMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(RedisClientMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind redis client map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, RedisClientConfig> entry : configMap.entrySet()) {
            RedisClientConfig config = entry.getValue();
            RedisMode redisMode = config.mode == null ? RedisMode.SINGLE : config.mode;
            RootBeanDefinition beanDefinition = null;
            switch (redisMode) {
                case SINGLE:
                    RedisSingleConfig redisSingleConfig = JSON.parseObject(
                            JSON.toJSONString(config), RedisSingleConfig.class);
                    beanDefinition = new RootBeanDefinition(RedisClient.class,
                            () -> RedisClient.buildRedisClient(redisSingleConfig));
                    break;
                case MASTER_SLAVE:
                    RedisMasterSlaveConfig redisMasterSlaveConfig = JSON.parseObject(
                            JSON.toJSONString(config), RedisMasterSlaveConfig.class);
                    beanDefinition = new RootBeanDefinition(RedisClient.class,
                            () -> RedisClient.buildRedisClient(redisMasterSlaveConfig));
                    break;
                case CLUSTER:
                    RedisClusterConfig redisClusterConfig = JSON.parseObject(
                            JSON.toJSONString(config), RedisClusterConfig.class);
                    beanDefinition = new RootBeanDefinition(RedisClient.class,
                            () -> RedisClient.buildRedisClient(redisClusterConfig));
                    break;
                case SENTINEL:
                    RedisSentinelConfig redisSentinelConfig = JSON.parseObject(
                            JSON.toJSONString(config), RedisSentinelConfig.class);
                    beanDefinition = new RootBeanDefinition(RedisClient.class,
                            () -> RedisClient.buildRedisClient(redisSentinelConfig));
                    break;
            }
            String name = String.format("%sRedisClient", entry.getKey());
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register redis client bean definition success for name[{}]", name);
        }
    }
}
