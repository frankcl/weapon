package xin.manong.weapon.base.memory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JVM内存
 *
 * @author frankcl
 * @date 2022-10-28 11:24:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Memory {

    @JsonProperty("use_memory")
    public long useMemory = 0L;
    @JsonProperty("free_memory")
    public long freeMemory = 0L;
    @JsonProperty("heap_memory")
    public long heapMemory = 0L;
    @JsonProperty("max_heap_memory")
    public long maxHeapMemory = 0L;

    @Override
    public String toString() {
        return String.format("useMemory=%d, freeMemory=%d, heapMemory=%d, maxHeapMemory=%d",
                useMemory, freeMemory, heapMemory, maxHeapMemory);
    }
}
