package xin.manong.weapon.spring.boot.etcd;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import xin.manong.weapon.base.etcd.EtcdClient;
import xin.manong.weapon.base.util.ReflectUtil;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

/**
 * WatchValue Bean后处理器
 *
 * @author frankcl
 * @date 2024-11-12 13:56:06
 */
@Component
public class WatchValuePostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WatchValuePostProcessor.class);

    @Resource
    private EtcdClient etcdClient;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        List<Field> watchedFields = ReflectUtil.getAnnotatedFields(bean.getClass(), WatchValue.class);
        if (watchedFields.isEmpty()) return bean;
        for (Field watchedField : watchedFields) {
            WatchValue watchValue = watchedField.getAnnotation(WatchValue.class);
            if (watchValue == null) continue;
            if (StringUtils.isEmpty(watchValue.key())) {
                logger.warn("WatchValue key is empty, ignore it");
                continue;
            }
            String key = WatchValueInjector.integrateKey(watchValue);
            String value = etcdClient.get(key);
            if (value == null) {
                logger.warn("key[{}] is not found from etcd", key);
                continue;
            }
            WatchValueInjector.inject(bean, watchedField, value);
            etcdClient.addWatch(key, new WatchValueConsumer(bean, watchedField));
            logger.info("inject value success for field[{}] of bean[{}]", watchedField.getName(), beanName);
        }
        return bean;
    }
}
