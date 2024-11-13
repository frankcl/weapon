package xin.manong.weapon.aliyun.datahub;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.secret.DynamicSecretSupport;

/**
 * DataHub客户端配置
 *
 * @author frankcl
 * @date 2023-07-06 14:25:52
 */
@Data
public class DataHubClientConfig extends DynamicSecretSupport {

    private static final Logger logger = LoggerFactory.getLogger(DataHubClientConfig.class);

    private static final int DEFAULT_RETRY_CNT = 3;
    public int retryCnt = DEFAULT_RETRY_CNT;
    public String endpoint;

    /**
     * 检测配置合法性
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (StringUtils.isEmpty(endpoint)) {
            logger.error("endpoint is empty");
            return false;
        }
        return true;
    }
}
