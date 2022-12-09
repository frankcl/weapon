package xin.manong.weapon.aliyun.oss;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.secret.AliyunSecret;

/**
 * OSS客户端配置
 *
 * @author frankcl
 * @create 2019-08-26 18:28:12
 */
@Data
public class OSSClientConfig {

    private final static Logger logger = LoggerFactory.getLogger(OSSClientConfig.class);

    private final static int DEFAULT_RETRY_CNT = 3;
    private final static int DEFAULT_CONNECTION_TIMEOUT_MS = 3000;
    private final static int DEFAULT_SOCKET_TIMEOUT_MS = 10000;

    public int retryCnt = DEFAULT_RETRY_CNT;
    public int connectionTimeoutMs = DEFAULT_CONNECTION_TIMEOUT_MS;
    public int socketTimeoutMs = DEFAULT_SOCKET_TIMEOUT_MS;
    public String endpoint;
    public AliyunSecret aliyunSecret;

    /**
     * 检测配置信息
     *
     * @return 如果合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(endpoint)) {
            logger.error("endpoint is empty");
            return false;
        }
        if (aliyunSecret == null || !aliyunSecret.check()) {
            logger.error("invalid aliyun secret");
            return false;
        }
        return true;
    }

}
