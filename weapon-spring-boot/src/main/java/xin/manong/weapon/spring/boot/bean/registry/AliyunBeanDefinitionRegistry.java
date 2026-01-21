package xin.manong.weapon.spring.boot.bean.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import xin.manong.weapon.aliyun.secret.AliyunSecret;
import xin.manong.weapon.aliyun.secret.DynamicSecretSupport;

/**
 * 阿里云bean定义注册父类
 * 完成阿里云秘钥绑定
 *
 * @author frankcl
 * @date 2022-08-26 13:09:23
 */
public abstract class AliyunBeanDefinitionRegistry extends ApplicationContextEnvironmentAware {

    private final static Logger logger = LoggerFactory.getLogger(AliyunBeanDefinitionRegistry.class);

    private final static String BINDING_KEY = "weapon.aliyun.secret";

    private AliyunSecret aliyunSecret;

    /**
     * 根据配置绑定阿里云秘钥
     */
    private void bindSecret() {
        try {
            if (aliyunSecret != null) return;
            aliyunSecret = Binder.get(environment).bind(BINDING_KEY, Bindable.of(AliyunSecret.class)).get();
        } catch (Exception e) {
            logger.warn("Bind aliyun secret failed");
        }
    }

    /**
     * 为配置填充阿里云秘钥
     *
     * @param config 阿里云bean配置
     */
    protected void fillSecret(DynamicSecretSupport config) {
        bindSecret();
        boolean check = aliyunSecret != null && aliyunSecret.check();
        if (check) config.aliyunSecret = aliyunSecret;
        if (!config.dynamic && !check) logger.warn("Dynamic secret is not config");
    }
}
