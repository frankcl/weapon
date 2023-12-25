package xin.manong.weapon.base.redis;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * redis主从模式配置信息
 *
 * @author frankcl
 * @date 2022-12-20 17:35:02
 */
@Data
@ConfigurationProperties(prefix = "weapon.common.redis.client.master-slave")
public class RedisMasterSlaveConfig extends RedisConfig {

    private final static Logger logger = LoggerFactory.getLogger(RedisMasterSlaveConfig.class);

    public Integer db;
    public String masterAddress;
    public List<String> slaveAddresses;

    /**
     * 检测有效性
     *
     * @return 有效返回false，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (StringUtils.isEmpty(masterAddress)) {
            logger.error("master address is empty for master/slave mode");
            return false;
        }
        masterAddress = fillAddress(masterAddress);
        if (slaveAddresses == null || slaveAddresses.isEmpty()) {
            logger.error("slave addresses are empty for master/slave mode");
            return false;
        }
        slaveAddresses = fillAddress(slaveAddresses);
        if (db == null || db < 0) db = 0;
        return true;
    }
}
