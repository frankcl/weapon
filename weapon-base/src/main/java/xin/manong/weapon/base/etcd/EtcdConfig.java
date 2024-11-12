package xin.manong.weapon.base.etcd;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Etcd配置
 *
 * @author frankcl
 * @date 2024-11-11 21:22:40
 */
@Data
public class EtcdConfig {

    private static final Logger logger = LoggerFactory.getLogger(EtcdConfig.class);

    public long connectTimeoutMs = 5000L;
    public List<String> endpoints;
    public String username;
    public String password;

    /**
     * 检测有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (connectTimeoutMs <= 0) connectTimeoutMs = 5000L;
        if (endpoints == null || endpoints.isEmpty()) {
            logger.error("endpoint is empty");
            return false;
        }
        if (StringUtils.isNotEmpty(username) && StringUtils.isEmpty(password)) {
            logger.error("password is empty");
            return false;
        }
        return true;
    }
}
