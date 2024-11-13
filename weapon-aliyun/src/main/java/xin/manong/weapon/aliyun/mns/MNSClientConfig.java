package xin.manong.weapon.aliyun.mns;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.secret.DynamicSecretSupport;

/**
 * 阿里云MNS客户端配置
 *
 * @author frankcl
 * @date 2024-01-12 10:27:46
 */
@Data
public class MNSClientConfig extends DynamicSecretSupport {

    private static final Logger logger = LoggerFactory.getLogger(MNSClientConfig.class);

    private static final int DEFAULT_MAX_CONNECTIONS = 200;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 200;
    private static final int DEFAULT_SOCKET_TIMEOUT_MS = 20000;
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 20000;

    public int maxConnections = DEFAULT_MAX_CONNECTIONS;
    public int maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
    public int socketTimeoutMs = DEFAULT_SOCKET_TIMEOUT_MS;
    public int connectTimeoutMs = DEFAULT_CONNECT_TIMEOUT_MS;
    public String endpoint;

    /**
     * 检测合法性
     *
     * @return 如果合法返回true，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (StringUtils.isEmpty(endpoint)) {
            logger.error("endpoint is empty");
            return false;
        }
        if (maxConnections <= 0) maxConnections = DEFAULT_MAX_CONNECTIONS;
        if (maxConnectionsPerRoute <= 0) maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
        if (socketTimeoutMs <= 0) socketTimeoutMs = DEFAULT_SOCKET_TIMEOUT_MS;
        if (connectTimeoutMs <= 0) connectTimeoutMs = DEFAULT_CONNECT_TIMEOUT_MS;
        return true;
    }
}
