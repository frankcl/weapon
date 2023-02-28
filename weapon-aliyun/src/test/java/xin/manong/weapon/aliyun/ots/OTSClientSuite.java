package xin.manong.weapon.aliyun.ots;

import com.alibaba.fastjson2.JSON;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.Condition;
import com.alicloud.openservices.tablestore.model.RowExistenceExpectation;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.Query;
import com.alicloud.openservices.tablestore.model.search.query.TermQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.weapon.aliyun.secret.AliyunSecret;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.util.FileUtil;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @author frankcl
 * @date 2022-07-25 16:09:05
 */
public class OTSClientSuite {

    private String secretFile = this.getClass().getResource("/secret").getPath();
    private OTSClient otsClient;

    @Before
    public void setUp() {
        String content = FileUtil.read(secretFile, Charset.forName("UTF-8"));
        AliyunSecret aliyunSecret = JSON.parseObject(content, AliyunSecret.class);
        OTSClientConfig config = new OTSClientConfig();
        config.dynamic = false;
        config.aliyunSecret = aliyunSecret;
        config.endpoint = "https://ai-media.cn-hangzhou.ots.aliyuncs.com";
        config.instance = "ai-media";
        Assert.assertTrue(config.check());
        otsClient = new OTSClient(config);
    }

    @After
    public void tearDown() {
        otsClient.close();
        otsClient = null;
    }

    @Test
    public void testPutCheckConditionFail() {
        long currentTime = System.currentTimeMillis();
        String tableName = "news_general_data";
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("news_key", "test_key");
        Set<String> keys = keyMap.keySet();
        KVRecord kvRecord = new KVRecord();
        kvRecord.put("news_key", "test_key");
        kvRecord.put("title", "标题");
        kvRecord.put("publish_timestamp", currentTime);
        kvRecord.setKeys(keys);
        Condition condition = new Condition();
        condition.setRowExistenceExpectation(RowExistenceExpectation.EXPECT_EXIST);
        Assert.assertEquals(OTSStatus.CHECK_CONDITION_FAIL, otsClient.put(tableName, kvRecord, condition));
    }

    @Test
    public void testUpdateCheckConditionFail() {
        long currentTime = System.currentTimeMillis();
        String tableName = "news_general_data";
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("news_key", "test_key");
        Set<String> keys = keyMap.keySet();
        KVRecord kvRecord = new KVRecord();
        kvRecord.put("news_key", "test_key");
        kvRecord.put("title", "标题");
        kvRecord.put("publish_timestamp", currentTime);
        kvRecord.setKeys(keys);
        Condition condition = new Condition();
        condition.setRowExistenceExpectation(RowExistenceExpectation.EXPECT_EXIST);
        Assert.assertEquals(OTSStatus.CHECK_CONDITION_FAIL, otsClient.update(tableName, kvRecord, condition));
    }

    @Test
    public void testDeleteCheckConditionFail() {
        String tableName = "news_general_data";
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("news_key", "test_key");
        Condition condition = new Condition();
        condition.setRowExistenceExpectation(RowExistenceExpectation.EXPECT_EXIST);
        Assert.assertEquals(OTSStatus.CHECK_CONDITION_FAIL, otsClient.delete(tableName, keyMap, condition));
    }

    @Test
    public void testOTSOperations() {
        long currentTime = System.currentTimeMillis();
        String tableName = "news_general_data";
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("news_key", "test_key");
        Set<String> keys = keyMap.keySet();
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("news_key", "test_key");
            kvRecord.put("title", "标题");
            kvRecord.put("publish_timestamp", currentTime);
            kvRecord.setKeys(keys);
            Assert.assertEquals(OTSStatus.SUCCESS, otsClient.put(tableName, kvRecord, null));
        }
        {
            KVRecord kvRecord = otsClient.get(tableName, keyMap);
            Assert.assertEquals(3, kvRecord.getFieldCount());
            Assert.assertTrue(kvRecord.has("news_key"));
            Assert.assertEquals("test_key", kvRecord.get("news_key"));
            Assert.assertTrue(kvRecord.has("title"));
            Assert.assertEquals("标题", kvRecord.get("title"));
            Assert.assertTrue(kvRecord.has("publish_timestamp"));
            Assert.assertEquals(currentTime, ((Long) kvRecord.get("publish_timestamp")).longValue());

            Assert.assertEquals(1, kvRecord.getKeys().size());
            Assert.assertTrue(kvRecord.getKeys().contains("news_key"));
        }
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("news_key", "test_key");
            kvRecord.put("title", "标题1");
            kvRecord.setKeys(keys);
            Assert.assertEquals(OTSStatus.SUCCESS, otsClient.update(tableName, kvRecord, null));
        }
        {
            KVRecord kvRecord = otsClient.get(tableName, keyMap);
            Assert.assertEquals(3, kvRecord.getFieldCount());
            Assert.assertTrue(kvRecord.has("news_key"));
            Assert.assertEquals("test_key", kvRecord.get("news_key"));
            Assert.assertTrue(kvRecord.has("title"));
            Assert.assertEquals("标题1", kvRecord.get("title"));
            Assert.assertTrue(kvRecord.has("publish_timestamp"));
            Assert.assertEquals(currentTime, ((Long) kvRecord.get("publish_timestamp")).longValue());

            Assert.assertEquals(1, kvRecord.getKeys().size());
            Assert.assertTrue(kvRecord.getKeys().contains("news_key"));
        }
        {
            Assert.assertEquals(OTSStatus.SUCCESS, otsClient.delete(tableName, keyMap, null));
        }
        {
            KVRecord kvRecord = otsClient.get(tableName, keyMap);
            Assert.assertTrue(kvRecord == null);
        }
    }

    @Test
    public void testSearch() {
        String tableName = "live_stream_data";
        String indexName = "live_stream_data_search_index";
        List<String> returnColumns = new ArrayList<>();
        returnColumns.add("programme");
        returnColumns.add("television");
        returnColumns.add("title");
        returnColumns.add("publish_timestamp");
        BoolQuery boolQuery = new BoolQuery();
        TermQuery channelTermQuery = new TermQuery();
        channelTermQuery.setFieldName("programme");
        channelTermQuery.setTerm(ColumnValue.fromString("晚间新闻"));

        TermQuery televisionTermQuery = new TermQuery();
        televisionTermQuery.setFieldName("television");
        televisionTermQuery.setTerm(ColumnValue.fromString("中央电视台"));
        boolQuery.setMustQueries(new ArrayList<Query>() {{ add(channelTermQuery); add(televisionTermQuery); }});
        OTSSearchRequest request = new OTSSearchRequest.Builder().tableName(tableName).
                limit(10).indexName(indexName).query(boolQuery).returnColumns(returnColumns).build();
        OTSSearchResponse response = otsClient.search(request);
        Assert.assertTrue(response.status);
    }
}
