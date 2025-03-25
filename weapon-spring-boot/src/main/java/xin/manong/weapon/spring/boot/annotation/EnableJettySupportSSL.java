package xin.manong.weapon.spring.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.ssl.JettySupportSSLCustomizer;

import java.lang.annotation.*;

/**
 * jetty HTTP和HTTPS同时支持启动注解
 *
 * @author frankcl
 * @date 2025-03-25 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({JettySupportSSLCustomizer.class})
public @interface EnableJettySupportSSL {
}
