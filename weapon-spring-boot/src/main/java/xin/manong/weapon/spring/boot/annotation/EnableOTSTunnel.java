package xin.manong.weapon.spring.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.bean.registry.OTSTunnelDefinitionRegistry;

import java.lang.annotation.*;

/**
 * 阿里云OTS数据通道启动注解
 *
 * @author frankcl
 * @date 2022-08-26 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({OTSTunnelDefinitionRegistry.class})
public @interface EnableOTSTunnel {
}
