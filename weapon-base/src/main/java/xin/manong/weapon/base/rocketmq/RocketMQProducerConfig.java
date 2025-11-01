package xin.manong.weapon.base.rocketmq;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.remoting.RPCHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RocketMQ消息生产配置
 *
 * @author frankcl
 * @date 2025-10-31 21:27:03
 */
@Data
public class RocketMQProducerConfig {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQProducerConfig.class);

    private static final int DEFAULT_RETRY_CNT = 3;
    private static final int DEFAULT_SEND_MSG_TIMEOUT = 3000;
    private static final String DEFAULT_PRODUCER_GROUP = "ROCKETMQ_PRODUCER";

    public int retryCnt = DEFAULT_RETRY_CNT;
    public int sendMsgTimeout = DEFAULT_SEND_MSG_TIMEOUT;
    public String username;
    public String password;
    public String endpoints;
    public String instanceId;
    public String producerGroup;

    /**
     * 检测合法性
     *
     * @return 如果合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(endpoints)) {
            logger.error("server url is empty");
            return false;
        }
        if (StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            logger.error("username is empty");
            return false;
        }
        if (!StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
            logger.error("password is empty");
            return false;
        }
        if (StringUtils.isEmpty(producerGroup)) producerGroup = DEFAULT_PRODUCER_GROUP;
        if (retryCnt <= 0) retryCnt = DEFAULT_RETRY_CNT;
        if (sendMsgTimeout <= 0) sendMsgTimeout = DEFAULT_SEND_MSG_TIMEOUT;
        return true;
    }

    /**
     * 构建RPCHook
     *
     * @return 用户名密码为空返回null，否则返回RPCHook
     */
    public RPCHook buildRPCHook() {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) return null;
        return new AclClientRPCHook(new SessionCredentials(username, password));
    }
}
