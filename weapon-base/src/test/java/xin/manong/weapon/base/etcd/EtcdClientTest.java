package xin.manong.weapon.base.etcd;

import com.alibaba.fastjson.JSON;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.watch.WatchEvent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frankcl
 * @date 2024-11-11 22:12:58
 */
public class EtcdClientTest {

    private static final Logger logger = LoggerFactory.getLogger(EtcdClientTest.class);
    private static EtcdClient client;

    @BeforeClass
    public static void setUp() {
        EtcdConfig config = new EtcdConfig();
        config.endpoints = new ArrayList<>();
        config.endpoints.add("http://127.0.0.1:2379");
        config.username = "root";
        config.password = "";
        client = new EtcdClient(config);
    }

    @AfterClass
    public static void tearDown() {
        client.close();
    }

    @Test
    public void testLock() throws InterruptedException {
        LockRequest request = new LockRequest("test/lock", 30L);
        new Thread(() -> {
            LockApproval approval = client.applyLock(request);
            logger.info(approval.getPath());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            client.releaseLock(approval);
        }).start();
        new Thread(() -> {
            LockApproval approval = client.applyLock(request);
            logger.info(approval.getPath());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            client.releaseLock(approval);
        }).start();
        Thread.sleep(15000);
    }

    @Test
    public void testGetKeysWithPrefix() {
        Assert.assertTrue(client.put("prefix/k1", "1"));
        Assert.assertTrue(client.put("prefix/k3", "1"));
        Assert.assertTrue(client.put("prefix/k2", "1"));
        List<String> keys = client.getKeysWithPrefix("prefix");
        Assert.assertEquals(3, keys.size());
        Assert.assertEquals("prefix/k1", keys.get(0));
        Assert.assertEquals("prefix/k2", keys.get(1));
        Assert.assertEquals("prefix/k3", keys.get(2));
        Assert.assertTrue(client.delete("prefix/k1"));
        Assert.assertTrue(client.delete("prefix/k2"));
        Assert.assertTrue(client.delete("prefix/k3"));
    }

    @Test
    public void testOperations() {
        Assert.assertTrue(client.put("k1", "1"));
        Assert.assertEquals("1", client.get("k1"));
        Assert.assertEquals(1, client.get("k1", Integer.class).intValue());
        Assert.assertTrue(client.delete("k1"));
    }

    @Test
    public void testList() {
        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        Assert.assertTrue(client.put("k1", JSON.toJSONString(list)));
        list = client.getList("k1", String.class);
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("123", list.get(0));
        Assert.assertEquals("456", list.get(1));
        Assert.assertTrue(client.delete("k1"));
    }

    @Test
    public void testMap() {
        Map<String, Long> map = new HashMap<>();
        map.put("k1", 1L);
        map.put("k2", 2L);
        Assert.assertTrue(client.put("k1", JSON.toJSONString(map)));
        map = client.getMap("k1", String.class, Long.class);
        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(1L, map.get("k1").longValue());
        Assert.assertEquals(2L, map.get("k2").longValue());
        Assert.assertTrue(client.delete("k1"));
    }

    @Test
    public void testWatch() {
        client.addWatch("k", watchResponse -> {
            List<WatchEvent> watchEvents = watchResponse.getEvents();
            for (WatchEvent watchEvent : watchEvents) {
                WatchEvent.EventType eventType = watchEvent.getEventType();
                KeyValue keyValue = watchEvent.getKeyValue();
                logger.info("event type[{}] for key[{}] and value[{}]",
                        eventType, keyValue.getKey(), keyValue.getValue());
            }
        });
        client.put("k", "123");
        client.put("k", "abc");
        client.delete("k");
        client.removeWatch("k");
    }
}
