package xin.manong.weapon.spring.boot.bean.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.lang.NonNull;
import xin.manong.weapon.aliyun.mns.MNSClient;
import xin.manong.weapon.aliyun.mns.MNSClientConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;
import xin.manong.weapon.spring.boot.configuration.MNSClientMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云MNS客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-01-12 11:25:16
 */
public class MNSClientDefinitionRegistry extends AliyunBeanDefinitionRegistry {

    private final static Logger logger = LoggerFactory.getLogger(MNSClientDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.mns.client";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, MNSClientConfig> configMap = new HashMap<>();
        try {
            MNSClientMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(MNSClientMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind MNS client map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, MNSClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sMNSClient", entry.getKey());
            MNSClientConfig config = entry.getValue();
            fillAliyunSecret(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(MNSClient.class, () -> new MNSClient(config));
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register MNS client bean definition success for name[{}]", name);
        }
    }
}
