package xin.manong.weapon.spring.boot.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;
import xin.manong.weapon.base.etcd.EtcdClient;
import xin.manong.weapon.base.etcd.EtcdConfig;

import java.util.Map;

/**
 * etcd客户端bean定义注册
 *
 * @author frankcl
 * @date 2024-11-12 13:38:19
 */
@Component
public class EtcdClientDefinitionRegistryPostProcessor extends AliyunBeanDefinitionRegistryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EtcdClientDefinitionRegistryPostProcessor.class);
    private static final String BINDING_KEY = "weapon.common.etcd.client-map";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, EtcdConfig> configMap;
        try {
            configMap = Binder.get(environment).bind(BINDING_KEY, Bindable.mapOf(
                    String.class, EtcdConfig.class)).get();
        } catch (Exception e) {
            logger.warn("bind etcd client config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, EtcdConfig> entry : configMap.entrySet()) {
            String name = String.format("%sEtcdClient", entry.getKey());
            EtcdConfig config = entry.getValue();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(EtcdClient.class, () -> new EtcdClient(config));
            beanDefinition.setDestroyMethodName("close");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register etcd client bean definition success for name[{}]", name);
        }
    }
}
