package xin.manong.weapon.spring.boot.env;

import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.rebuild.RebuildManager;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * 动态秘钥更新
 *
 * @author frankcl
 * @date 2024-11-23 10:48:06
 */
public class DynamicSecretConsumer implements Consumer<WatchResponse> {

    private static final Logger logger = LoggerFactory.getLogger(DynamicSecretConsumer.class);

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
                        logger.info("aliyun key changed for etcd key[{}]: {}", key, value);
                        DynamicSecretInjector.inject(value);
                        RebuildManager.rebuild();
                        break;
                    case DELETE:
                        logger.warn("aliyun key[{}] is deleted", key);
                        break;
                    default:
                        logger.warn("unknown event type[{}] for etcd key[{}]", eventType.name(), key);
                        break;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
