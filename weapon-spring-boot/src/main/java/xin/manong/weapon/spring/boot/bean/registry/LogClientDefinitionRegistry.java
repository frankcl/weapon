package xin.manong.weapon.spring.boot.bean.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.lang.NonNull;
import xin.manong.weapon.aliyun.log.LogClient;
import xin.manong.weapon.aliyun.log.LogClientConfig;
import xin.manong.weapon.spring.boot.configuration.LogClientMapConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云SLS日志客户端bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
public class LogClientDefinitionRegistry extends AliyunBeanDefinitionRegistry {

    private final static Logger logger = LoggerFactory.getLogger(LogClientDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.log.client";

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Map<String, LogClientConfig> configMap = new HashMap<>();
        try {
            LogClientMapConfig config = Binder.get(environment).bind(
                    BINDING_KEY, Bindable.of(LogClientMapConfig.class)).get();
            if (config.many == null || config.many.isEmpty()) configMap.put("default", config);
            else configMap.putAll(config.many);
        } catch (Exception e) {
            logger.warn("bind log client map config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, LogClientConfig> entry : configMap.entrySet()) {
            String name = String.format("%sLogClient", entry.getKey());
            LogClientConfig config = entry.getValue();
            fillAliyunSecret(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(LogClient.class, () -> new LogClient(config));
            beanDefinition.setInitMethodName("init");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(true);
            beanDefinition.setDestroyMethodName("destroy");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register log client bean definition success for name[{}]", name);
        }
    }
}
