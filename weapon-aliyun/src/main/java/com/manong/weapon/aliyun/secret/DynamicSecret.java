package com.manong.weapon.aliyun.secret;

import com.manong.weapon.aliyun.common.Rebuildable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 动态秘钥管理
 *
 * @author frankcl
 * @date 2022-10-31 15:19:44
 */
public final class DynamicSecret {

    private final static Logger logger = LoggerFactory.getLogger(DynamicSecret.class);

    public static String accessKey;
    public static String secretKey;

    private static List<Rebuildable> rebuildObjects;

    static {
        rebuildObjects = new ArrayList<>();
        //TODO 秘钥变更监听，触发rebuild对象进行重建
    }

    /**
     * 注册重建对象
     *
     * @param rebuildObject 重建对象
     */
    public static void register(Rebuildable rebuildObject) {
        if (rebuildObject == null || rebuildObjects == null) return;
        synchronized (DynamicSecret.class) {
            for (Rebuildable object : rebuildObjects) {
                if (object != rebuildObject) continue;
                logger.warn("rebuild object[{}] has been registered, ignore it", rebuildObject.getClass().getName());
                return;
            }
            rebuildObjects.add(rebuildObject);
            logger.info("register rebuild object success[{}]", rebuildObject.getClass().getName());
        }
    }

    /**
     * 注销重建对象
     *
     * @param rebuildObject 重建对象
     */
    public static void unregister(Rebuildable rebuildObject) {
        if (rebuildObject == null || rebuildObjects == null) return;
        synchronized (DynamicSecret.class) {
            Iterator<Rebuildable> iterator = rebuildObjects.iterator();
            while (iterator.hasNext()) {
                Rebuildable object = iterator.next();
                if (object != rebuildObject) continue;
                logger.info("unregister rebuild object success[{}]", rebuildObject.getClass().getName());
                iterator.remove();
                return;
            }
        }
    }
}
