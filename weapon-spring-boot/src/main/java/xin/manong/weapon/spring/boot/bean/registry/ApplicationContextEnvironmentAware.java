package xin.manong.weapon.spring.boot.bean.registry;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

/**
 * 应用上下文及环境变量感知实现
 *
 * @author frankcl
 * @date 2025-03-04 10:54:10
 */
public class ApplicationContextEnvironmentAware implements EnvironmentAware, ApplicationContextAware {

    protected Environment environment;
    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
