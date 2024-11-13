package xin.manong.weapon.spring.boot.bean.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.lang.NonNull;
import xin.manong.weapon.aliyun.ots.OTSClient;
import xin.manong.weapon.aliyun.ots.OTSClientConfig;
import xin.manong.weapon.spring.boot.configuration.OTSClientMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云OTS客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
public class OTSClientDefinitionRegistry extends AliyunBeanDefinitionRegistry {

    private final static Logger logger = LoggerFactory.getLogger(OTSClientDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.ots.client";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, OTSClientConfig> configMap = new HashMap<>();
        try {
            OTSClientMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(OTSClientMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind OTS client map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, OTSClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sOTSClient", entry.getKey());
            OTSClientConfig config = entry.getValue();
            fillAliyunSecret(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(OTSClient.class, () -> new OTSClient(config));
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register OTS client bean definition success for name[{}]", name);
        }
    }
}
