package xin.manong.weapon.base.memory;

import lombok.Data;

/**
 * JVM内存监控配置
 *
 * @author frankcl
 * @date 2020-08-03 16:38:00
 */
@Data
public class MemoryMonitorConfig {

    private final static long DEFAULT_MIN_FORCE_FULL_GC_INTERVAL_MS = 60000L;
    private final static double DEFAULT_MIN_FORCE_FULL_GC_MEMORY_RATIO = 0.7d;

    public long minForceFullGCInterval = DEFAULT_MIN_FORCE_FULL_GC_INTERVAL_MS;
    public double minForceFullGCMemoryRatio = DEFAULT_MIN_FORCE_FULL_GC_MEMORY_RATIO;
}
