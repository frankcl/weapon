package xin.manong.weapon.spring.boot.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.boot.bean.OSSClientDefinitionRegistryPostProcessor;

import java.lang.annotation.*;

/**
 * 阿里云OSS客户端启动注解
 *
 * @author frankcl
 * @date 2022-08-26 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({OSSClientDefinitionRegistryPostProcessor.class})
public @interface EnableWeaponOSSClient {
}
