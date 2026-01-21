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
import xin.manong.weapon.aliyun.oss.OSSClient;
import xin.manong.weapon.aliyun.oss.OSSClientConfig;
import xin.manong.weapon.spring.boot.configuration.OSSClientMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云OSS客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
public class OSSClientDefinitionRegistry extends AliyunBeanDefinitionRegistry
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(OSSClientDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.oss.client";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, OSSClientConfig> configMap = new HashMap<>();
        try {
            OSSClientMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(OSSClientMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("Bind OSS client map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, OSSClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sOSSClient", entry.getKey());
            OSSClientConfig config = entry.getValue();
            fillSecret(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(OSSClient.class, () -> new OSSClient(config));
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("Register OSS client bean definition success for name:{}", name);
        }
    }
}
