package xin.manong.weapon.aliyun.mns;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.model.ErrorMessageResult;
import com.aliyun.mns.model.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.rebuild.RebuildListener;
import xin.manong.weapon.base.rebuild.RebuildManager;
import xin.manong.weapon.base.rebuild.Rebuildable;
import xin.manong.weapon.aliyun.secret.DynamicSecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 阿里云MNS客户端封装，支持AK/SK动态更新
 *
 * @author frankcl
 * @date 2024-01-12 10:38:36
 */
public class MNSClient implements Rebuildable {

    private static final Logger logger = LoggerFactory.getLogger(MNSClient.class);

    private final MNSClientConfig config;
    private com.aliyun.mns.client.MNSClient client;
    private Map<String, CloudQueue> queueMap;
    private final List<RebuildListener> rebuildListeners;

    public MNSClient(MNSClientConfig config) {
        if (config == null || !config.check()) throw new IllegalArgumentException("MNS client config is invalid");
        this.config = config;
        build();
        this.rebuildListeners = new ArrayList<>();
        if (this.config.dynamic) RebuildManager.register(this);
    }

    /**
     * 关闭客户端实例
     */
    public void close() {
        logger.info("MNS client is closing ...");
        if (config.dynamic) RebuildManager.unregister(this);
        if (client != null) client.close();
        logger.info("MNS client has been closed");
    }

    @Override
    public void rebuild() {
        logger.info("MNS client is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("secret is not changed, ignore MNS client rebuilding");
            return;
        }
        config.aliyunSecret.accessKey = DynamicSecret.accessKey;
        config.aliyunSecret.secretKey = DynamicSecret.secretKey;
        com.aliyun.mns.client.MNSClient prevClient = client;
        build();
        if (prevClient != null) prevClient.close();
        for (RebuildListener rebuildListener : rebuildListeners) {
            rebuildListener.onRebuild(this);
        }
        logger.info("MNS client rebuild success");
    }

    /**
     * 获取队列引用对象
     *
     * @param queueName 队列名称
     * @return 队列引用对象
     */
    public CloudQueue getQueue(String queueName) {
        CloudQueue queue = queueMap.getOrDefault(queueName, null);
        if (queue != null) return queue;
        queue = client.getQueueRef(queueName);
        queueMap.put(queueName, queue);
        return queue;
    }

    /**
     * 发送消息到队列
     *
     * @param queueName 队列名称
     * @param messageBody 消息体
     * @return 成功返回消息ID，否则返回null
     */
    public String sendQueueMessage(String queueName, byte[] messageBody) {
        if (StringUtils.isEmpty(queueName)) {
            logger.error("queue name is empty");
            return null;
        }
        if (messageBody == null || messageBody.length == 0) {
            logger.error("message body is empty");
            return null;
        }
        try {
            CloudQueue queue = getQueue(queueName);
            Message message = new Message(messageBody);
            Message response = queue.putMessage(message);
            if (response.isErrorMessage()) {
                ErrorMessageResult errorMessageResult = response.getErrorMessage();
                logger.error("send message failed, code[{}] and cause[{}]",
                        errorMessageResult.getErrorCode(), errorMessageResult.getErrorMessage());
                return null;
            }
            return response.getMessageId();
        } catch (Exception e) {
            logger.error("exception occurred when sending message, cause[{}]", e.getMessage());
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 添加重建监听器
     *
     * @param listener 重建监听器
     */
    public void addRebuildListener(RebuildListener listener) {
        if (listener == null) return;
        rebuildListeners.add(listener);
    }

    /**
     * 构建实例
     */
    private void build() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxConnections(config.maxConnections);
        clientConfiguration.setMaxConnectionsPerRoute(config.maxConnectionsPerRoute);
        clientConfiguration.setSocketTimeout(config.socketTimeoutMs);
        clientConfiguration.setConnectionTimeout(config.connectTimeoutMs);
        CloudAccount cloudAccount = new CloudAccount(config.aliyunSecret.accessKey,
                config.aliyunSecret.secretKey, config.endpoint, clientConfiguration);
        client = cloudAccount.getMNSClient();
        queueMap = new ConcurrentHashMap<>();
    }
}
