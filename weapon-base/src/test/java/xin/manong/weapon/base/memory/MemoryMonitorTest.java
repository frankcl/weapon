package xin.manong.weapon.base.memory;

import org.junit.Test;

/**
 * @author frankcl
 * @date 2020-08-03 16:45:33
 */
public class MemoryMonitorTest {

    @Test
    public void testForceFullGC() {
        MemoryMonitor memoryMonitor = new MemoryMonitor(new MemoryMonitorConfig());
        System.out.println("before: " + MemoryMonitor.getCurrentMemory());
        memoryMonitor.forceFullGC();
        System.out.println("after: " + MemoryMonitor.getCurrentMemory());
    }
}
