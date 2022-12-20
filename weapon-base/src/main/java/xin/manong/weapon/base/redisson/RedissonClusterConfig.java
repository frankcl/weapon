package xin.manong.weapon.base.redisson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * redisson集群模式配置信息
 *
 * @author frankcl
 * @date 2022-12-20 17:41:04
 */
public class RedissonClusterConfig extends RedissonConfig {

    private final static Logger logger = LoggerFactory.getLogger(RedissonClusterConfig.class);

    public List<String> nodeAddresses;

    /**
     * 检测有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (nodeAddresses == null || nodeAddresses.isEmpty()) {
            logger.error("node addresses are empty for clustering mode");
            return false;
        }
        nodeAddresses = fillAddress(nodeAddresses);
        Set<String> set = new HashSet<>(nodeAddresses);
        nodeAddresses = new ArrayList<>(set);
        return true;
    }
}
