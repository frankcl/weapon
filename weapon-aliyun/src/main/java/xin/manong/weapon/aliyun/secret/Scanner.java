package xin.manong.weapon.aliyun.secret;

import java.util.ServiceLoader;

/**
 * 动态秘钥监听器扫描加载
 *
 * @author frankcl
 * @date 2023-12-07 14:55:09
 */
public class Scanner {

    /**
     * 扫描加载动态秘钥监听器
     *
     * @return 成功返回true，否则返回false
     */
    public static boolean scan() {
        ServiceLoader<DynamicSecretListener> loader = ServiceLoader.load(DynamicSecretListener.class);
        for (DynamicSecretListener listener : loader) {
            if (!listener.start()) return false;
        }
        return true;
    }
}
