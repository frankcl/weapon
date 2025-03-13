package xin.manong.weapon.spring.boot.bean.registry;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import xin.manong.weapon.base.kafka.KafkaAuthConfig;
import xin.manong.weapon.base.kafka.KafkaAuthSupport;

/**
 * kafka bean注册父类
 * 完成kafka注册认证绑定
 *
 * @author frankcl
 * @date 2025-03-13 13:09:23
 */
public abstract class KafkaBeanDefinitionRegistry extends ApplicationContextEnvironmentAware {

    private final static String BINDING_KEY = "weapon.common.kafka.auth";

    private KafkaAuthConfig authConfig;

    /**
     * 绑定kafka认证配置
     */
    private void bindAuthConfig() {
        try {
            if (authConfig != null) return;
            authConfig = Binder.get(environment).bind(BINDING_KEY, Bindable.of(KafkaAuthConfig.class)).get();
        } catch (Exception ignored) {
        }
    }

    /**
     * 填充kafka认证信息
     *
     * @param config kafka认证配置
     */
    protected void fillAuthConfig(KafkaAuthSupport config) {
        bindAuthConfig();
        if (config.authConfig == null) config.authConfig = authConfig;
    }
}
