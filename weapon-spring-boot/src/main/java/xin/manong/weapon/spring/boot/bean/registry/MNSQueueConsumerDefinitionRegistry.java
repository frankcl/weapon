package xin.manong.weapon.spring.boot.bean.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.lang.NonNull;
import xin.manong.weapon.aliyun.mns.MNSQueueConsumerConfig;
import xin.manong.weapon.spring.boot.bean.wrap.MNSQueueConsumerBean;
import xin.manong.weapon.spring.boot.configuration.MNSQueueConsumerMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云MNS消息消费bean定义注册
 *
 * @author frankcl
 * @date 2024-01-12 11:25:16
 */
public class MNSQueueConsumerDefinitionRegistry extends AliyunBeanDefinitionRegistry {

    private final static Logger logger = LoggerFactory.getLogger(MNSQueueConsumerDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.mns.consumer";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, MNSQueueConsumerConfig> configMap = new HashMap<>();
        try {
            MNSQueueConsumerMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(MNSQueueConsumerMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind MNS queue consumer map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, MNSQueueConsumerConfig> entry : configMap.entrySet()) {
            String name = String.format("%sMNSQueueConsumer", entry.getKey());
            MNSQueueConsumerConfig config = entry.getValue();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(
                    MNSQueueConsumerBean.class, () -> new MNSQueueConsumerBean(config));
            beanDefinition.setInitMethodName("start");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(false);
            beanDefinition.setDestroyMethodName("stop");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register MNS queue consumer bean definition success for name[{}]", name);
        }
    }
}
