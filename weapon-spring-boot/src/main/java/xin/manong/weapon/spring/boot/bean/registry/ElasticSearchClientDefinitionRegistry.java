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
import xin.manong.weapon.base.elasticsearch.ElasticSearchClient;
import xin.manong.weapon.base.elasticsearch.ElasticSearchClientConfig;
import xin.manong.weapon.spring.boot.configuration.ElasticSearchClientMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * ES客户端bean定义注册
 *
 * @author frankcl
 * @date 2024-11-12 13:38:19
 */
public class ElasticSearchClientDefinitionRegistry extends ApplicationContextEnvironmentAware
        implements BeanDefinitionRegistryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchClientDefinitionRegistry.class);
    private static final String BINDING_KEY = "weapon.common.elastic.client";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, ElasticSearchClientConfig> configMap = new HashMap<>();
        try {
            ElasticSearchClientMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(ElasticSearchClientMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("Bind elasticsearch client map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, ElasticSearchClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sElasticSearchClient", entry.getKey());
            ElasticSearchClientConfig config = entry.getValue();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(
                    ElasticSearchClient.class, () -> new ElasticSearchClient(config));
            beanDefinition.setInitMethodName("open");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(false);
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("Register elasticsearch client bean definition success for name:{}", name);
        }
    }
}
