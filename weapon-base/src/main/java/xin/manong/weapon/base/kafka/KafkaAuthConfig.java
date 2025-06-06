package xin.manong.weapon.base.kafka;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * kafka认证配置
 *
 * @author frankcl
 * @date 2023-01-06 10:38:52
 */
@Data
public class KafkaAuthConfig {

    private final static Logger logger = LoggerFactory.getLogger(KafkaAuthConfig.class);

    private final static Set<String> SECURITY_PROTOCOLS = new HashSet<>() {{
        add(PROTOCOL_PLAINTEXT); add(PROTOCOL_SSL); add(PROTOCOL_SASL_PLAINTEXT); add(PROTOCOL_SASL_SSL);
    }};

    public static final String PROTOCOL_PLAINTEXT = "PLAINTEXT";
    public static final String PROTOCOL_SSL = "SSL";
    public static final String PROTOCOL_SASL_PLAINTEXT = "SASL_PLAINTEXT";
    public static final String PROTOCOL_SASL_SSL = "SASL_SSL";

    public static final String SECURITY_PROTOCOL = "security.protocol";
    public static final String SASL_MECHANISM = "sasl.mechanism";
    public static final String SASL_JAAS_CONFIG = "sasl.jaas.config";

    public String securityProtocol;
    public String saslMechanism;
    public String saslJaasConfig;

    /**
     * 检测有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(saslJaasConfig)) {
            logger.error("SASL JAAS config is empty");
            return false;
        }
        if (StringUtils.isEmpty(saslMechanism)) {
            logger.error("SASL mechanism is empty");
            return false;
        }
        if (StringUtils.isEmpty(securityProtocol) ||
                !SECURITY_PROTOCOLS.contains(securityProtocol)) {
            logger.error("security protocol[{}] is invalid", securityProtocol);
            return false;
        }
        return true;
    }
}
