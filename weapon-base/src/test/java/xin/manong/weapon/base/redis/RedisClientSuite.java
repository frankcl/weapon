package xin.manong.weapon.base.redis;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2022-12-20 17:02:39
 */
public class RedisClientSuite {

    private String nodeAddress = "r-bp172bf1e294b244.redis.rds.aliyuncs.com:6379";
    private String password = "";

    @Test
    public void testCommonLock() {
        RedisSingleConfig config = new RedisSingleConfig();
        config.address = nodeAddress;
        config.db = 2;
        config.password = password;
        RedisClient redisClient = RedisClient.buildRedisClient(config);
        try {
            Assert.assertTrue(redisClient.tryLock("test_aaa", 60L));
            Assert.assertTrue(redisClient.tryLock("test_aaa", 60L));
            {
                Thread thread = new Thread(() -> Assert.assertFalse(redisClient.tryLock("test_aaa", 60L)));
                thread.start();
                thread.join();
            }
            redisClient.unlock("test_aaa");
            redisClient.unlock("test_aaa");

            Assert.assertTrue(redisClient.tryReadLock("test_read_aaa", 60L));
            Assert.assertTrue(redisClient.tryReadLock("test_read_aaa", 60L));
            Assert.assertFalse(redisClient.tryWriteLock("test_read_aaa", 60L));
            {
                Thread thread = new Thread(() -> {
                    Assert.assertTrue(redisClient.tryReadLock("test_read_aaa", 60L));
                    redisClient.unLockRead("test_read_aaa");
                    Assert.assertFalse(redisClient.tryWriteLock("test_read_aaa", 60L));
                });
                thread.start();
                thread.join();
            }
            redisClient.unLockRead("test_read_aaa");
            redisClient.unLockRead("test_read_aaa");

            Assert.assertTrue(redisClient.tryWriteLock("test_write_aaa", 60L));
            Assert.assertTrue(redisClient.tryWriteLock("test_write_aaa", 60L));
            {
                Thread thread = new Thread(() -> Assert.assertFalse(redisClient.tryWriteLock("test_write_aaa", 60L)));
                thread.start();
                thread.join();
            }
            Assert.assertTrue(redisClient.tryReadLock("test_write_aaa", 60L));
            {
                Thread thread = new Thread(() -> Assert.assertFalse(redisClient.tryReadLock("test_write_aaa", 60L)));
                thread.start();
                thread.join();
            }
            redisClient.unLockRead("test_write_aaa");
            redisClient.unLockWrite("test_write_aaa");
            redisClient.unLockWrite("test_write_aaa");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisClient.close();
        }
    }
}
