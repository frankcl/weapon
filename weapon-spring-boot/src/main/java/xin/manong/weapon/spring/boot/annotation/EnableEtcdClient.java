package xin.manong.weapon.spring.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.bean.registry.EtcdClientDefinitionRegistry;

import java.lang.annotation.*;

/**
 * etcd客户端启动注解
 *
 * @author frankcl
 * @date 2024-11-12 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EtcdClientDefinitionRegistry.class})
public @interface EnableEtcdClient {
}
