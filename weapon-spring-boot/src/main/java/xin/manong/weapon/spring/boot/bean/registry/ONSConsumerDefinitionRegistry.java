package xin.manong.weapon.spring.boot.bean.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.lang.NonNull;
import xin.manong.weapon.aliyun.ons.ONSConsumerConfig;
import xin.manong.weapon.spring.boot.bean.wrap.ONSConsumerBean;
import xin.manong.weapon.spring.boot.configuration.ONSConsumerMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云ONS消息消费bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
public class ONSConsumerDefinitionRegistry extends AliyunBeanDefinitionRegistry {

    private final static Logger logger = LoggerFactory.getLogger(ONSConsumerDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.ons.consumer";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, ONSConsumerConfig> configMap = new HashMap<>();
        try {
            ONSConsumerMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(ONSConsumerMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind ONS consumer map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, ONSConsumerConfig> entry : configMap.entrySet()) {
            String name = String.format("%sONSConsumer", entry.getKey());
            ONSConsumerConfig config = entry.getValue();
            fillAliyunSecret(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(
                    ONSConsumerBean.class, () -> new ONSConsumerBean(config));
            beanDefinition.setInitMethodName("start");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(false);
            beanDefinition.setDestroyMethodName("stop");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register ONS consumer bean definition success for name[{}]", name);
        }
    }
}
