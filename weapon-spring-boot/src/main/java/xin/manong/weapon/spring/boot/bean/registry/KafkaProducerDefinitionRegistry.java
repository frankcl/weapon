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
import xin.manong.weapon.base.kafka.KafkaProduceConfig;
import xin.manong.weapon.base.kafka.KafkaProducer;
import xin.manong.weapon.spring.boot.configuration.KafkaProducerMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * kafka消息生产bean定义注册
 *
 * @author frankcl
 * @date 2025-03-04 11:25:16
 */
public class KafkaProducerDefinitionRegistry extends ApplicationContextEnvironmentAware
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(KafkaProducerDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.common.kafka.producer";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, KafkaProduceConfig> configMap = new HashMap<>();
        try {
            KafkaProducerMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(KafkaProducerMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind kafka producer map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, KafkaProduceConfig> entry : configMap.entrySet()) {
            String name = String.format("%sKafkaProducer", entry.getKey());
            KafkaProduceConfig config = entry.getValue();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(KafkaProducer.class, () -> new KafkaProducer(config));
            beanDefinition.setInitMethodName("init");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(true);
            beanDefinition.setDestroyMethodName("destroy");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register kafka producer bean definition success for name[{}]", name);
        }
    }
}
