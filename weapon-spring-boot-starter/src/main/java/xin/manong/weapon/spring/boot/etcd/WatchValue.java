package xin.manong.weapon.spring.boot.etcd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * etcd监听字段注解
 * 完整etcd字段为namespace + name
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface WatchValue {

    /**
     * 名字空间
     *
     * @return 名字空间，缺省值为''
     */
    String namespace() default "";

    /**
     * 监听字段
     *
     * @return 监听字段
     */
    String key();
}
