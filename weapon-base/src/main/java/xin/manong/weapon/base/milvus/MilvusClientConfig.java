package xin.manong.weapon.base.milvus;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Milvus客户端配置
 *
 * @author frankcl
 * @date 2026-01-19 15:57:22
 */
@Data
public class MilvusClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(MilvusClientConfig.class);

    private static final long DEFAULT_CONNECT_TIMEOUT_MS = 6000;
    private static final long DEFAULT_KEEP_ALIVE_TIMEOUT_MS = 6000;

    public long connectTimeoutMs = DEFAULT_CONNECT_TIMEOUT_MS;
    public long keepAliveTimeoutMs = DEFAULT_KEEP_ALIVE_TIMEOUT_MS;
    public String endpoint;
    public String username;
    public String password;

    /**
     * 验证有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(endpoint)) {
            logger.error("Milvus endpoint is empty");
            return false;
        }
        try {
            new URL(endpoint);
        } catch (MalformedURLException e) {
            logger.error("Milvus endpoint:{} is invalid", endpoint);
            return false;
        }
        if (StringUtils.isNotEmpty(username) && StringUtils.isEmpty(password)) {
            logger.error("Missing password");
            return false;
        }
        if (connectTimeoutMs <= 0) connectTimeoutMs = DEFAULT_CONNECT_TIMEOUT_MS;
        if (keepAliveTimeoutMs <= 0) keepAliveTimeoutMs = DEFAULT_KEEP_ALIVE_TIMEOUT_MS;
        return true;
    }
}
