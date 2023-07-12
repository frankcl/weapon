package xin.manong.weapon.spring.web.ws.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解：开启Web日志切面
 *
 * @author frankcl
 * @date 2022-08-23 13:04:01
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableWebLogAspect {

    boolean commitResponse() default true;
}
