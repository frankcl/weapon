package com.manong.weapon.aliyun.ons;

import com.manong.weapon.aliyun.secret.AliyunSecret;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ONS消息消费者配置
 *
 * @author frankcl
 * @create 2019-05-29 18:52
 */
public class ONSConsumerConfig {

    private final static Logger logger = LoggerFactory.getLogger(ONSConsumerConfig.class);

    private final static int DEFAULT_CONSUME_THREAD_NUM = 1;
    private final static int DEFAULT_MAX_CACHED_MESSAGE_NUM = 1000;

    public int consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
    public int maxCachedMessageNum = DEFAULT_MAX_CACHED_MESSAGE_NUM;
    public String consumeId;
    public String serverURL;
    public List<Subscribe> subscribes = new ArrayList<>();
    public AliyunSecret aliyunSecret;

    /**
     * 添加订阅信息
     *
     * @param subscribe 订阅信息
     */
    public void addSubscribe(Subscribe subscribe) {
        if (subscribe == null || !subscribe.check()) return;
        if (subscribes == null) subscribes = new ArrayList<>();
        for (Subscribe s : subscribes) {
            if (s.topic != null && s.topic.equals(subscribe.topic)) {
                logger.warn("topic[{}] has been subscribed", subscribe.topic);
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
        if (StringUtils.isEmpty(serverURL)) {
            logger.error("server url is empty");
            return false;
        }
        if (StringUtils.isEmpty(consumeId)) {
            logger.error("consume id is empty");
            return false;
        }
        if (aliyunSecret == null || !aliyunSecret.check()) {
            logger.error("aliyun secret is invalid");
            return false;
        }
        if (subscribes == null || subscribes.isEmpty()) {
            logger.error("missing subscribe list");
            return false;
        }
        for (Subscribe subscribe : subscribes) if (!subscribe.check()) return false;
        if (consumeThreadNum <= 0) consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
        if (maxCachedMessageNum <= 0) maxCachedMessageNum = DEFAULT_MAX_CACHED_MESSAGE_NUM;
        return true;
    }
}
