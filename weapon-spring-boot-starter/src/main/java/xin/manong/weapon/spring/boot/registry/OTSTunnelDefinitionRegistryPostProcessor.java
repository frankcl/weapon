package xin.manong.weapon.spring.boot.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;
import xin.manong.weapon.aliyun.ots.OTSTunnelConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;
import xin.manong.weapon.spring.boot.bean.OTSTunnelBean;

import java.util.Map;

/**
 * 阿里云OTS数据通道bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
@Component
public class OTSTunnelDefinitionRegistryPostProcessor extends AliyunBeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelDefinitionRegistryPostProcessor.class);

    private final static String BINDING_KEY = "weapon.aliyun.ots.tunnel-map";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        AliyunSecret secret = bindAliyunSecret();
        Map<String, OTSTunnelConfig> configMap;
        try {
            configMap = Binder.get(environment).bind(BINDING_KEY, Bindable.mapOf(
                    String.class, OTSTunnelConfig.class)).get();
        } catch (Exception e) {
            logger.warn("bind OTS tunnel config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, OTSTunnelConfig> entry : configMap.entrySet()) {
            String name = String.format("%sOTSTunnel", entry.getKey());
            OTSTunnelConfig config = entry.getValue();
            boolean check = secret != null && secret.check();
            if (check) config.aliyunSecret = secret;
            if (!config.dynamic && !check) logger.error("dynamic secret is not config");
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
