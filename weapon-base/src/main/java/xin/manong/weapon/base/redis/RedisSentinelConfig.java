package xin.manong.weapon.base.redis;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * redis哨兵模式配置信息
 *
 * @author frankcl
 * @date 2022-12-20 17:35:02
 */
@Data
@ConfigurationProperties(prefix = "weapon.common.redis.client.sentinel")
public class RedisSentinelConfig extends RedisConfig {

    private final static Logger logger = LoggerFactory.getLogger(RedisSentinelConfig.class);

    public Integer db;
    public String masterName;
    public List<String> sentinelAddresses;

    /**
     * 检测有效性
     *
     * @return 有效返回false，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (StringUtils.isEmpty(masterName)) {
            logger.error("master name is empty for sentinel mode");
            return false;
        }
        if (sentinelAddresses == null || sentinelAddresses.isEmpty()) {
            logger.error("sentinel addresses are empty for sentinel mode");
            return false;
        }
        sentinelAddresses = fillAddress(sentinelAddresses);
        if (db == null || db < 0) db = 0;
        return true;
    }
}
