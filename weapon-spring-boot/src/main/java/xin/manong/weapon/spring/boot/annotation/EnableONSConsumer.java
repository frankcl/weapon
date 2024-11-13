package xin.manong.weapon.spring.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.bean.registry.ONSConsumerDefinitionRegistry;

import java.lang.annotation.*;

/**
 * 阿里云ONS消息消费启动注解
 *
 * @author frankcl
 * @date 2022-08-26 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ONSConsumerDefinitionRegistry.class})
public @interface EnableONSConsumer {
}
