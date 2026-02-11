package xin.manong.weapon.base.kafka;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka管理器配置
 *
 * @author frankcl
 * @date 2026-02-11 11:24:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KafkaAdminConfig extends KafkaAuthSupport {

    private static final Logger logger = LoggerFactory.getLogger(KafkaAdminConfig.class);

    public String servers;

    /**
     * 检测配置有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (StringUtils.isEmpty(servers)) {
            logger.error("Kafka servers are empty");
            return false;
        }
        return true;
    }
}
