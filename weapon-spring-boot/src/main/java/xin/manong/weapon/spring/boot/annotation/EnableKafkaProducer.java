package xin.manong.weapon.spring.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.bean.registry.KafkaProducerDefinitionRegistry;

import java.lang.annotation.*;

/**
 * kafka消息生产启动注解
 *
 * @author frankcl
 * @date 2025-03-04 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({KafkaProducerDefinitionRegistry.class})
public @interface EnableKafkaProducer {
}
