package xin.manong.weapon.base.etcd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.watch.WatchResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Etcd客户端封装
 *
 * @author frankcl
 * @date 2024-11-11 21:19:21
 */
public class EtcdClient {

    private static final Logger logger = LoggerFactory.getLogger(EtcdClient.class);

    private final Client client;
    private final Map<String, List<EtcdWatcher>> watcherMap;
    private final WatchErrorConsumer watchErrorConsumer;

    public EtcdClient(EtcdConfig config) {
        if (config == null || !config.check()) throw new IllegalArgumentException("etcd config is invalid");
        ClientBuilder builder = Client.builder().endpoints(config.endpoints.toArray(new String[0])).
                user(ByteSequence.from(config.username, StandardCharsets.UTF_8)).
                password(ByteSequence.from(config.password, StandardCharsets.UTF_8)).
                connectTimeout(Duration.ofMillis(config.connectTimeoutMs));
        client = builder.build();
        watcherMap = new HashMap<>();
        watchErrorConsumer = new WatchErrorConsumer();
    }

    /**
     * 关闭Etcd客户端
     */
    public void close() {
        if (client != null) client.close();
    }

    /**
     * 获取指定key字符串内容
     *
     * @param key 指定key
     * @return 成功返回内容字符串，否则返回null
     */
    public String get(String key) {
        try {
            if (StringUtils.isEmpty(key)) {
                logger.error("getting key is empty");
                return null;
            }
            GetResponse response = client.getKVClient().get(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            return response.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("get key[{}] failed", key);
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 添加key及对应内容value
     *
     * @param key 关键key
     * @param value 内容
     * @return 成功返回true，否则返回false
     */
    public boolean put(String key, String value) {
        try {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
                logger.error("putting key or value is empty");
                return false;
            }
            client.getKVClient().put(ByteSequence.from(key, StandardCharsets.UTF_8),
                    ByteSequence.from(value, StandardCharsets.UTF_8)).get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("put key[{}] and value[{}] failed", key, value);
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean delete(String key) {
        try {
            if (StringUtils.isEmpty(key)) {
                logger.error("deleting key is empty");
                return false;
            }
            client.getKVClient().delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("delete key[{}] failed", key);
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取指定key内容
     *
     * @param key 指定key
     * @param recordType 内容数据类型
     * @return 成功返回内容对象，否则返回null
     * @param <T> 数据类型
     */
    public <T> T get(String key, Class<T> recordType) {
        assert recordType != null;
        return transform(get(key), recordType);
    }

    /**
     * 获取指定key列表内容
     *
     * @param key 指定key
     * @param recordType 列表项数据类型
     * @return 成功返回内容对象，否则返回null
     * @param <T> 列表项数据类型
     */
    public <T> List<T> getList(String key, Class<T> recordType) {
        return transformList(get(key), recordType);
    }

    /**
     * 获取指定key字典内容
     *
     * @param key 指定key
     * @param keyType 字典key类型
     * @param valueType 字段value类型
     * @return 成功返回内容对象，否则返回null
     * @param <K> 字典key类型
     * @param <V> 字段value类型
     */
    public <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType) {
        return transformMap(get(key), keyType, valueType);
    }

    /**
     * 添加监听
     *
     * @param key 监听key
     * @param onNext 监听回调处理
     */
    public void addWatch(String key, Consumer<WatchResponse> onNext) {
        Watch.Watcher watcher = client.getWatchClient().watch(
                ByteSequence.from(key, StandardCharsets.UTF_8),
                onNext, watchErrorConsumer);
        if (!watcherMap.containsKey(key)) watcherMap.put(key, new ArrayList<>());
        List<EtcdWatcher> watchers = watcherMap.get(key);
        watchers.add(new EtcdWatcher(onNext, watcher));
    }

    /**
     * 移除监听
     *
     * @param key 监听key
     */
    public void removeWatch(String key) {
        List<EtcdWatcher> watchers = watcherMap.remove(key);
        if (watchers == null) return;
        watchers.forEach(watcher -> watcher.getWatcher().close());
    }

    /**
     * 移除监听
     *
     * @param key 监听key
     * @param onNext 监听回调
     */
    public int removeWatch(String key, Consumer<WatchResponse> onNext) {
        int removeCount = 0;
        List<EtcdWatcher> watchers = watcherMap.get(key);
        if (watchers == null) return removeCount;
        Iterator<EtcdWatcher> iterator = watchers.iterator();
        while (iterator.hasNext()) {
            EtcdWatcher watcher = iterator.next();
            if (!watcher.getOnNext().equals(onNext)) continue;
            watcher.getWatcher().close();
            iterator.remove();
            removeCount++;
        }
        return removeCount;
    }

    /**
     * 转换数据
     *
     * @param v 字符串数据
     * @param recordType 数据类型
     * @return 转换结果
     * @param <T> 数据类型
     */
    public static <T> T transform(String v, Class<T> recordType) {
        if (recordType == String.class) return recordType.cast(v);
        if (recordType == Integer.class) return recordType.cast(Integer.valueOf(v));
        if (recordType == Long.class) return recordType.cast(Long.valueOf(v));
        if (recordType == Short.class) return recordType.cast(Short.valueOf(v));
        if (recordType == Float.class) return recordType.cast(Float.valueOf(v));
        if (recordType == Double.class) return recordType.cast(Double.valueOf(v));
        if (recordType == Boolean.class) return recordType.cast(Boolean.valueOf(v));
        if (recordType == Byte.class) return recordType.cast(Byte.valueOf(v));
        return JSON.parseObject(v, recordType);
    }

    /**
     * 转换字典数据
     *
     * @param v 字符串数据
     * @param keyType key类型
     * @param valueType value类型
     * @return 字典数据
     * @param <K> key类型
     * @param <V> value类型
     */
    public static <K, V> Map<K, V> transformMap(String v, Class<K> keyType, Class<V> valueType) {
        assert keyType != null && valueType != null;
        return JSON.parseObject(v, new TypeReference<>(keyType, valueType) {});
    }

    /**
     * 转换列表数据
     *
     * @param v 字符串数据
     * @param recordType 数据类型
     * @return 列表数据
     * @param <T> 数据类型
     */
    public static <T> List<T> transformList(String v, Class<T> recordType) {
        assert recordType != null;
        return JSON.parseObject(v, new TypeReference<>(recordType) {});
    }

    private static class WatchErrorConsumer implements Consumer<Throwable> {

        @Override
        public void accept(Throwable throwable) {
            logger.error("error occurred when watching: {}", throwable.getMessage());
            logger.error(throwable.getMessage(), throwable);
        }
    }
}
