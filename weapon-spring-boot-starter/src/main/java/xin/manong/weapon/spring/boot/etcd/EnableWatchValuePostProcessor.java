package xin.manong.weapon.spring.boot.etcd;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * WatchValue Bean后处理器启动注解
 *
 * @author frankcl
 * @date 2024-11-12 11:55:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ WatchValuePostProcessor.class })
public @interface EnableWatchValuePostProcessor {
}
