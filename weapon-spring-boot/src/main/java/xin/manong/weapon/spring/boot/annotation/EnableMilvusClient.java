package xin.manong.weapon.spring.boot.annotation;

import org.springframework.context.annotation.Import;
import xin.manong.weapon.spring.boot.bean.registry.MilvusClientDefinitionRegistry;

import java.lang.annotation.*;

/**
 * Milvus客户端启动注解
 *
 * @author frankcl
 * @date 2026-01-12 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MilvusClientDefinitionRegistry.class})
public @interface EnableMilvusClient {
}
