package com.manong.weapon.spring.boot.annotation;

import com.manong.weapon.spring.boot.bean.ONSProducerDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 阿里云ONS消息生产启动注解
 *
 * @author frankcl
 * @date 2022-08-26 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ONSProducerDefinitionRegistryPostProcessor.class})
public @interface EnableWeaponONSProducer {
}
