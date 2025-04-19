package xin.manong.weapon.aliyun.secret;

import xin.manong.weapon.base.event.EventListener;

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
        ServiceLoader<EventListener> listeners = ServiceLoader.load(EventListener.class);
        for (EventListener eventListener : listeners) ListenerRegistry.register(eventListener);
        ListenerRegistry.start();
    }

    /**
     * 注销动态秘钥监听器
     */
    public static void unregister() {
        ListenerRegistry.stop();
    }
}
