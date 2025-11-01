package xin.manong.weapon.spring.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.bean.registry.RocketMQProducerDefinitionRegistry;

import java.lang.annotation.*;

/**
 * RocketMQ消息生产启动注解
 *
 * @author frankcl
 * @date 2025-10-31 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RocketMQProducerDefinitionRegistry.class})
public @interface EnableRocketMQProducer {
}
