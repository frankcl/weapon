package xin.manong.weapon.base.rocketmq;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.remoting.RPCHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * RocketMQ消费配置
 *
 * @author frankcl
 * @date 2025-10-31 22:05:11
 */
@Data
public class RocketMQConsumerConfig {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQConsumerConfig.class);

    private static final int DEFAULT_CONSUME_THREAD_NUM = 1;
    private static final int DEFAULT_CONSUME_BATCH_MAX_SIZE = 1;

    public int consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
    public int consumeMessageBatchMaxSize = DEFAULT_CONSUME_BATCH_MAX_SIZE;
    public String consumeId;
    public String endpoints;
    public String instanceId;
    public String username;
    public String password;
    /* 消息监听器bean名称，支持spring boot */
    public String listener;
    public MessageListener messageListener;
    public List<RocketMQSubscribe> subscribes = new ArrayList<>();

    /**
     * 添加订阅信息
     *
     * @param subscribe 订阅信息
     */
    public void addSubscribe(RocketMQSubscribe subscribe) {
        if (subscribe == null || !subscribe.check()) return;
        if (subscribes == null) subscribes = new ArrayList<>();
        for (RocketMQSubscribe s : subscribes) {
            if (s.topic != null && s.topic.equals(subscribe.topic)) {
                logger.warn("Topic:{} has been subscribed", subscribe.topic);
                return;
            }
        }
        subscribes.add(subscribe);
    }

    /**
     * 检测配置合法性
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(endpoints)) {
            logger.error("Endpoints is empty");
            return false;
        }
        if (StringUtils.isEmpty(consumeId)) {
            logger.error("Consume id is empty");
            return false;
        }
        if (subscribes == null || subscribes.isEmpty()) {
            logger.error("Missing subscribes");
            return false;
        }
        for (RocketMQSubscribe subscribe : subscribes) if (!subscribe.check()) return false;
        if (messageListener == null) {
            logger.error("Message listener is not config");
            return false;
        }
        if (consumeThreadNum <= 0) consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
        if (consumeMessageBatchMaxSize <= 0) consumeMessageBatchMaxSize = DEFAULT_CONSUME_BATCH_MAX_SIZE;
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
