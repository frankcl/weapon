package xin.manong.weapon.spring.boot.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;
import xin.manong.weapon.aliyun.datahub.DataHubClient;
import xin.manong.weapon.aliyun.datahub.DataHubClientConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;

import java.util.Map;

/**
 * 阿里云DataHub客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
@Component
public class DataHubClientDefinitionRegistryPostProcessor extends AliyunBeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(DataHubClientDefinitionRegistryPostProcessor.class);

    private final static String BINDING_KEY = "weapon.aliyun.datahub.client";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        AliyunSecret secret = bindAliyunSecret();
        Map<String, DataHubClientConfig> configMap;
        try {
            configMap = Binder.get(environment).bind(BINDING_KEY, Bindable.mapOf(
                    String.class, DataHubClientConfig.class)).get();
        } catch (Exception e) {
            logger.warn("bind data hub client config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, DataHubClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sDataHubClient", entry.getKey());
            DataHubClientConfig config = entry.getValue();
            boolean check = secret != null && secret.check();
            if (check) config.aliyunSecret = secret;
            if (!config.dynamic && !check) logger.error("dynamic secret is not config");
            RootBeanDefinition beanDefinition = new RootBeanDefinition(DataHubClient.class, () -> new DataHubClient(config));
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register data hub client bean definition success for name[{}]", name);
        }
    }
}
