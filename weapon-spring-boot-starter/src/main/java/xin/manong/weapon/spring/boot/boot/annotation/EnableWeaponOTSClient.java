package xin.manong.weapon.spring.boot.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.boot.bean.OTSClientDefinitionRegistryPostProcessor;

import java.lang.annotation.*;

/**
 * 阿里云OTS客户端启动注解
 *
 * @author frankcl
 * @date 2022-08-26 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({OTSClientDefinitionRegistryPostProcessor.class})
public @interface EnableWeaponOTSClient {
}
