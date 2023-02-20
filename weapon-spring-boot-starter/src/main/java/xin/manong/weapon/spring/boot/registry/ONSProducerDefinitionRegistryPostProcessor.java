package xin.manong.weapon.spring.boot.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;
import xin.manong.weapon.aliyun.ons.ONSProducer;
import xin.manong.weapon.aliyun.ons.ONSProducerConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;

import java.util.Map;

/**
 * 阿里云ONS消息生产bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
@Component
public class ONSProducerDefinitionRegistryPostProcessor extends AliyunBeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(ONSProducerDefinitionRegistryPostProcessor.class);

    private final static String BINDING_KEY = "weapon.aliyun.ons.producer";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        AliyunSecret secret = bindAliyunSecret();
        Map<String, ONSProducerConfig> configMap;
        try {
            configMap = Binder.get(environment).bind(BINDING_KEY, Bindable.mapOf(
                    String.class, ONSProducerConfig.class)).get();
        } catch (Exception e) {
            logger.warn("bind ONS producer config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, ONSProducerConfig> entry : configMap.entrySet()) {
            String name = String.format("%sONSProducer", entry.getKey());
            ONSProducerConfig config = entry.getValue();
            boolean check = secret != null && secret.check();
            if (check) config.aliyunSecret = secret;
            if (!config.dynamic && !check) logger.error("aliyun secret is not config");
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
