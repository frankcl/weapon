package xin.manong.weapon.spring.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.bean.registry.MNSClientDefinitionRegistry;

import java.lang.annotation.*;

/**
 * 阿里云MNS客户端启动注解
 *
 * @author frankcl
 * @date 2024-01-12 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MNSClientDefinitionRegistry.class})
public @interface EnableMNSClient {
}
