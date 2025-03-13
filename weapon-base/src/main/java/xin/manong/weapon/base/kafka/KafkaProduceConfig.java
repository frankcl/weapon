package xin.manong.weapon.base.kafka;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka消息生产配置
 *
 * @author frankcl
 * @date 2023-01-05 18:05:41
 */
@Data
public class KafkaProduceConfig extends KafkaAuthSupport {

    private final static Logger logger = LoggerFactory.getLogger(KafkaProduceConfig.class);

    private final static int DEFAULT_RETRY_CNT = 3;
    private final static int DEFAULT_REQUEST_TIMEOUT_MS = 3000;

    public int retryCnt = DEFAULT_RETRY_CNT;
    public int requestTimeoutMs = DEFAULT_REQUEST_TIMEOUT_MS;
    public String servers;

    /**
     * 检测配置有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (StringUtils.isEmpty(servers)) {
            logger.error("kafka servers are empty");
            return false;
        }
        if (retryCnt <= 0) retryCnt = DEFAULT_RETRY_CNT;
        if (requestTimeoutMs <= 0) requestTimeoutMs = DEFAULT_REQUEST_TIMEOUT_MS;
        return true;
    }
}
