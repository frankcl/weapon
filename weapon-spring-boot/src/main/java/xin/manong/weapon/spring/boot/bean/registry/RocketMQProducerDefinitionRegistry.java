package xin.manong.weapon.spring.boot.bean.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.lang.NonNull;
import xin.manong.weapon.base.rocketmq.RocketMQProducer;
import xin.manong.weapon.base.rocketmq.RocketMQProducerConfig;
import xin.manong.weapon.spring.boot.configuration.RocketMQProducerMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * RocketMQ消息生产bean定义注册
 *
 * @author frankcl
 * @date 2025-10-31 11:25:16
 */
public class RocketMQProducerDefinitionRegistry extends ApplicationContextEnvironmentAware
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(RocketMQProducerDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.common.rocketmq.producer";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, RocketMQProducerConfig> configMap = new HashMap<>();
        try {
            RocketMQProducerMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(RocketMQProducerMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("Bind RocketMQ producer map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, RocketMQProducerConfig> entry : configMap.entrySet()) {
            String name = String.format("%sRocketMQProducer", entry.getKey());
            RocketMQProducerConfig config = entry.getValue();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(
                    RocketMQProducer.class, () -> new RocketMQProducer(config));
            beanDefinition.setInitMethodName("init");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(true);
            beanDefinition.setDestroyMethodName("destroy");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("Register RocketMQ producer bean definition success for name[{}]", name);
        }
    }
}
