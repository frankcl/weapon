package xin.manong.weapon.base.secret;

import xin.manong.weapon.base.listen.Listener;

import java.util.ServiceLoader;

/**
 * 动态秘钥监听器扫描加载
 *
 * @author frankcl
 * @date 2023-12-07 14:55:09
 */
public class ListenerScanner {

    /**
     * 扫描并注册动态秘钥监听器
     */
    public static void scanRegister() {
        ServiceLoader<Listener> listeners = ServiceLoader.load(Listener.class);
        for (Listener listener : listeners) ListenerRegistry.register(listener);
        ListenerRegistry.start();
    }

    /**
     * 注销动态秘钥监听器
     */
    public static void unregister() {
        ListenerRegistry.stop();
    }
}
