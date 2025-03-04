package xin.manong.weapon.base.redis;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * redis内存信息
 *
 * @author frankcl
 * @date 2023-03-08 14:00:47
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedisMemory {

    @JSONField(name = "used_memory")
    @JsonProperty("used_memory")
    public Long usedMemoryBytes = 0L;
    @JSONField(name = "used_memory_lua")
    @JsonProperty("used_memory_lua")
    public Long usedMemoryLuaBytes = 0L;
    @JSONField(name = "used_memory_rss")
    @JsonProperty("used_memory_rss")
    public Long usedMemoryRssBytes = 0L;
    @JSONField(name = "used_memory_scripts")
    @JsonProperty("used_memory_scripts")
    public Long usedMemoryScriptsBytes = 0L;
    @JSONField(name = "maxmemory")
    @JsonProperty("maxmemory")
    public Long maxMemoryBytes = 0L;
    @JSONField(name = "total_system_memory")
    @JsonProperty("total_system_memory")
    public Long totalSystemMemoryBytes = 0L;
}
