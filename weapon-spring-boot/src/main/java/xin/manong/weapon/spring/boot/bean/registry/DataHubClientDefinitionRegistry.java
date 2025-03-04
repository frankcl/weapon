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
import xin.manong.weapon.aliyun.datahub.DataHubClient;
import xin.manong.weapon.aliyun.datahub.DataHubClientConfig;
import xin.manong.weapon.spring.boot.configuration.DataHubClientMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云DataHub客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
public class DataHubClientDefinitionRegistry extends AliyunBeanDefinitionRegistry
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(DataHubClientDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.datahub.client";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, DataHubClientConfig> configMap = new HashMap<>();
        try {
            DataHubClientMapConfig config = Binder.get(environment).bind(BINDING_KEY,
                    Bindable.of(DataHubClientMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind datahub client map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, DataHubClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sDataHubClient", entry.getKey());
            DataHubClientConfig config = entry.getValue();
            fillSecret(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(DataHubClient.class, () -> new DataHubClient(config));
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register data hub client bean definition success for name[{}]", name);
        }
    }
}
