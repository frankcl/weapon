package xin.manong.weapon.base.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JVM内存监控
 *
 * @author frankcl
 * @date 2020-08-03 16:27:05
 */
public class MemoryMonitor {

    private final static Logger logger = LoggerFactory.getLogger(MemoryMonitor.class);

    private volatile long lastForceFullGCTime;
    private final MemoryMonitorConfig config;

    public MemoryMonitor(MemoryMonitorConfig config) {
        this.lastForceFullGCTime = 0L;
        this.config = config;
        if (config == null) throw new RuntimeException("memory monitor config is null");
    }

    /**
     * 强制执行FullGC
     */
    public void forceFullGC() {
        long interval = System.currentTimeMillis() - lastForceFullGCTime;
        if (interval < config.minForceFullGCInterval) return;
        synchronized (this) {
            interval = System.currentTimeMillis() - lastForceFullGCTime;
            Memory memory = getCurrentMemory();
            double useMemoryRatio = memory.useMemory * 1.0d / memory.maxHeapMemory;
            if (useMemoryRatio >= config.minForceFullGCMemoryRatio && interval >= config.minForceFullGCInterval) {
                logger.info("force fullGC for use memory ratio[{}], current memory[{}]", useMemoryRatio, memory);
                System.gc();
                lastForceFullGCTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * 获取当前内存信息
     *
     * @return 内存信息
     */
    public static Memory getCurrentMemory() {
        Runtime runtime = Runtime.getRuntime();
        Memory memory = new Memory();
        memory.freeMemory = runtime.freeMemory();
        memory.heapMemory = runtime.totalMemory();
        memory.maxHeapMemory = runtime.maxMemory();
        memory.useMemory = memory.heapMemory - memory.freeMemory;
        return memory;
    }
}
