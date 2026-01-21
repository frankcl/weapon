package xin.manong.weapon.base.milvus;

import com.alibaba.fastjson.annotation.JSONField;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author frankcl
 * @date 2026-01-21 14:49:28
 */
public class MilvusClientTest {

    private final String database = "test_db";
    private final String collection = "test_collection";
    private MilvusClient client;

    @Before
    public void setUp() throws Exception {
        MilvusClientConfig config = new MilvusClientConfig();
        config.endpoint = "http://120.27.208.214:19530";
        client = new MilvusClient(config);
        Assert.assertTrue(client.open());
        prepare();
    }

    @After
    public void tearDown() throws Exception {
        destroy();
        client.close();
    }

    private void prepare() {
        Assert.assertTrue(client.createDatabase(database));
        CreateCollectionReq.CollectionSchema schema = MilvusClientV2.CreateSchema();
        schema.addField(AddFieldReq.builder().
                fieldName("id").
                dataType(DataType.VarChar).
                maxLength(10).
                isPrimaryKey(true).
                autoID(false).build());
        schema.addField(AddFieldReq.builder().
                fieldName("vector").
                dataType(DataType.FloatVector).
                dimension(3).build());
        schema.addField(AddFieldReq.builder().
                fieldName("score").
                isNullable(true).
                dataType(DataType.Double).build());

        IndexParam vectorIndexParam = IndexParam.builder().
                fieldName("vector").
                metricType(IndexParam.MetricType.COSINE).
                indexType(IndexParam.IndexType.AUTOINDEX).build();
        List<IndexParam> indexParams = new ArrayList<>();
        indexParams.add(vectorIndexParam);

        Assert.assertTrue(client.createCollection(collection, database, schema, indexParams));
    }

    private void destroy() {
        client.deleteCollection(collection, database);
        client.deleteDatabase(database);
    }

    static class Record {
        @JSONField(name = "id")
        public String id;
        @JSONField(name = "score")
        public Double score;
        @JSONField(name = "vector")
        public float[] vector;
    }

    @Test
    public void testOperations() throws Exception {
        {
            Record record = new Record();
            record.id = "abc";
            record.score = 1.0d;
            record.vector = new float[] { 1.0f, 2.0f, 3.0f };
            MilvusInsertRequest.Builder<Record> builder = new MilvusInsertRequest.Builder<>();
            Object primaryKey = client.insert(builder.data(record).collection(collection).database(database).build());
            Assert.assertEquals("abc", primaryKey);
        }
        {
            Record record = new Record();
            record.id = "def";
            record.score = 2.0d;
            record.vector = new float[] { 1.0f, 2.0f, 2.0f };
            MilvusInsertRequest.Builder<Record> builder = new MilvusInsertRequest.Builder<>();
            Object primaryKey = client.insert(builder.data(record).collection(collection).database(database).build());
            Assert.assertEquals("def", primaryKey);
        }
        {
            Record record = new Record();
            record.id = "def";
            record.vector = new float[] { 1.0f, 1.0f, 1.0f };
            MilvusUpsertRequest.Builder<Record> builder = new MilvusUpsertRequest.Builder<>();
            Object primaryKey = client.upsert(builder.data(record).collection(collection).
                    database(database).partialUpdate(true).build());
            Assert.assertEquals("def", primaryKey);
        }
        {
            Thread.sleep(3000);
            MilvusGetRequest.Builder builder = new MilvusGetRequest.Builder();
            builder.collection(collection).database(database).id("def");
            Record record = client.get(builder.build(), Record.class);
            Assert.assertNotNull(record);
            Assert.assertEquals("def", record.id);
            Assert.assertEquals(2.0d, record.score, 0.1d);
            Assert.assertEquals(3, record.vector.length);
            Assert.assertEquals(1.0f, record.vector[0], 0.1f);
            Assert.assertEquals(1.0f, record.vector[1], 0.1f);
            Assert.assertEquals(1.0f, record.vector[2], 0.1f);
        }
        {
            MilvusSearchRequest.Builder builder = new MilvusSearchRequest.Builder();
            builder.collection(collection).database(database).field("vector").
                    metricType(IndexParam.MetricType.COSINE).vector(new FloatVec(List.of(1f, 2f, 1f))).
                    outputFields(List.of("id", "score", "vector"));
            MilvusSearchResponse<Record> response = client.search(builder.build(), Record.class);
            Assert.assertEquals(2, response.records.size());
            Assert.assertEquals("def", response.records.get(0).id);
            Assert.assertEquals("abc", response.records.get(1).id);
        }
        {
            MilvusDeleteRequest.Builder builder = new MilvusDeleteRequest.Builder();
            builder.collection(collection).database(database).id("def");
            Assert.assertTrue(client.delete(builder.build()));
        }
        {
            MilvusDeleteRequest.Builder builder = new MilvusDeleteRequest.Builder();
            builder.collection(collection).database(database).id("abc");
            Assert.assertTrue(client.delete(builder.build()));
        }
    }
}
