package xin.manong.weapon.spring.boot.registry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;
import xin.manong.weapon.base.redis.*;

import java.util.Map;

/**
 * redis客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-12-21 10:32:05
 */
@Component
public class RedisClientDefinitionRegistryPostProcessor extends AliyunBeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(RedisClientDefinitionRegistryPostProcessor.class);

    private final static String BINDING_KEY = "weapon.common.redis.client-map";
    private final static String KEY_MODE = "mode";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, JSONObject> configMap;
        try {
            configMap = Binder.get(environment).bind(BINDING_KEY, Bindable.mapOf(
                    String.class, JSONObject.class)).get();
        } catch (Exception e) {
            logger.warn("bind redis client config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, JSONObject> entry : configMap.entrySet()) {
            JSONObject json = entry.getValue();
            RedisMode redisMode = json.containsKey(KEY_MODE) ?
                    RedisMode.valueOf(json.getString(KEY_MODE)) : RedisMode.SINGLE;
            RootBeanDefinition beanDefinition = null;
            switch (redisMode) {
                case SINGLE:
                    RedisSingleConfig redisSingleConfig = JSON.toJavaObject(json, RedisSingleConfig.class);
                    beanDefinition = new RootBeanDefinition(RedisClient.class,
                            () -> RedisClient.buildRedisClient(redisSingleConfig));
                    break;
                case MASTER_SLAVE:
                    RedisMasterSlaveConfig redisMasterSlaveConfig = JSON.toJavaObject(
                            json, RedisMasterSlaveConfig.class);
                    beanDefinition = new RootBeanDefinition(RedisClient.class,
                            () -> RedisClient.buildRedisClient(redisMasterSlaveConfig));
                    break;
                case CLUSTER:
                    RedisClusterConfig redisClusterConfig = JSON.toJavaObject(json, RedisClusterConfig.class);
                    beanDefinition = new RootBeanDefinition(RedisClient.class,
                            () -> RedisClient.buildRedisClient(redisClusterConfig));
                    break;
                case SENTINEL:
                    RedisSentinelConfig redisSentinelConfig = JSON.toJavaObject(json, RedisSentinelConfig.class);
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
