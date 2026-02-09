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
import xin.manong.weapon.aliyun.rocketmq.RocketMQAdmin;
import xin.manong.weapon.aliyun.rocketmq.RocketMQAdminConfig;

/**
 * RocketMQ管理定义注册
 *
 * @author frankcl
 * @date 2025-10-31 11:25:16
 */
public class RocketMQAdminDefinitionRegistry extends ApplicationContextEnvironmentAware
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(RocketMQAdminDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.common.rocketmq.admin";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        RocketMQAdminConfig config;
        try {
            config = Binder.get(environment).bind(BINDING_KEY, Bindable.of(RocketMQAdminConfig.class)).get();
        } catch (Exception e) {
            logger.warn("Bind RocketMQ admin config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        RootBeanDefinition beanDefinition = new RootBeanDefinition(
                RocketMQAdmin.class, () -> new RocketMQAdmin(config));
        beanDefinition.setInitMethodName("init");
        beanDefinition.setEnforceInitMethod(true);
        beanDefinition.setLazyInit(false);
        beanDefinition.setDestroyMethodName("destroy");
        beanDefinition.setEnforceDestroyMethod(true);
        beanDefinitionRegistry.registerBeanDefinition("RocketMQAdmin", beanDefinition);
        logger.info("Register RocketMQ admin definition success for name");
    }
}
