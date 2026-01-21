package xin.manong.weapon.spring.boot.bean.wrap;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import xin.manong.weapon.base.rocketmq.RocketMQConsumer;
import xin.manong.weapon.base.rocketmq.RocketMQConsumerConfig;

/**
 * RocketMQ Consumer封装
 * 在属性设置完成之后保证messageListener注入
 *
 * @author frankcl
 * @date 2025-10-31 21:44:34
 */
public class RocketMQConsumerBean extends RocketMQConsumer implements InitializingBean, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(RocketMQConsumerBean.class);

    private ApplicationContext applicationContext;

    public RocketMQConsumerBean(RocketMQConsumerConfig config) {
        super(config);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(config.listener)) {
            logger.warn("Message listener is not config");
            throw new IllegalStateException("Message listener is not config");
        }
        Object bean = applicationContext.getBean(config.listener);
        if (!(bean instanceof MessageListener)) {
            logger.error("Unexpected bean:{}, not MessageListener", bean.getClass().getName());
            throw new IllegalStateException(String.format("Unexpected bean:%s", bean.getClass().getName()));
        }
        config.messageListener = (MessageListener) bean;
    }
}
