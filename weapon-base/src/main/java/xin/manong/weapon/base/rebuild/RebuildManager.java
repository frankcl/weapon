package xin.manong.weapon.base.rebuild;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 可重建对象管理器
 *
 * @author frankcl
 * @date 2022-10-31 16:17:58
 */
public class RebuildManager {

    private static final Logger logger = LoggerFactory.getLogger(RebuildManager.class);

    private static List<Rebuildable> rebuildObjects;

    static {
        rebuildObjects = new ArrayList<>();
    }

    /**
     * 重建对象
     */
    public static void rebuild() {
        if (rebuildObjects == null) return;
        synchronized (RebuildManager.class) {
            for (Rebuildable rebuildObject : rebuildObjects) {
                rebuildObject.rebuild();
            }
        }
    }

    /**
     * 注册重建对象
     *
     * @param rebuildObject 重建对象
     */
    public static void register(Rebuildable rebuildObject) {
        if (rebuildObject == null || rebuildObjects == null) return;
        synchronized (RebuildManager.class) {
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
        synchronized (RebuildManager.class) {
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
