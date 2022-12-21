package xin.manong.weapon.spring.boot.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;
import xin.manong.weapon.aliyun.oss.OSSClient;
import xin.manong.weapon.aliyun.oss.OSSClientConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;

import java.util.Map;

/**
 * 阿里云OSS客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
@Component
public class OSSClientDefinitionRegistryPostProcessor extends AliyunBeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(OSSClientDefinitionRegistryPostProcessor.class);

    private final static String BINDING_KEY = "weapon.aliyun.oss";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        AliyunSecret secret = bindAliyunSecret();
        if (secret == null) return;
        Map<String, OSSClientConfig> configMap;
        try {
            configMap = Binder.get(environment).bind(BINDING_KEY, Bindable.mapOf(
                    String.class, OSSClientConfig.class)).get();
        } catch (Exception e) {
            logger.warn("bind OSS client config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, OSSClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sOSSClient", entry.getKey());
            OSSClientConfig config = entry.getValue();
            config.aliyunSecret = secret;
            RootBeanDefinition beanDefinition = new RootBeanDefinition(OSSClient.class, () -> new OSSClient(config));
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register OSS client bean definition success for name[{}]", name);
        }
    }
}
