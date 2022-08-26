package com.manong.weapon.spring.boot.bean;

import com.manong.weapon.aliyun.secret.AliyunSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 阿里云bean定义注册父类
 * 完成阿里云秘钥绑定
 *
 * @author frankcl
 * @date 2022-08-26 13:09:23
 */
public abstract class AliyunBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor,
        EnvironmentAware, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(AliyunBeanDefinitionRegistryPostProcessor.class);

    private final static String BINDING_KEY = "weapon.aliyun.secret";

    protected Environment environment;
    protected ApplicationContext applicationContext;

    /**
     * 根据配置绑定阿里云秘钥
     *
     * @return 阿里云秘钥
     */
    protected AliyunSecret bindAliyunSecret() {
        try {
            return Binder.get(environment).bind(BINDING_KEY, Bindable.of(AliyunSecret.class)).get();
        } catch (Exception e) {
            logger.warn("bind aliyun secret failed");
            return null;
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
            throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
