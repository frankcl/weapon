package xin.manong.weapon.aliyun.ots;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.secret.DynamicSecretSupport;

/**
 * OTS客户端配置
 *
 * @author frankcl
 * @date 2019-05-28 20:30
 */
@Data
public class OTSClientConfig extends DynamicSecretSupport {

    private final static Logger logger = LoggerFactory.getLogger(OTSClientConfig.class);

    private final static int DEFAULT_RETRY_CNT = 3;
    private final static int DEFAULT_CONNECTION_TIMEOUT_MS = 5000;
    private final static int DEFAULT_CONNECTION_REQUEST_TIMEOUT_MS = 5000;
    private final static int DEFAULT_SOCKET_TIMEOUT_MS = 5000;

    public int retryCnt = DEFAULT_RETRY_CNT;
    public int connectionTimeoutMs = DEFAULT_CONNECTION_TIMEOUT_MS;
    public int connectionRequestTimeoutMs = DEFAULT_CONNECTION_REQUEST_TIMEOUT_MS;
    public int socketTimeoutMs = DEFAULT_SOCKET_TIMEOUT_MS;
    public String endpoint;
    public String instance;

    /**
     * 检测合法性
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (StringUtils.isEmpty(endpoint)) {
            logger.error("endpoint is empty");
            return false;
        }
        if (StringUtils.isEmpty(instance)) {
            logger.error("instance is empty");
            return false;
        }
        if (retryCnt <= 0) retryCnt = DEFAULT_RETRY_CNT;
        if (socketTimeoutMs <= 0) socketTimeoutMs = DEFAULT_SOCKET_TIMEOUT_MS;
        if (connectionTimeoutMs <= 0) connectionTimeoutMs = DEFAULT_CONNECTION_TIMEOUT_MS;
        if (connectionRequestTimeoutMs <= 0) connectionRequestTimeoutMs = DEFAULT_CONNECTION_REQUEST_TIMEOUT_MS;
        return true;
    }
}
