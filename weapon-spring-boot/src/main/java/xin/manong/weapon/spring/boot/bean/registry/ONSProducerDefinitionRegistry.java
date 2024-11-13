package xin.manong.weapon.spring.boot.bean.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.lang.NonNull;
import xin.manong.weapon.aliyun.ons.ONSProducer;
import xin.manong.weapon.aliyun.ons.ONSProducerConfig;
import xin.manong.weapon.spring.boot.configuration.ONSProducerMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云ONS消息生产bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
public class ONSProducerDefinitionRegistry extends AliyunBeanDefinitionRegistry {

    private final static Logger logger = LoggerFactory.getLogger(ONSProducerDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.ons.producer";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, ONSProducerConfig> configMap = new HashMap<>();
        try {
            ONSProducerMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(ONSProducerMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind ONS producer map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, ONSProducerConfig> entry : configMap.entrySet()) {
            String name = String.format("%sONSProducer", entry.getKey());
            ONSProducerConfig config = entry.getValue();
            fillAliyunSecret(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(ONSProducer.class, () -> new ONSProducer(config));
            beanDefinition.setInitMethodName("init");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(true);
            beanDefinition.setDestroyMethodName("destroy");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register ONS producer bean definition success for name[{}]", name);
        }
    }
}
