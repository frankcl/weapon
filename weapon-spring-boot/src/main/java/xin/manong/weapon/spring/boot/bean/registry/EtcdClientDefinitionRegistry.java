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
import xin.manong.weapon.base.etcd.EtcdClient;
import xin.manong.weapon.base.etcd.EtcdConfig;
import xin.manong.weapon.spring.boot.configuration.EtcdClientMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * etcd客户端bean定义注册
 *
 * @author frankcl
 * @date 2024-11-12 13:38:19
 */
public class EtcdClientDefinitionRegistry extends ApplicationContextEnvironmentAware
        implements BeanDefinitionRegistryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EtcdClientDefinitionRegistry.class);
    private static final String BINDING_KEY = "weapon.common.etcd.client";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, EtcdConfig> configMap = new HashMap<>();
        try {
            EtcdClientMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(EtcdClientMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind etcd client map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, EtcdConfig> entry : configMap.entrySet()) {
            String name = String.format("%sEtcdClient", entry.getKey());
            EtcdConfig config = entry.getValue();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(EtcdClient.class, () -> new EtcdClient(config));
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register etcd client bean definition success for name[{}]", name);
        }
    }
}
