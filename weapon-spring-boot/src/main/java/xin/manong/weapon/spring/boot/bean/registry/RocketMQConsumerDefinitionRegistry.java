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
import xin.manong.weapon.base.rocketmq.RocketMQConsumerConfig;
import xin.manong.weapon.spring.boot.bean.wrap.RocketMQConsumerBean;
import xin.manong.weapon.spring.boot.configuration.RocketMQConsumerMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * RocketMQ消息消费bean定义注册
 *
 * @author frankcl
 * @date 2025-10-31 11:25:16
 */
public class RocketMQConsumerDefinitionRegistry extends ApplicationContextEnvironmentAware
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(RocketMQConsumerDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.common.rocketmq.consumer";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, RocketMQConsumerConfig> configMap = new HashMap<>();
        try {
            RocketMQConsumerMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(RocketMQConsumerMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("Bind RocketMQ consumer map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, RocketMQConsumerConfig> entry : configMap.entrySet()) {
            String name = String.format("%sRocketMQConsumer", entry.getKey());
            RocketMQConsumerConfig config = entry.getValue();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(
                    RocketMQConsumerBean.class, () -> new RocketMQConsumerBean(config));
            beanDefinition.setInitMethodName("start");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(false);
            beanDefinition.setDestroyMethodName("stop");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("Register RocketMQ consumer bean definition success for name[{}]", name);
        }
    }
}
