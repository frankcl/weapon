package xin.manong.weapon.base.milvus;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.DropCollectionReq;
import io.milvus.v2.service.collection.request.ListCollectionsReq;
import io.milvus.v2.service.collection.response.ListCollectionsResp;
import io.milvus.v2.service.database.request.CreateDatabaseReq;
import io.milvus.v2.service.database.request.DropDatabaseReq;
import io.milvus.v2.service.database.response.ListDatabasesResp;
import io.milvus.v2.service.vector.request.*;
import io.milvus.v2.service.vector.response.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Milvus客户端
 *
 * @author frankcl
 * @date 2026-01-19 15:57:12
 */
public class MilvusClient {

    private static final Logger logger = LoggerFactory.getLogger(MilvusClient.class);

    private final MilvusClientConfig config;
    private final Gson gson;
    private MilvusClientV2 client;

    public MilvusClient(MilvusClientConfig config) {
        this.config = config;
        this.gson = new Gson();
    }

    /**
     * 打开客户端
     *
     * @return 成功返回true，否则返回false
     */
    public boolean open() {
        logger.info("Milvus client start opening");
        if (config == null || !config.check()) {
            logger.error("Milvus client config is null or invalid");
            return false;
        }
        ConnectConfig.ConnectConfigBuilder builder = ConnectConfig.builder();
        builder.uri(config.endpoint).connectTimeoutMs(config.connectTimeoutMs).
                keepAliveTimeoutMs(config.keepAliveTimeoutMs);
        if (StringUtils.isNotEmpty(config.username)) {
            builder.token(config.username).password(config.password);
        }
        client = new MilvusClientV2(builder.build());
        logger.info("Milvus client open success");
        return true;
    }

    /**
     * 关闭客户端
     */
    public void close() {
        logger.info("Milvus client start closing");
        if (client != null) {
            client.close();
            client = null;
        }
        logger.info("Milvus client close success");
    }

    /**
     * 插入数据
     *
     * @param request 插入数据请求
     * @return 成功返回数据主键，否则返回null
     * @param <T> 数据类型
     */
    public <T> Object insert(MilvusInsertRequest<T> request) {
        request.check();
        InsertReq.InsertReqBuilder builder = InsertReq.builder().collectionName(request.collection);
        if (StringUtils.isNotEmpty(request.database)) builder.databaseName(request.database);
        if (StringUtils.isNotEmpty(request.partition)) builder.partitionName(request.partition);
        builder.data(List.of(gson.fromJson(JSON.toJSONString(request.data), JsonObject.class)));
        try {
            InsertResp response = client.insert(builder.build());
            if (response.getInsertCnt() != 1) {
                logger.error("Put data failed");
                return null;
            }
            return response.getPrimaryKeys().get(0);
        } catch (Exception e) {
            logger.error("Put data failed", e);
            return null;
        }
    }

    /**
     * 更新插入数据
     *
     * @param request 更新插入请求
     * @return 成功返回数据主键，否则返回null
     * @param <T> 数据类型
     */
    public <T> Object upsert(MilvusUpsertRequest<T> request) {
        request.check();
        UpsertReq.UpsertReqBuilder builder = UpsertReq.builder().collectionName(request.collection).
                partialUpdate(request.partialUpdate);
        if (StringUtils.isNotEmpty(request.database)) builder.databaseName(request.database);
        if (StringUtils.isNotEmpty(request.partition)) builder.partitionName(request.partition);
        builder.data(List.of(gson.fromJson(JSON.toJSONString(request.data), JsonObject.class)));
        try {
            UpsertResp response = client.upsert(builder.build());
            if (response.getUpsertCnt() != 1) {
                logger.error("Upsert data failed");
                return null;
            }
            return response.getPrimaryKeys().get(0);
        } catch (Exception e) {
            logger.error("Upsert data failed", e);
            return null;
        }
    }

    /**
     * 删除数据
     *
     * @param request 删除请求
     * @return 成功返回true，否则返回false
     */
    public boolean delete(MilvusDeleteRequest request) {
        request.check();
        DeleteReq.DeleteReqBuilder builder = DeleteReq.builder().collectionName(request.collection);
        if (StringUtils.isNotEmpty(request.database)) builder.databaseName(request.database);
        if (StringUtils.isNotEmpty(request.partition)) builder.partitionName(request.partition);
        builder.ids(List.of(request.id));
        try {
            DeleteResp response = client.delete(builder.build());
            if (response.getDeleteCnt() != 1) {
                logger.error("Delete data failed");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("Delete data failed", e);
            return false;
        }
    }

    /**
     * 根据ID获取数据
     *
     * @param request 请求
     * @param recordType 数据类型
     * @return 成功返回数据，否则返回null
     * @param <T> 数据类型
     */
    public <T> T get(MilvusGetRequest request, Class<T> recordType) {
        request.check();
        GetReq.GetReqBuilder builder = GetReq.builder().collectionName(request.collection).ids(List.of(request.id));
        if (StringUtils.isNotEmpty(request.database)) builder.databaseName(request.database);
        if (StringUtils.isNotEmpty(request.partition)) builder.partitionName(request.partition);
        try {
            GetResp response = client.get(builder.build());
            if (response.getGetResults().isEmpty()) return null;
            return JSON.parseObject(JSON.toJSONString(response.getGetResults().get(0).getEntity()), recordType);
        } catch (Exception e) {
            logger.error("Get data failed for id:{}", request.id);
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 搜索
     *
     * @param request 搜索请求
     * @param recordType 数据类型
     * @return 搜索响应
     * @param <T> 数据类型
     */
    public <T> MilvusSearchResponse<T> search(MilvusSearchRequest request, Class<T> recordType) {
        request.check();
        SearchReq.SearchReqBuilder builder = SearchReq.builder().collectionName(request.collection).
                annsField(request.field).data(List.of(request.vector)).metricType(request.metricType).
                offset(request.offset).limit(request.limit);
        if (StringUtils.isNotEmpty(request.database)) builder.databaseName(request.database);
        if (request.partitions != null && !request.partitions.isEmpty()) builder.partitionNames(request.partitions);
        if (StringUtils.isNotEmpty(request.filter)) builder.filter(request.filter);
        if (request.outputFields != null && !request.outputFields.isEmpty()) builder.outputFields(request.outputFields);
        if (request.paramMap != null && !request.paramMap.isEmpty()) builder.searchParams(request.paramMap);
        MilvusSearchResponse<T> searchResponse = new MilvusSearchResponse<>();
        try {
            SearchResp response = client.search(builder.build());
            if (response.getSearchResults().isEmpty()) return searchResponse;
            searchResponse.sessionTs = response.getSessionTs();
            response.getSearchResults().get(0).forEach(searchResult -> {
                MilvusRecord.Builder<T> b = new MilvusRecord.Builder<>();
                b.id(searchResult.getId()).primaryKey(searchResult.getPrimaryKey()).score(searchResult.getScore());
                b.value(JSON.parseObject(JSON.toJSONString(searchResult.getEntity()), recordType));
                searchResponse.addRecord(b.build());
            });
            return searchResponse;
        } catch (Exception e) {
            logger.error("Search data failed", e);
            return searchResponse;
        }
    }

    /**
     * 创建数据库
     *
     * @param database 数据库名称
     * @return 成功返回true，否则返回false
     */
    public boolean createDatabase(String database) {
        ListDatabasesResp response = client.listDatabases();
        if (response.getDatabaseNames().contains(database)) {
            logger.error("Database {} already exists", database);
            return false;
        }
        CreateDatabaseReq.CreateDatabaseReqBuilder builder = CreateDatabaseReq.builder();
        builder.databaseName(database);
        client.createDatabase(builder.build());
        logger.info("Create database {} success", StringUtils.isEmpty(database) ? "default" : database);
        return true;
    }

    /**
     * 删除数据库
     *
     * @param database 数据库名称
     */
    public void deleteDatabase(String database) {
        DropDatabaseReq.DropDatabaseReqBuilder builder = DropDatabaseReq.builder();
        builder.databaseName(database);
        client.dropDatabase(builder.build());
        logger.info("Delete database {} success", StringUtils.isEmpty(database) ? "default" : database);
    }

    /**
     * 创建collection
     *
     * @param collection collection名称
     * @param database 数据库名称
     * @param schema collection schema
     * @param indexParams 索引信息
     * @return 成功返回true，否则返回false
     */
    public boolean createCollection(String collection, String database,
                                    CreateCollectionReq.CollectionSchema schema,
                                    List<IndexParam> indexParams) {
        ListCollectionsReq.ListCollectionsReqBuilder listBuilder = ListCollectionsReq.builder();
        if (StringUtils.isNotEmpty(database)) listBuilder.databaseName(database);
        ListCollectionsResp listResponse = client.listCollectionsV2(listBuilder.build());
        if (listResponse.getCollectionNames().contains(collection)) {
            logger.error("Collection {} already exists", collection);
            return false;
        }
        CreateCollectionReq.CreateCollectionReqBuilder builder = CreateCollectionReq.builder();
        builder.collectionName(collection).collectionSchema(schema);
        if (StringUtils.isNotEmpty(database)) builder.databaseName(database);
        if (indexParams != null && !indexParams.isEmpty()) builder.indexParams(indexParams);
        client.createCollection(builder.build());
        logger.info("Create collection {} success for database:{}",
                collection, StringUtils.isEmpty(database) ? "default" : database);
        return true;
    }

    /**
     * 删除collection
     *
     * @param collection collection名称
     * @param database 数据库名称
     */
    public void deleteCollection(String collection, String database) {
        DropCollectionReq.DropCollectionReqBuilder builder = DropCollectionReq.builder();
        builder.collectionName(collection);
        if (StringUtils.isNotEmpty(database)) builder.databaseName(database);
        client.dropCollection(builder.build());
        logger.info("Delete collection {} success for database:{}",
                collection, StringUtils.isEmpty(database) ? "default" : database);
    }
}
