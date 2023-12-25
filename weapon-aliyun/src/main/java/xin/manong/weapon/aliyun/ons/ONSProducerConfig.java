package xin.manong.weapon.aliyun.ons;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import xin.manong.weapon.aliyun.secret.DynamicSecretConfig;

/**
 * ONS消息生产配置
 *
 * @author frankcl
 * @create 2019-06-11 19:05
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.ons.producer")
public class ONSProducerConfig extends DynamicSecretConfig {

    private final static Logger logger = LoggerFactory.getLogger(ONSProducerConfig.class);

    private final static int DEFAULT_RETRY_CNT = 3;
    private final static int DEFAULT_REQUEST_TIMEOUT_MS = 3000;

    public int retryCnt = DEFAULT_RETRY_CNT;
    public int requestTimeoutMs = DEFAULT_REQUEST_TIMEOUT_MS;
    public String serverURL;

    /**
     * 检测合法性
     *
     * @return 如果合法返回true，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (StringUtils.isEmpty(serverURL)) {
            logger.error("server url is empty");
            return false;
        }
        return true;
    }
}
