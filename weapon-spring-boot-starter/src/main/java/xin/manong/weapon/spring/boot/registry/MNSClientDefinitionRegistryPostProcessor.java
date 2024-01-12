package xin.manong.weapon.spring.boot.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;
import xin.manong.weapon.aliyun.mns.MNSClient;
import xin.manong.weapon.aliyun.mns.MNSClientConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;

import java.util.Map;

/**
 * 阿里云MNS客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-01-12 11:25:16
 */
@Component
public class MNSClientDefinitionRegistryPostProcessor extends AliyunBeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(MNSClientDefinitionRegistryPostProcessor.class);

    private final static String BINDING_KEY = "weapon.aliyun.mns.client-map";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        AliyunSecret secret = bindAliyunSecret();
        Map<String, MNSClientConfig> configMap;
        try {
            configMap = Binder.get(environment).bind(BINDING_KEY, Bindable.mapOf(
                    String.class, MNSClientConfig.class)).get();
        } catch (Exception e) {
            logger.warn("bind MNS client config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, MNSClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sMNSClient", entry.getKey());
            MNSClientConfig config = entry.getValue();
            boolean check = secret != null && secret.check();
            if (check) config.aliyunSecret = secret;
            if (!config.dynamic && !check) logger.error("dynamic secret is not config");
            RootBeanDefinition beanDefinition = new RootBeanDefinition(MNSClient.class, () -> new MNSClient(config));
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register MNS client bean definition success for name[{}]", name);
        }
    }
}
