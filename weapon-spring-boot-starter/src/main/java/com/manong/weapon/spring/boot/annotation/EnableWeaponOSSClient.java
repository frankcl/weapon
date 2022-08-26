package com.manong.weapon.spring.boot.annotation;

import com.manong.weapon.spring.boot.bean.OSSClientDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Import;

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
