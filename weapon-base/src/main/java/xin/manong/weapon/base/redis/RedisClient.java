package xin.manong.weapon.base.redis;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.client.RedisConnection;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.config.*;
import org.redisson.connection.ConnectionManager;
import org.redisson.misc.RedisURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis客户端
 *
 * @author frankcl
 * @date 2022-12-20 10:24:05
 */
public class RedisClient {

    /**
     * redis客户端及连接缓存
     */
    static class CachedClientConnection {
        private final RedisMode redisMode;
        private RedisConnection connection;
        private final RedissonClient redissonClient;
        private org.redisson.client.RedisClient redisClient;

        public CachedClientConnection(RedissonClient redissonClient, RedisMode redisMode) {
            this.redisMode = redisMode;
            this.redissonClient = redissonClient;
        }

        public void destroy() {
            if (connection != null && !connection.isClosed()) connection.closeAsync();
            if (redisClient != null && !redisClient.isShutdown()) redisClient.shutdown();
        }

        /**
         * 获取redis连接
         *
         * @return redis连接
         */
        public RedisConnection getConnection() {
            if (connection == null || connection.isClosed() || !connection.isActive() || !connection.isOpen()) {
                if (connection != null && !connection.isClosed()) {
                    connection.closeAsync();
                    connection = null;
                }
                org.redisson.client.RedisClient client = getRedisClient();
                if (client != null) connection = client.connect();
            }
            return connection;
        }

        /**
         * 获取redis客户端
         *
         * @return redis客户端
         */
        private org.redisson.client.RedisClient getRedisClient() {
            if (redisClient != null && !redisClient.isShutdown()) return redisClient;
            redisClient = null;
            Redisson redisson = (Redisson) redissonClient;
            ConnectionManager connectionManager = redisson.getConnectionManager();
            RedisURI redisURI = null;
            if (redisMode == RedisMode.SINGLE) {
                redisURI = new RedisURI(redisson.getConfig().useSingleServer().getAddress());
            } else if (redisMode == RedisMode.MASTER_SLAVE) {
                redisURI = new RedisURI(redisson.getConfig().useMasterSlaveServers().getMasterAddress());
            }
            if (redisURI == null) return null;
            redisClient = connectionManager.createClient(NodeType.MASTER, redisURI, null);
            return redisClient;
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private final static Long DEFAULT_LOCK_EXPIRED_SECONDS = 30L;

    @Getter
    private RedissonClient redissonClient;
    private CachedClientConnection cachedClientConnection;

    private RedisClient() {
    }

    private RedisClient(RedissonClient redissonClient, RedisMode redisMode) {
        this.redissonClient = redissonClient;
        this.cachedClientConnection = new CachedClientConnection(redissonClient, redisMode);
    }

    /**
     * 关闭
     */
    public void close() {
        if (cachedClientConnection != null) cachedClientConnection.destroy();
        if (redissonClient != null) redissonClient.shutdown();
    }

    /**
     * 构建单点模式redis客户端
     *
     * @param config 配置信息
     * @return Redis客户端
     */
    public static RedisClient buildRedisClient(RedisSingleConfig config) {
        if (config == null || !config.check()) throw new RuntimeException("config is invalid for single server mode");
        Config redissonConfig = new Config();
        if (config.codec != null) redissonConfig.setCodec(config.codec);
        SingleServerConfig serverConfig = redissonConfig.useSingleServer();
        if (config.timeout != null && config.timeout > 0) serverConfig.setTimeout(config.timeout);
        serverConfig.setAddress(config.address);
        serverConfig.setDatabase(config.db);
        if (config.connectionPoolSize != null && config.connectionPoolSize > 0) {
            serverConfig.setConnectionPoolSize(config.connectionPoolSize);
        }
        if (!StringUtils.isEmpty(config.password)) serverConfig.setPassword(config.password);
        return new RedisClient(Redisson.create(redissonConfig), RedisMode.SINGLE);
    }

    /**
     * 构建集群模式redis客户端
     *
     * @param config 配置信息
     * @return redis客户端
     */
    public static RedisClient buildRedisClient(RedisClusterConfig config) {
        if (config == null || !config.check()) throw new RuntimeException("config is invalid for clustering mode");
        Config redissonConfig = new Config();
        if (config.codec != null) redissonConfig.setCodec(config.codec);
        ClusterServersConfig serverConfig = redissonConfig.useClusterServers();
        if (config.timeout != null && config.timeout > 0) serverConfig.setTimeout(config.timeout);
        serverConfig.setNodeAddresses(config.nodeAddresses);
        setBaseConfig(config, serverConfig);
        return new RedisClient(Redisson.create(redissonConfig), RedisMode.CLUSTER);
    }

    /**
     * 构建主从模式redis客户端
     *
     * @param config 配置信息
     * @return redis客户端
     */
    public static RedisClient buildRedisClient(RedisMasterSlaveConfig config) {
        if (config == null || !config.check()) throw new RuntimeException("config is invalid for master/slave mode");
        Config redissonConfig = new Config();
        if (config.codec != null) redissonConfig.setCodec(config.codec);
        MasterSlaveServersConfig serverConfig = redissonConfig.useMasterSlaveServers();
        if (config.timeout != null && config.timeout > 0) serverConfig.setTimeout(config.timeout);
        serverConfig.setMasterAddress(config.masterAddress);
        serverConfig.setSlaveAddresses(new HashSet<>(config.slaveAddresses));
        serverConfig.setDatabase(config.db);
        setBaseConfig(config, serverConfig);
        return new RedisClient(Redisson.create(redissonConfig), RedisMode.MASTER_SLAVE);
    }

    /**
     * 构建哨兵模式RedisClient
     *
     * @param config 配置信息
     * @return redis客户端
     */
    public static RedisClient buildRedisClient(RedisSentinelConfig config) {
        if (config == null || !config.check()) throw new RuntimeException("config is invalid for sentinel mode");
        Config redissonConfig = new Config();
        if (config.codec != null) redissonConfig.setCodec(config.codec);
        SentinelServersConfig serverConfig = redissonConfig.useSentinelServers();
        if (config.timeout != null && config.timeout > 0) serverConfig.setTimeout(config.timeout);
        serverConfig.setMasterName(config.masterName);
        serverConfig.setSentinelAddresses(config.sentinelAddresses);
        serverConfig.setDatabase(config.db);
        setBaseConfig(config, serverConfig);
        return new RedisClient(Redisson.create(redissonConfig), RedisMode.SENTINEL);
    }

    /**
     * 进行基础配置
     *
     * @param config 配置信息
     * @param baseConfig MasterSlave基础配置
     */
    private static void setBaseConfig(RedisConfig config, BaseMasterSlaveServersConfig<?> baseConfig) {
        if (config.connectionPoolSize != null && config.connectionPoolSize > 0) {
            baseConfig.setMasterConnectionPoolSize(config.connectionPoolSize);
            baseConfig.setSlaveConnectionPoolSize(config.connectionPoolSize);
        }
        if (!StringUtils.isEmpty(config.password)) baseConfig.setPassword(config.password);
    }

    /**
     * 加锁
     *
     * @param key 锁key
     * @param expiredSeconds 过期时间（单位：秒），如果为null表示不过期；如果小于等于0，默认30秒
     * @return 加锁成功返回true，否则返回false
     */
    public boolean tryLock(String key, Long expiredSeconds) {
        if (StringUtils.isEmpty(key)) throw new RuntimeException("lock key is not allowed to be empty");
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(0, expiredSeconds == null ? -1 : (expiredSeconds <= 0 ?
                    DEFAULT_LOCK_EXPIRED_SECONDS : expiredSeconds), TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 解锁
     *
     * @param key 锁key
     */
    public void unlock(String key) {
        if (StringUtils.isEmpty(key)) throw new RuntimeException("lock key is not allowed to be empty");
        RLock lock = redissonClient.getLock(key);
        lock.unlock();
    }

    /**
     * 加读锁
     *
     * @param key 锁key
     * @param expiredSeconds 过期时间（单位：秒），如果为null表示不过期；如果小于等于0，默认30秒
     * @return 加锁成功返回true，否则返回false
     */
    public boolean tryReadLock(String key, Long expiredSeconds) {
        if (StringUtils.isEmpty(key)) throw new RuntimeException("lock key is not allowed to be empty");
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        try {
            return readWriteLock.readLock().tryLock(0, expiredSeconds == null ? -1 : (expiredSeconds <= 0 ?
                    DEFAULT_LOCK_EXPIRED_SECONDS : expiredSeconds), TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 解开读锁
     *
     * @param key 锁key
     */
    public void unLockRead(String key) {
        if (StringUtils.isEmpty(key)) throw new RuntimeException("lock key is not allowed to be empty");
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        readWriteLock.readLock().unlock();
    }

    /**
     * 加写锁
     *
     * @param key 锁key
     * @param expiredSeconds 过期时间（单位：秒），如果为null表示不过期；如果小于等于0，默认30秒
     * @return 加锁成功返回true，否则返回false
     */
    public boolean tryWriteLock(String key, Long expiredSeconds) {
        if (StringUtils.isEmpty(key)) throw new RuntimeException("lock key is not allowed to be empty");
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        try {
            return readWriteLock.writeLock().tryLock(0, expiredSeconds == null ? -1 : (expiredSeconds <= 0 ?
                    DEFAULT_LOCK_EXPIRED_SECONDS : expiredSeconds), TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 解开写锁
     *
     * @param key 锁key
     */
    public void unLockWrite(String key) {
        if (StringUtils.isEmpty(key)) throw new RuntimeException("lock key is not allowed to be empty");
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        readWriteLock.writeLock().unlock();
    }

    /**
     * 构建事务
     *
     * @return 事务
     */
    public RTransaction buildTransaction() {
        return redissonClient.createTransaction(TransactionOptions.defaults());
    }

    /**
     * 构建事务
     *
     * @param options 事务选项
     * @return 事务
     */
    public RTransaction buildTransaction(TransactionOptions options) {
        if (options == null) {
            logger.warn("transaction options are null, using default");
            return buildTransaction();
        }
        return redissonClient.createTransaction(options);
    }

    /**
     * 获取限速器
     *
     * @param key 限速器key
     * @return 限速器实例
     */
    public RRateLimiter getRateLimiter(String key) {
        if (StringUtils.isEmpty(key)) throw new RuntimeException("rate limiter key is not allowed to be empty");
        return redissonClient.getRateLimiter(key);
    }

    /**
     * 获取内存使用情况
     *
     * @return 内存使用情况，无法获取返回null
     */
    public RedisMemory getMemoryInfo() {
        RedisConnection connection = cachedClientConnection.getConnection();
        if (connection == null) return null;
        Map<String, String> memoryInfo = connection.sync(StringCodec.INSTANCE, RedisCommands.INFO_MEMORY);
        return JSON.toJavaObject(JSON.parseObject(JSON.toJSONString(memoryInfo)), RedisMemory.class);
    }
}
