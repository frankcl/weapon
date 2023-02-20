package xin.manong.weapon.spring.boot.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;
import xin.manong.weapon.aliyun.ons.ONSConsumer;
import xin.manong.weapon.aliyun.ons.ONSConsumerConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;

import java.util.Map;

/**
 * 阿里云ONS消息消费bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
@Component
public class ONSConsumerDefinitionRegistryPostProcessor extends AliyunBeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(ONSConsumerDefinitionRegistryPostProcessor.class);

    private final static String BINDING_KEY = "weapon.aliyun.ons.consumer";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        AliyunSecret secret = bindAliyunSecret();
        Map<String, ONSConsumerConfig> configMap;
        try {
            configMap = Binder.get(environment).bind(BINDING_KEY, Bindable.mapOf(
                    String.class, ONSConsumerConfig.class)).get();
        } catch (Exception e) {
            logger.warn("bind ONS consumer config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, ONSConsumerConfig> entry : configMap.entrySet()) {
            String name = String.format("%sONSConsumer", entry.getKey());
            ONSConsumerConfig config = entry.getValue();
            boolean check = secret != null && secret.check();
            if (check) config.aliyunSecret = secret;
            if (!config.dynamic && !check) logger.error("aliyun secret is not config");
            RootBeanDefinition beanDefinition = new RootBeanDefinition(
                    ONSConsumer.class, () -> new ONSConsumer(config));
            beanDefinition.setDestroyMethodName("stop");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register ONS consumer bean definition success for name[{}]", name);
        }
    }
}
