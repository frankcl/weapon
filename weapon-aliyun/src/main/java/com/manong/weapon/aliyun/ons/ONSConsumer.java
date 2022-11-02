package com.manong.weapon.aliyun.ons;

import com.aliyun.openservices.ons.api.*;
import com.manong.weapon.aliyun.common.RebuildListener;
import com.manong.weapon.aliyun.common.RebuildManager;
import com.manong.weapon.aliyun.common.Rebuildable;
import com.manong.weapon.aliyun.secret.DynamicSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * ONS消息消费器
 *
 * @author frankcl
 * @date 2022-08-03 19:09:57
 */
public class ONSConsumer implements Rebuildable {

    private final static Logger logger = LoggerFactory.getLogger(ONSConsumer.class);

    private ONSConsumerConfig config;
    private MessageListener listener;
    private List<RebuildListener> rebuildListeners;
    private Consumer consumer;

    public ONSConsumer(ONSConsumerConfig config,
                       MessageListener listener) {
        this.config = config;
        this.listener = listener;
        this.rebuildListeners = new ArrayList<>();
    }

    /**
     * 构建消息接收器实例
     *
     * @return 构建成功返回true，否则返回false
     */
    private boolean build() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESRV_ADDR, config.serverURL);
        properties.put(PropertyKeyConst.GROUP_ID, config.consumeId);
        properties.put(PropertyKeyConst.AccessKey, config.aliyunSecret.accessKey);
        properties.put(PropertyKeyConst.SecretKey, config.aliyunSecret.secretKey);
        properties.put(PropertyKeyConst.ConsumeThreadNums, config.consumeThreadNum);
        properties.put(PropertyKeyConst.MaxCachedMessageAmount, config.maxCachedMessageNum);
        properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        try {
            consumer = ONSFactory.createConsumer(properties);
            for (Subscribe subscribe : config.subscribes) {
                consumer.subscribe(subscribe.topic, subscribe.tags, listener);
            }
            consumer.start();
            logger.info("build ONS consumer success");
            return true;
        } catch (Exception e) {
            logger.error("build ONS consumer failed");
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void rebuild() {
        logger.info("ONS consumer is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("secret is not changed, ignore ONS consumer rebuilding");
            return;
        }
        config.aliyunSecret.accessKey = DynamicSecret.accessKey;
        config.aliyunSecret.secretKey = DynamicSecret.secretKey;
        Consumer prevConsumer = consumer;
        if (prevConsumer != null) prevConsumer.shutdown();
        for (RebuildListener rebuildListener : rebuildListeners) {
            rebuildListener.notifyRebuildEvent(this);
        }
        if (!build()) throw new RuntimeException("rebuild ONS consumer failed");
        logger.info("ONS consumer rebuild success");
    }

    /**
     * 启动消息消费器
     *
     * @return 启动成功返回true，否则返回false
     */
    public boolean start() {
        logger.info("ONS consumer is starting ...");
        if (config == null || !config.check()) return false;
        if (listener == null) {
            logger.error("message listener is null");
            return false;
        }
        if (!build()) return false;
        RebuildManager.register(this);
        logger.info("ONS consumer has been started");
        return true;
    }

    /**
     * 停止消息消费器
     */
    public void stop() {
        logger.info("ONS consumer is stopping ...");
        RebuildManager.unregister(this);
        if (consumer != null) consumer.shutdown();
        logger.info("ONS consumer has been stopped");
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
}
