package xin.manong.weapon.spring.boot.env;

import xin.manong.weapon.aliyun.secret.DynamicSecret;

/**
 * 动态秘钥解析注入
 *
 * @author frankcl
 * @date 2024-11-23 14:35:28
 */
public class DynamicSecretInjector {

    /**
     * 动态秘钥解析注入
     *
     * @param value 动态秘钥
     */
    public static void inject(String value) {
        String[] keys = value.split("/");
        if (keys.length < 2) throw new IllegalStateException(String.format("Invalid Aliyun secret key:%s", value));
        DynamicSecret.accessKey = keys[0].trim();
        DynamicSecret.secretKey = keys[1].trim();
    }
}
