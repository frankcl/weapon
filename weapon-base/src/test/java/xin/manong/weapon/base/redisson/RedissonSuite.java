package xin.manong.weapon.base.redisson;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2022-12-20 17:02:39
 */
public class RedissonSuite {

    private String serverURL = "r-bp172bf1e294b244.redis.rds.aliyuncs.com:6379";
    private String password = "";

    @Test
    public void testCommonLock() {
        RedissonSingleConfig config = new RedissonSingleConfig();
        config.address = serverURL;
        config.db = 2;
        config.password = password;
        Redisson redisson = Redisson.buildSingleRedisson(config);
        try {
            Assert.assertTrue(redisson.tryLock("test_aaa", 60L));
            Assert.assertTrue(redisson.tryLock("test_aaa", 60L));
            {
                Thread thread = new Thread(() -> Assert.assertFalse(redisson.tryLock("test_aaa", 60L)));
                thread.start();
                thread.join();
            }
            redisson.unlock("test_aaa");
            redisson.unlock("test_aaa");

            Assert.assertTrue(redisson.tryReadLock("test_read_aaa", 60L));
            Assert.assertTrue(redisson.tryReadLock("test_read_aaa", 60L));
            Assert.assertFalse(redisson.tryWriteLock("test_read_aaa", 60L));
            {
                Thread thread = new Thread(() -> {
                    Assert.assertTrue(redisson.tryReadLock("test_read_aaa", 60L));
                    redisson.unLockRead("test_read_aaa");
                    Assert.assertFalse(redisson.tryWriteLock("test_read_aaa", 60L));
                });
                thread.start();
                thread.join();
            }
            redisson.unLockRead("test_read_aaa");
            redisson.unLockRead("test_read_aaa");

            Assert.assertTrue(redisson.tryWriteLock("test_write_aaa", 60L));
            Assert.assertTrue(redisson.tryWriteLock("test_write_aaa", 60L));
            {
                Thread thread = new Thread(() -> Assert.assertFalse(redisson.tryWriteLock("test_write_aaa", 60L)));
                thread.start();
                thread.join();
            }
            Assert.assertTrue(redisson.tryReadLock("test_write_aaa", 60L));
            {
                Thread thread = new Thread(() -> Assert.assertFalse(redisson.tryReadLock("test_write_aaa", 60L)));
                thread.start();
                thread.join();
            }
            redisson.unLockRead("test_write_aaa");
            redisson.unLockWrite("test_write_aaa");
            redisson.unLockWrite("test_write_aaa");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisson.close();
        }
    }
}
