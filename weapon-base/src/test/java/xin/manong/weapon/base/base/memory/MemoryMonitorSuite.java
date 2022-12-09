package xin.manong.weapon.base.base.memory;

import org.junit.Test;

/**
 * @author frankcl
 * @date 2020-08-03 16:45:33
 */
public class MemoryMonitorSuite {

    @Test
    public void testForceFullGC() {
        MemoryMonitor memoryMonitor = new MemoryMonitor(new MemoryMonitorConfig());
        System.out.println("before: " + memoryMonitor.getCurrentMemory());
        memoryMonitor.forceFullGC();
        System.out.println("after: " + memoryMonitor.getCurrentMemory());
    }
}
