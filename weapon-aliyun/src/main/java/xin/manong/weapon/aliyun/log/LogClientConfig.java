package xin.manong.weapon.aliyun.log;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.secret.DynamicSecretSupport;

/**
 * 阿里云SLS客户端配置
 *
 * @author frankcl
 * @date 2023-05-17 16:59:32
 */
@Data
public class LogClientConfig extends DynamicSecretSupport {

    private static final Logger logger = LoggerFactory.getLogger(LogClientConfig.class);

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
