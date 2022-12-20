package xin.manong.weapon.base.redisson;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * redisson单点服务配置信息
 *
 * @author frankcl
 * @date 2022-12-20 17:20:08
 */
public class RedissonSingleConfig extends RedissonConfig {

    private final static Logger logger = LoggerFactory.getLogger(RedissonSingleConfig.class);

    public Integer db;
    public String address;

    /**
     * 检测配置有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (StringUtils.isEmpty(address)) {
            logger.error("node address is empty for single mode");
            return false;
        }
        address = fillAddress(address);
        if (db == null || db < 0) db = 0;
        return true;
    }
}
