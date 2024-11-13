package xin.manong.weapon.spring.boot.bean.wrap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import xin.manong.weapon.aliyun.mns.MNSClient;
import xin.manong.weapon.aliyun.mns.MNSQueueConsumer;
import xin.manong.weapon.aliyun.mns.MNSQueueConsumerConfig;
import xin.manong.weapon.aliyun.mns.MessageProcessor;

/**
 * MNS队列Consumer封装
 * 在属性设置完成之后保证mnsClient和messageProcessor注入
 *
 * @author frankcl
 * @date 2024-01-12 21:44:34
 */
public class MNSQueueConsumerBean extends MNSQueueConsumer implements InitializingBean, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(MNSQueueConsumerBean.class);

    private ApplicationContext applicationContext;

    public MNSQueueConsumerBean(MNSQueueConsumerConfig config) {
        super(config);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setMnsClient();
        setMessageProcessor();
    }

    /**
     * 设置MNS客户端
     *
     * @throws Exception 异常
     */
    private void setMnsClient() throws Exception {
        if (StringUtils.isEmpty(config.clientName)) {
            logger.error("MNS client is not found for name[{}]", config.clientName);
            throw new Exception(String.format("MNS client is not found for name[%s]", config.clientName));
        }
        Object bean = applicationContext.getBean(config.clientName);
        if (!(bean instanceof MNSClient)) {
            logger.error("unexpected bean[{}], not MNSClient", bean.getClass().getName());
            throw new Exception(String.format("unexpected bean[%s]", bean.getClass().getName()));
        }
        mnsClient = (MNSClient) bean;
    }

    /**
     * 设置消息处理器
     *
     * @throws Exception 异常
     */
    private void setMessageProcessor() throws Exception {
        if (StringUtils.isEmpty(config.processorName)) {
            logger.error("message processor is not found for name[{}]", config.processorName);
            throw new Exception(String.format("message processor is not found for name[%s]", config.processorName));
        }
        Object bean = applicationContext.getBean(config.processorName);
        if (!(bean instanceof MessageProcessor)) {
            logger.error("unexpected bean[{}], not MessageProcessor", bean.getClass().getName());
            throw new Exception(String.format("unexpected bean[%s]", bean.getClass().getName()));
        }
        processor = (MessageProcessor) bean;
    }
}
