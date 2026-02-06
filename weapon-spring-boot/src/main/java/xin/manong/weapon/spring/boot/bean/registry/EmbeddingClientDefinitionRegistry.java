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
import xin.manong.weapon.aliyun.dashscope.EmbeddingClient;
import xin.manong.weapon.aliyun.dashscope.EmbeddingClientConfig;
import xin.manong.weapon.spring.boot.configuration.EmbeddingClientMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Embedding客户端bean定义注册
 *
 * @author frankcl
 * @date 2026-02-05 11:25:16
 */
public class EmbeddingClientDefinitionRegistry extends AliyunBeanDefinitionRegistry
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(EmbeddingClientDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.embedding.client";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, EmbeddingClientConfig> configMap = new HashMap<>();
        try {
            EmbeddingClientMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(EmbeddingClientMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("Bind embedding client map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, EmbeddingClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sEmbeddingClient", entry.getKey());
            EmbeddingClientConfig config = entry.getValue();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(
                    EmbeddingClient.class, () -> new EmbeddingClient(config));
            beanDefinition.setInitMethodName("open");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(true);
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("Register embedding client bean definition success for name:{}", name);
        }
    }
}
