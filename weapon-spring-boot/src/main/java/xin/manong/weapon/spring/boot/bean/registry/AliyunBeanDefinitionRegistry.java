package xin.manong.weapon.spring.boot.bean.registry;

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
import org.springframework.lang.NonNull;
import xin.manong.weapon.aliyun.secret.AliyunSecret;
import xin.manong.weapon.aliyun.secret.DynamicSecretSupport;

/**
 * 阿里云bean定义注册父类
 * 完成阿里云秘钥绑定
 *
 * @author frankcl
 * @date 2022-08-26 13:09:23
 */
public abstract class AliyunBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor,
        EnvironmentAware, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(AliyunBeanDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.secret";

    private AliyunSecret aliyunSecret;
    protected Environment environment;
    protected ApplicationContext applicationContext;

    /**
     * 根据配置绑定阿里云秘钥
     */
    private void bindAliyunSecret() {
        try {
            if (aliyunSecret != null);
            aliyunSecret = Binder.get(environment).bind(BINDING_KEY, Bindable.of(AliyunSecret.class)).get();
        } catch (Exception e) {
            logger.warn("bind aliyun secret failed");
        }
    }

    /**
     * 为配置填充阿里云秘钥
     *
     * @param config 阿里云bean配置
     */
    protected void fillAliyunSecret(DynamicSecretSupport config) {
        bindAliyunSecret();
        boolean check = aliyunSecret != null && aliyunSecret.check();
        if (check) config.aliyunSecret = aliyunSecret;
        if (!config.dynamic && !check) logger.warn("dynamic secret is not config");
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
