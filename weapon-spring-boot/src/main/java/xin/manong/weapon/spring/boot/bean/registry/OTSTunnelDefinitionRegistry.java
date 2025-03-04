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
import xin.manong.weapon.aliyun.ots.OTSTunnelConfig;
import xin.manong.weapon.spring.boot.bean.wrap.OTSTunnelBean;
import xin.manong.weapon.spring.boot.configuration.OTSTunnelMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云OTS数据通道bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
public class OTSTunnelDefinitionRegistry extends AliyunBeanDefinitionRegistry
        implements BeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.ots.tunnel";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, OTSTunnelConfig> configMap = new HashMap<>();
        try {
            OTSTunnelMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(OTSTunnelMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind OTS tunnel map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, OTSTunnelConfig> entry : configMap.entrySet()) {
            String name = String.format("%sOTSTunnel", entry.getKey());
            OTSTunnelConfig config = entry.getValue();
            fillSecret(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(OTSTunnelBean.class, () ->
                    new OTSTunnelBean(config));
            beanDefinition.setInitMethodName("start");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(false);
            beanDefinition.setDestroyMethodName("stop");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register OTS tunnel bean definition success for name[{}]", name);
        }
    }
}
