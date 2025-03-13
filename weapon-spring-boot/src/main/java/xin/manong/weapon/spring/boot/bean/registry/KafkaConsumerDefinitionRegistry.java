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
import xin.manong.weapon.base.kafka.KafkaConsumeConfig;
import xin.manong.weapon.spring.boot.bean.wrap.KafkaConsumerBean;
import xin.manong.weapon.spring.boot.configuration.KafkaConsumerMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * kafka消息消费bean定义注册
 *
 * @author frankcl
 * @date 2025-03-04 11:25:16
 */
public class KafkaConsumerDefinitionRegistry extends KafkaBeanDefinitionRegistry
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(KafkaConsumerDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.common.kafka.consumer";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, KafkaConsumeConfig> configMap = new HashMap<>();
        try {
            KafkaConsumerMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(KafkaConsumerMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind kafka consumer map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, KafkaConsumeConfig> entry : configMap.entrySet()) {
            String name = String.format("%sKafkaConsumer", entry.getKey());
            KafkaConsumeConfig config = entry.getValue();
            fillAuthConfig(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(
                    KafkaConsumerBean.class, () -> new KafkaConsumerBean(config));
            beanDefinition.setInitMethodName("start");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(false);
            beanDefinition.setDestroyMethodName("stop");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register kafka consumer bean definition success for name[{}]", name);
        }
    }
}
