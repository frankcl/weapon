package xin.manong.weapon.spring.boot.etcd;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import xin.manong.weapon.base.etcd.EtcdClient;
import xin.manong.weapon.base.util.ReflectUtil;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 清理WatchValue回调处理
 *
 * @author frankcl
 * @date 2024-11-12 16:04:16
 */
public abstract class WatchValueDisposableBean implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(WatchValueDisposableBean.class);

    @Resource
    private EtcdClient etcdClient;

    @Override
    public void destroy() throws Exception {
        List<Field> watchedFields = ReflectUtil.getAnnotatedFields(getClass(), WatchValue.class);
        if (watchedFields.isEmpty()) return;
        for (Field watchedField : watchedFields) {
            WatchValue watchValue = watchedField.getAnnotation(WatchValue.class);
            if (watchValue == null || StringUtils.isEmpty(watchValue.key())) continue;
            String compositeKey = WatchValueInjector.integrateKey(watchValue);
            int n = etcdClient.removeWatch(compositeKey, new WatchValueConsumer(this, watchedField));
            logger.info("Remove watcher num:{} for field:{} of bean:{}",
                    n, watchedField.getName(), this.getClass().getName());
        }
    }
}
