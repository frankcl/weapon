package xin.manong.weapon.aliyun.ons;

import com.aliyun.openservices.ons.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.rebuild.RebuildManager;
import xin.manong.weapon.base.rebuild.Rebuildable;
import xin.manong.weapon.base.secret.DynamicSecret;

import java.util.Properties;

/**
 * ONS消息发送
 *
 * @author frankcl
 * @create 2019-06-11 19:05
 */
public class ONSProducer implements Rebuildable {

    private final static Logger logger = LoggerFactory.getLogger(ONSProducer.class);

    private ONSProducerConfig config;
    private Producer producer;

    public ONSProducer(ONSProducerConfig config) {
        this.config = config;
    }

    @Override
    public void rebuild() {
        logger.info("ONS producer is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
            DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("secret is not changed, ignore ONS producer rebuilding");
            return;
        }
        config.aliyunSecret.accessKey = DynamicSecret.accessKey;
        config.aliyunSecret.secretKey = DynamicSecret.secretKey;
        Producer prevProducer = producer;
        if (!build()) throw new RuntimeException("rebuild ONS producer failed");
        if (prevProducer != null) prevProducer.shutdown();
        logger.info("ONS producer rebuild success");
    }

    /**
     * 构建ONS producer
     *
     * @return 成功返回true，否则返回false
     */
    private boolean build() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, config.aliyunSecret.accessKey);
        properties.put(PropertyKeyConst.SecretKey, config.aliyunSecret.secretKey);
        properties.put(PropertyKeyConst.SendMsgTimeoutMillis, config.requestTimeoutMs);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, config.serverURL);
        try {
            producer = ONSFactory.createProducer(properties);
            producer.start();
        } catch (Exception e) {
            logger.error("build ONS producer failed");
            return false;
        }
        return true;
    }

    /**
     * 初始化ONS producer
     *
     * @return 成功返回true，否则返回false
     */
    public boolean init() {
        logger.info("ONS producer is init ...");
        if (!config.check()) return false;
        if (!build()) return false;
        if (config.dynamic) RebuildManager.register(this);
        logger.info("ONS producer has finished init");
        return true;
    }

    /**
     * 销毁ONS producer
     */
    public void destroy() {
        logger.info("ONS producer is destroying ...");
        if (config.dynamic) RebuildManager.unregister(this);
        if (producer != null) producer.shutdown();
        logger.info("ONS producer has been destroyed");
    }

    /**
     * 发送消息
     *
     * @param message 推送消息
     * @return 发送成功返回messageId，否则返回null
     */
    public SendResult send(Message message) {
        if (message == null) {
            logger.error("send message is null");
            return null;
        }
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                return producer.send(message);
            } catch (Exception e) {
                logger.error("send message failed for topic[{}], retry {} times", message.getTopic(), i + 1);
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }
}
