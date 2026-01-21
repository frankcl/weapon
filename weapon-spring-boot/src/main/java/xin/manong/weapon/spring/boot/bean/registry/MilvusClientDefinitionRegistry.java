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
import xin.manong.weapon.base.milvus.MilvusClient;
import xin.manong.weapon.base.milvus.MilvusClientConfig;
import xin.manong.weapon.spring.boot.configuration.MilvusClientMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Milvus客户端bean定义注册
 *
 * @author frankcl
 * @date 2026-01-12 13:38:19
 */
public class MilvusClientDefinitionRegistry extends ApplicationContextEnvironmentAware
        implements BeanDefinitionRegistryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MilvusClientDefinitionRegistry.class);
    private static final String BINDING_KEY = "weapon.common.milvus.client";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, MilvusClientConfig> configMap = new HashMap<>();
        try {
            MilvusClientMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(MilvusClientMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("Bind milvus client map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, MilvusClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sMilvusClient", entry.getKey());
            MilvusClientConfig config = entry.getValue();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(
                    MilvusClient.class, () -> new MilvusClient(config));
            beanDefinition.setInitMethodName("open");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(false);
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("Register milvus client bean definition success for name:{}", name);
        }
    }
}
