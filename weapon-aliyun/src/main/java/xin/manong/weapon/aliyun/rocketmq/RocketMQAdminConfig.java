package xin.manong.weapon.aliyun.rocketmq;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.secret.DynamicSecretSupport;

/**
 * RocketMQ管理配置
 *
 * @author frankcl
 * @date 2026-02-09 18:52:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RocketMQAdminConfig extends DynamicSecretSupport {

    private final static Logger logger = LoggerFactory.getLogger(RocketMQAdminConfig.class);

    public String endpoint;

    /**
     * 检测配置合法性
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (StringUtils.isEmpty(endpoint)) {
            logger.error("Endpoint is empty");
            return false;
        }
        return true;
    }
}
