package xin.manong.weapon.spring.boot.bean;

import com.aliyun.openservices.ons.api.MessageListener;
import org.apache.commons.lang3.StringUtils;
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
import xin.manong.weapon.aliyun.ons.Subscribe;
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
            fillMessageListener(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(
                    ONSConsumer.class, () -> new ONSConsumer(config));
            beanDefinition.setInitMethodName("start");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(false);
            beanDefinition.setDestroyMethodName("stop");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register ONS consumer bean definition success for name[{}]", name);
        }
    }

    /**
     * 从spring上下文填充消息监听器实例
     *
     * @param config 消息消费配置
     * @return 失败抛出异常
     */
    private void fillMessageListener(ONSConsumerConfig config) {
        if (config.subscribes == null || config.subscribes.isEmpty()) {
            logger.error("message subscribe relation is not config");
            throw new RuntimeException("message subscribe relation is not config");
        }
        for (Subscribe subscribe : config.subscribes) {
            if (StringUtils.isEmpty(subscribe.listenerName)) {
                logger.error("message listener name is not config for subscribe[{}]", subscribe.topic);
                throw new RuntimeException(String.format("message listener name is not config for subscribe[%s]",
                        subscribe.topic));
            }
            MessageListener messageListener = (MessageListener) applicationContext.getBean(subscribe.listenerName);
            if (messageListener == null) {
                logger.error("message listener is not found for name[{}]", subscribe.listenerName);
                throw new RuntimeException(String.format("message listener is not found for name[%s]",
                        subscribe.listenerName));
            }
            subscribe.listener = messageListener;
        }
    }
}
