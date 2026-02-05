package xin.manong.weapon.spring.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.bean.registry.EmbeddingClientDefinitionRegistry;

import java.lang.annotation.*;

/**
 * 阿里云Embedding客户端启动注解
 *
 * @author frankcl
 * @date 2026-02-05 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EmbeddingClientDefinitionRegistry.class})
public @interface EnableEmbeddingClient {
}
