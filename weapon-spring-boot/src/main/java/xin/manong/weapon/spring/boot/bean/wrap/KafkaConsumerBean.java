package xin.manong.weapon.spring.boot.bean.wrap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import xin.manong.weapon.base.kafka.KafkaConsumeConfig;
import xin.manong.weapon.base.kafka.KafkaConsumeGroup;
import xin.manong.weapon.base.kafka.KafkaRecordProcessor;

/**
 * kafka consumer封装
 * 在属性设置完成之后保证KafkaRecordProcessor注入
 *
 * @author frankcl
 * @date 2025-03-04 21:44:34
 */
public class KafkaConsumerBean extends KafkaConsumeGroup implements InitializingBean, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(KafkaConsumerBean.class);

    private ApplicationContext applicationContext;

    public KafkaConsumerBean(KafkaConsumeConfig config) {
        super(config);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(config.processorName)) {
            logger.warn("Record processor:{} is not found", config.processorName);
        }
        Object bean = applicationContext.getBean(config.processorName);
        if (!(bean instanceof KafkaRecordProcessor)) {
            logger.error("Unexpected bean:{}, not KafkaRecordProcessor", bean.getClass().getName());
            throw new Exception(String.format("Unexpected bean:%s", bean.getClass().getName()));
        }
        setProcessor((KafkaRecordProcessor) bean);
    }
}
