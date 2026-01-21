package xin.manong.weapon.spring.boot.etcd;

import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * etcd数据变换回调处理
 *
 * @author frankcl
 * @date 2024-11-12 15:10:39
 */
public class WatchValueConsumer implements Consumer<WatchResponse> {

    private static final Logger logger = LoggerFactory.getLogger(WatchValueConsumer.class);

    private final Object bean;
    private final Field field;

    public WatchValueConsumer(Object bean, Field field) {
        this.bean = bean;
        this.field = field;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WatchValueConsumer other)) return false;
        return bean == other.bean && field.equals(other.field);
    }

    @Override
    public void accept(WatchResponse watchResponse) {
        List<WatchEvent> watchEvents = watchResponse.getEvents();
        for (WatchEvent watchEvent : watchEvents) {
            try {
                KeyValue keyValue = watchEvent.getKeyValue();
                String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                WatchEvent.EventType eventType = watchEvent.getEventType();
                switch (eventType) {
                    case PUT:
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        logger.info("Value changed for etcd key:{}, value is {}", key, value);
                        WatchValueInjector.inject(bean, field, value);
                        break;
                    case DELETE:
                        logger.warn("Etcd key:{} is deleted", key);
                        break;
                    default:
                        logger.warn("Unknown event type:{} for etcd key:{}", eventType.name(), key);
                        break;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
