package xin.manong.weapon.spring.boot.bean;

import com.aliyun.openservices.ons.api.MessageListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import xin.manong.weapon.aliyun.ons.ONSConsumer;
import xin.manong.weapon.aliyun.ons.ONSConsumerConfig;
import xin.manong.weapon.aliyun.ons.Subscribe;

import java.util.List;

/**
 * @author frankcl
 * @date 2023-02-20 21:44:34
 */
public class ONSConsumerBean extends ONSConsumer implements InitializingBean, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(ONSConsumerBean.class);

    private ApplicationContext applicationContext;

    public ONSConsumerBean(ONSConsumerConfig config) {
        super(config);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Subscribe> subscribes = config.subscribes;
        for (Subscribe subscribe : subscribes) {
            if (StringUtils.isEmpty(subscribe.listenerName)) {
                logger.warn("message listener config is not found for subscribe[{}/{}]",
                        subscribe.topic, subscribe.tags);
                continue;
            }
            Object bean = applicationContext.getBean(subscribe.listenerName);
            if (bean == null || !(bean instanceof MessageListener)) {
                logger.error("unexpected bean[{}], not MessageListener",
                        bean == null ? "null" : bean.getClass().getName());
                throw new Exception(String.format("unexpected bean[%s]",
                        bean == null ? "null" : bean.getClass().getName()));
            }
            subscribe.listener = (MessageListener) bean;
        }
    }
}
