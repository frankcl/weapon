package xin.manong.weapon.spring.boot.autoconfigure;

import lombok.Data;
import xin.manong.weapon.base.redis.RedisMode;

import java.util.List;

/**
 * Redis客户端自动配置
 *
 * @author frankcl
 * @date 2023-12-25 17:06:36
 */
@Data
public class RedisClientConfig {

    public Integer connectionPoolSize;
    public Integer timeout;
    public Integer db;
    public String address;
    public String password;
    public String codecClassName;
    public String masterName;
    public String masterAddress;
    public List<String> nodeAddresses;
    public List<String> slaveAddresses;
    public List<String> sentinelAddresses;
    public RedisMode mode;
}
