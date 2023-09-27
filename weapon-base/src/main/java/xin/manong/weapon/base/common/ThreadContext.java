package xin.manong.weapon.base.common;

/**
 * 线程上下文
 * 用于同一线程中上下文在不同对象中传递
 *
 * @author frankcl
 * @date 2023-02-08 10:39:23
 */
public class ThreadContext {

    private final static ThreadLocal<Context> THREAD_LOCAL_CONTEXT = new ThreadLocal<>();

    /**
     * 设置上下文对象
     *
     * @param context 上下文对象
     */
    public static void setContext(Context context) {
        if (context == null) return;
        THREAD_LOCAL_CONTEXT.set(context);
    }

    /**
     * 获取线程上下文
     *
     * @return 线程上下文
     */
    public static Context getContext() {
        return THREAD_LOCAL_CONTEXT.get();
    }

    /**
     * 移除线程上下文
     */
    public static void removeContext() {
        THREAD_LOCAL_CONTEXT.remove();
    }

    /**
     * 提交上下文信息
     *
     * @param key 记录key
     * @param value 记录值
     */
    public static void commit(String key, Object value) {
        Context context = getContext();
        if (context == null) return;
        context.put(key, value);
    }

    /**
     * 从上下文中移除信息
     *
     * @param key 记录key
     */
    public static void remove(String key) {
        Context context = getContext();
        if (context == null || !context.contains(key)) return;
        context.remove(key);
    }
}
