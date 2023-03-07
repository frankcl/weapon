package xin.manong.weapon.base.redis;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * redis客户端
 *
 * @author frankcl
 * @date 2022-12-20 10:24:05
 */
public class RedisClient {

    private final static Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private final static Long DEFAULT_LOCK_EXPIRED_SECONDS = 30L;

    private RedissonClient redissonClient;

    private RedisClient() {
    }

    private RedisClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 关闭
     */
    public void close() {
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
        return new RedisClient(Redisson.create(redissonConfig));
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
        if (config.connectionPoolSize != null && config.connectionPoolSize > 0) {
            serverConfig.setMasterConnectionPoolSize(config.connectionPoolSize);
            serverConfig.setSlaveConnectionPoolSize(config.connectionPoolSize);
        }
        if (!StringUtils.isEmpty(config.password)) serverConfig.setPassword(config.password);
        return new RedisClient(Redisson.create(redissonConfig));
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
        if (config.connectionPoolSize != null && config.connectionPoolSize > 0) {
            serverConfig.setMasterConnectionPoolSize(config.connectionPoolSize);
            serverConfig.setSlaveConnectionPoolSize(config.connectionPoolSize);
        }
        if (!StringUtils.isEmpty(config.password)) serverConfig.setPassword(config.password);
        return new RedisClient(Redisson.create(redissonConfig));
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
        if (config.connectionPoolSize != null && config.connectionPoolSize > 0) {
            serverConfig.setMasterConnectionPoolSize(config.connectionPoolSize);
            serverConfig.setSlaveConnectionPoolSize(config.connectionPoolSize);
        }
        if (!StringUtils.isEmpty(config.password)) serverConfig.setPassword(config.password);
        return new RedisClient(Redisson.create(redissonConfig));
    }

    /**
     * 加锁
     *
     * @param key 锁key
     * @param expiredSeconds 过期时间（单位：秒），如果小于等于0，默认30秒
     * @return 加锁成功返回true，否则返回false
     */
    public boolean tryLock(String key, Long expiredSeconds) {
        if (StringUtils.isEmpty(key)) throw new RuntimeException("lock key is not allowed to be empty");
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(0, expiredSeconds == null || expiredSeconds <= 0 ?
                    DEFAULT_LOCK_EXPIRED_SECONDS : expiredSeconds, TimeUnit.SECONDS);
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
     * @param expiredSeconds 过期时间（单位：秒），如果小于等于0，默认30秒
     * @return 加锁成功返回true，否则返回false
     */
    public boolean tryReadLock(String key, Long expiredSeconds) {
        if (StringUtils.isEmpty(key)) throw new RuntimeException("lock key is not allowed to be empty");
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        try {
            return readWriteLock.readLock().tryLock(0, expiredSeconds == null || expiredSeconds <= 0 ?
                    DEFAULT_LOCK_EXPIRED_SECONDS : expiredSeconds, TimeUnit.SECONDS);
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
     * @param expiredSeconds 过期时间（单位：秒），如果小于等于0，默认30秒
     * @return 加锁成功返回true，否则返回false
     */
    public boolean tryWriteLock(String key, Long expiredSeconds) {
        if (StringUtils.isEmpty(key)) throw new RuntimeException("lock key is not allowed to be empty");
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        try {
            return readWriteLock.writeLock().tryLock(0, expiredSeconds == null || expiredSeconds <= 0 ?
                    DEFAULT_LOCK_EXPIRED_SECONDS : expiredSeconds, TimeUnit.SECONDS);
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
     * 获取RedissonClient
     *
     * @return RedissonClient实例
     */
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
