package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch._types.Refresh;
import org.junit.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frankcl
 * @date 2025-09-11 15:34:23
 */
public class ElasticSearchClientTest {

    public static class Record {
        public String key;
        public Integer count;
        public Float price;
        public String name;
    }

    private ElasticSearchClient elasticSearchClient;

    @Before
    public void setUp() {
        ElasticSearchClientConfig config = new ElasticSearchClientConfig();
        config.serverURL = "http://localhost:9200";
        elasticSearchClient = new ElasticSearchClient(config);
        Assert.assertTrue(elasticSearchClient.open());
    }

    @After
    public void tearDown() {
        elasticSearchClient.close();
    }

    @Test
    public void testOperations() {
        {
            Record record = new Record();
            record.count = 1;
            record.price = 1.0f;
            record.key = "key1";
            Assert.assertTrue(elasticSearchClient.put("key1", record, "test_index", Refresh.True));
        }

        {
            Record record = new Record();
            record.count = 2;
            record.price = 2.0f;
            record.key = "key2";
            Assert.assertTrue(elasticSearchClient.put("key2", record, "test_index", Refresh.True));
        }

        {
            Record record = new Record();
            record.count = 1;
            record.price = 2.0f;
            record.key = "key3";
            Assert.assertTrue(elasticSearchClient.put("key3", record, "test_index", Refresh.True));
        }

        {
            Assert.assertNull(elasticSearchClient.get("unknown", "test_index", Record.class));
            Record record = elasticSearchClient.get("key3", "test_index", Record.class);
            Assert.assertNotNull(record);
            Assert.assertEquals(record.count.intValue(), 1);
            Assert.assertEquals(record.key, "key3");
            Assert.assertEquals(record.price, 2.0f, 0.001f);
        }

        {
            Map<String, Object> updateRecord = new HashMap<>();
            updateRecord.put("price", 3.0);
            Assert.assertTrue(elasticSearchClient.update("key3", updateRecord, "test_index",
                    Record.class, Refresh.True));

            Record record = elasticSearchClient.get("key3", "test_index", Record.class);
            Assert.assertNotNull(record);
            Assert.assertEquals(record.count.intValue(), 1);
            Assert.assertEquals(record.key, "key3");
            Assert.assertEquals(record.price, 3.0f, 0.001f);
        }

        {
            ElasticSearchRequest searchRequest = new ElasticSearchRequest();
            searchRequest.index = "test_index";
            searchRequest.from = 0;
            ElasticSearchResponse<Record> searchResponse = elasticSearchClient.search(searchRequest, Record.class);
            Assert.assertEquals(3L, searchResponse.total.longValue());
            Assert.assertEquals(3, searchResponse.records.size());

            List<ElasticAggField> fields = new ArrayList<>();
            fields.add(new ElasticAggField("key"));
            fields.add(new ElasticAggField("count"));
            fields.add(new ElasticAggField("price"));
            Map<String, List<ElasticBucket<?>>> aggregateMap = elasticSearchClient.multiTermsAggregate(
                    searchRequest, fields, 100);
            Assert.assertEquals(3, aggregateMap.size());
            Assert.assertEquals(3, aggregateMap.get("key").size());
            Assert.assertEquals("key1", aggregateMap.get("key").get(0).key);
            Assert.assertEquals(1L, aggregateMap.get("key").get(0).count.longValue());
            Assert.assertEquals("key2", aggregateMap.get("key").get(1).key);
            Assert.assertEquals(1L, aggregateMap.get("key").get(1).count.longValue());
            Assert.assertEquals("key3", aggregateMap.get("key").get(2).key);
            Assert.assertEquals(1L, aggregateMap.get("key").get(2).count.longValue());
            Assert.assertEquals(2, aggregateMap.get("count").size());
            Assert.assertEquals(1L, aggregateMap.get("count").get(0).key);
            Assert.assertEquals(2L, aggregateMap.get("count").get(0).count.longValue());
            Assert.assertEquals(2L, aggregateMap.get("count").get(1).key);
            Assert.assertEquals(1L, aggregateMap.get("count").get(1).count.longValue());
            Assert.assertEquals(3, aggregateMap.get("price").size());
        }

        {
            ElasticSearchRequest searchRequest = new ElasticSearchRequest();
            searchRequest.index = "test_index";
            searchRequest.from = 0;

            List<ElasticAggField> fields = new ArrayList<>();
            fields.add(new ElasticAggField("count"));
            fields.add(new ElasticAggField("price"));
            List<ElasticBucket<?>> elasticBuckets = elasticSearchClient.nestedTermsAggregate(
                    searchRequest, 100, fields.toArray(new ElasticAggField[0]));
            Assert.assertEquals(2, elasticBuckets.size());
            {
                ElasticBucket<?> elasticBucket = elasticBuckets.get(0);
                Assert.assertEquals(1L, elasticBucket.key);
                Assert.assertEquals(2L, elasticBucket.count.longValue());
                Assert.assertEquals(1, elasticBucket.bucketMap.size());
                Assert.assertEquals(2, elasticBucket.bucketMap.get("price").size());
                Assert.assertEquals(1.0d, elasticBucket.bucketMap.get("price").get(0).key);
                Assert.assertEquals(1L, elasticBucket.bucketMap.get("price").get(0).count.longValue());
                Assert.assertEquals(3.0d, elasticBucket.bucketMap.get("price").get(1).key);
                Assert.assertEquals(1L, elasticBucket.bucketMap.get("price").get(1).count.longValue());
            }
            {
                ElasticBucket<?> elasticBucket = elasticBuckets.get(1);
                Assert.assertEquals(2L, elasticBucket.key);
                Assert.assertEquals(1L, elasticBucket.count.longValue());
                Assert.assertEquals(1, elasticBucket.bucketMap.size());
                Assert.assertEquals(1, elasticBucket.bucketMap.get("price").size());
                Assert.assertEquals(2.0d, elasticBucket.bucketMap.get("price").get(0).key);
                Assert.assertEquals(1L, elasticBucket.bucketMap.get("price").get(0).count.longValue());
            }
        }

        {
            Assert.assertTrue(elasticSearchClient.delete("key1", "test_index"));
            Assert.assertTrue(elasticSearchClient.delete("key2", "test_index"));
            Assert.assertTrue(elasticSearchClient.delete("key3", "test_index"));
        }
    }
}
