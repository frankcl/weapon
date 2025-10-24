package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import org.junit.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frankcl
 * @date 2025-09-11 15:34:23
 */
public class ElasticSearchClientTest {

    public static class Record implements ElasticHighlightRecord {
        public String key;
        public Integer count;
        public Float price;
        public String name;
        public String title;
        public Map<String, List<String>> highlightMap;

        @Override
        public void injectHighlight(Map<String, List<String>> highlightMap) {
            this.highlightMap = highlightMap;
        }
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
    public void testOperations() throws ConflictVersionException{
        ElasticOption option = new ElasticOption(Refresh.True);
        {
            Record record = new Record();
            record.count = 1;
            record.price = 1.0f;
            record.key = "key1";
            record.title = "哈哈哈";
            Assert.assertTrue(elasticSearchClient.put("key1", record, "test_index", option));
        }

        {
            Record record = new Record();
            record.count = 2;
            record.price = 2.0f;
            record.key = "key2";
            record.title = "测试设备";
            Assert.assertTrue(elasticSearchClient.put("key2", record, "test_index", option));
        }

        {
            Record record = new Record();
            record.count = 1;
            record.price = 2.0f;
            record.key = "key3";
            record.title = "测试机器";
            Assert.assertTrue(elasticSearchClient.put("key3", record, "test_index", option));
        }

        {
            Assert.assertNull(elasticSearchClient.get("unknown", "test_index", Record.class));
            ElasticRecord<Record> record = elasticSearchClient.get("key3", "test_index", Record.class);
            Assert.assertNotNull(record);
            Assert.assertEquals(record.value.count.intValue(), 1);
            Assert.assertEquals(record.value.key, "key3");
            Assert.assertEquals(record.value.price, 2.0f, 0.001f);
        }

        {
            Map<String, Object> updateRecord = new HashMap<>();
            updateRecord.put("price", 3.0);
            Assert.assertTrue(elasticSearchClient.update("key3", updateRecord, "test_index",
                    Record.class, option));

            ElasticRecord<Record> record = elasticSearchClient.get("key3", "test_index", Record.class);
            Assert.assertNotNull(record);
            Assert.assertEquals(record.value.count.intValue(), 1);
            Assert.assertEquals(record.value.key, "key3");
            Assert.assertEquals(record.value.price, 3.0f, 0.001f);
        }

        {
            ElasticSearchRequest searchRequest = new ElasticSearchRequest();
            searchRequest.index = "test_index";
            searchRequest.from = 0;
            ElasticSearchResponse<Record> searchResponse = elasticSearchClient.search(searchRequest, Record.class);
            Assert.assertEquals(3L, searchResponse.total.longValue());
            Assert.assertEquals(3, searchResponse.records.size());
        }

        {
            ElasticSearchRequest searchRequest = new ElasticSearchRequest();
            ElasticHighlight highlight = new ElasticHighlight("title");
            highlight.addPreTag("<strong>");
            highlight.addPostTag("</strong>");
            searchRequest.index = "test_index";
            searchRequest.from = 0;
            searchRequest.addHighlight(highlight);
            searchRequest.query = MatchQuery.of(b -> b.field("title").query("测试"))._toQuery();
            ElasticSearchResponse<Record> searchResponse = elasticSearchClient.search(searchRequest, Record.class);
            Assert.assertEquals(2L, searchResponse.total.longValue());
            Assert.assertEquals(2, searchResponse.records.size());
        }

        {
            Assert.assertTrue(elasticSearchClient.delete("key1", "test_index"));
            Assert.assertTrue(elasticSearchClient.delete("key2", "test_index"));
            Assert.assertTrue(elasticSearchClient.delete("key3", "test_index"));
        }
    }
}
