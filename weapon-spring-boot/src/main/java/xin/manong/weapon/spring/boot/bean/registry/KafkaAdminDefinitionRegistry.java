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
import xin.manong.weapon.base.kafka.KafkaAdmin;
import xin.manong.weapon.spring.boot.configuration.KafkaAdminMapConfig;

/**
 * kafka admin定义注册
 *
 * @author frankcl
 * @date 2025-03-04 11:25:16
 */
public class KafkaAdminDefinitionRegistry extends KafkaBeanDefinitionRegistry
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(KafkaAdminDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.common.kafka.admin";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        KafkaAdminMapConfig config;
        try {
            config = Binder.get(environment).bind(BINDING_KEY, Bindable.of(KafkaAdminMapConfig.class)).get();
        } catch (Exception e) {
            logger.warn("Bind kafka admin config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        String name = "kafkaAdmin";
        fillAuthConfig(config);
        RootBeanDefinition beanDefinition = new RootBeanDefinition(KafkaAdmin.class, () -> new KafkaAdmin(config));
        beanDefinition.setInitMethodName("open");
        beanDefinition.setEnforceInitMethod(true);
        beanDefinition.setLazyInit(true);
        beanDefinition.setDestroyMethodName("close");
        beanDefinition.setEnforceDestroyMethod(true);
        beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
        logger.info("Register kafka admin definition success for name:{}", name);
    }
}
