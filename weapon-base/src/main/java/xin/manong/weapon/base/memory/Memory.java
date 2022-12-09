package xin.manong.weapon.base.memory;

/**
 * JVM内存
 *
 * @author frankcl
 * @date 2022-10-28 11:24:37
 */
public class Memory {

    public long useMemory = 0L;
    public long freeMemory = 0L;
    public long heapMemory = 0L;
    public long maxHeapMemory = 0L;

    @Override
    public String toString() {
        return String.format("useMemory=%d, freeMemory=%d, heapMemory=%d, maxHeapMemory=%d",
                useMemory, freeMemory, heapMemory, maxHeapMemory);
    }
}
