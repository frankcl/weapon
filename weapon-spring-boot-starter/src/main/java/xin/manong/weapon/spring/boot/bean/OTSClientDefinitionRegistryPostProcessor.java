package xin.manong.weapon.spring.boot.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;
import xin.manong.weapon.aliyun.ots.OTSClient;
import xin.manong.weapon.aliyun.ots.OTSClientConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;

import java.util.Map;

/**
 * 阿里云OTS客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
@Component
public class OTSClientDefinitionRegistryPostProcessor extends AliyunBeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(OTSClientDefinitionRegistryPostProcessor.class);

    private final static String BINDING_KEY = "weapon.aliyun.ots.client";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        AliyunSecret secret = bindAliyunSecret();
        Map<String, OTSClientConfig> configMap;
        try {
            configMap = Binder.get(environment).bind(BINDING_KEY, Bindable.mapOf(
                    String.class, OTSClientConfig.class)).get();
        } catch (Exception e) {
            logger.warn("bind OTS client config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, OTSClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sOTSClient", entry.getKey());
            OTSClientConfig config = entry.getValue();
            boolean check = secret != null && secret.check();
            if (check) config.aliyunSecret = secret;
            if (!config.dynamic && !check) logger.error("aliyun secret is not config");
            RootBeanDefinition beanDefinition = new RootBeanDefinition(OTSClient.class, () -> new OTSClient(config));
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register OTS client bean definition success for name[{}]", name);
        }
    }
}
