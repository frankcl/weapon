package xin.manong.weapon.base.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ES客户端封装
 *
 * @author frankcl
 * @date 2025-09-11 12:21:58
 */
public class ElasticSearchClient {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchClient.class);

    private final ElasticSearchClientConfig config;
    private ElasticsearchClient client;

    public ElasticSearchClient(ElasticSearchClientConfig config) {
        this.config = config;
    }

    /**
     * 打开客户端
     *
     * @return 成功返回true，否则返回false
     */
    public boolean open() {
        logger.info("elasticsearch client start opening");
        if (config == null || !config.check()) {
            logger.error("elasticsearch client config is invalid");
            return false;
        }
        RestClientBuilder builder = RestClient.builder(HttpHost.create(config.serverURL));
        if (StringUtils.isNotEmpty(config.apiKey)) {
            builder.setDefaultHeaders(new Header[] {
                    new BasicHeader("Authorization", "ApiKey " + config.apiKey)});
        }
        RestClient restClient = builder.build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
        logger.info("elasticsearch client open success");
        return true;
    }

    /**
     * 关闭客户端
     */
    public void close() {
        logger.info("elasticsearch client start closing");
        try {
            if (client != null) client._transport().close();
        } catch (IOException e) {
            logger.error("close elasticsearch client error");
            logger.error(e.getMessage(), e);
        }
        logger.info("elasticsearch client close success");
    }

    /**
     * 根据ID获取数据
     *
     * @param id 数据ID
     * @param index 索引名
     * @param documentClass 数据类型
     * @return 成功返回数据，否则返回null
     * @param <T> 数据类型
     */
    public <T> ElasticRecord<T> get(String id, String index, Class<T> documentClass) {
        GetRequest request = GetRequest.of(builder -> builder.index(index).id(id));
        try {
            GetResponse<T> response = client.get(request, documentClass);
            if (!response.found()) return null;
            return new ElasticRecord<>(response.seqNo(), response.primaryTerm(),
                    response.version(), response.source());
        } catch (Exception e) {
            logger.error("get failed for id:{}, index:{}", id, index);
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 添加数据
     *
     * @param id 数据ID
     * @param doc 数据
     * @param index 索引名
     * @return 成功返回true，否则返回false
     * @param <T> 数据类型
     */
    public <T> boolean put(String id, T doc, String index) {
        try {
            return put(id, doc, index, null, null, Refresh.False);
        } catch (ConflictVersionException e) {
            return false;
        }
    }

    /**
     * 添加数据
     *
     * @param id 数据ID
     * @param doc 数据
     * @param index 索引名
     * @param seqNo 序号
     * @param primaryTerm 主项
     * @param refresh 是否立即刷新索引
     * @return 成功返回true，否则返回false
     * @param <T> 数据类型
     * @throws ConflictVersionException 版本冲突异常
     */
    public <T> boolean put(String id, T doc, String index,
                           Long seqNo, Long primaryTerm,
                           Refresh refresh) throws ConflictVersionException {
        IndexRequest<T> request = IndexRequest.of(builder -> {
            builder.index(index).id(id).document(doc).
                    refresh(refresh == null ? Refresh.False : refresh);
            if (seqNo != null) builder.ifSeqNo(seqNo);
            if (primaryTerm != null) builder.ifPrimaryTerm(primaryTerm);
            return builder;
        });
        try {
            IndexResponse response = client.index(request);
            logger.debug("put success for id:{}, version:{}", id, response.version());
            return true;
        } catch (Exception e) {
            checkVersionConflict(e);
            logger.error("put failed for id:{}, index:{}", id, index);
            return false;
        }
    }

    /**
     * 删除数据
     *
     * @param id 数据ID
     * @param index 索引名
     * @return 成功返回true，否则返回false
     */
    public boolean delete(String id, String index) {
        try {
            return delete(id, index, null, null, Refresh.False);
        } catch (ConflictVersionException e) {
            return false;
        }
    }

    /**
     * 删除数据
     *
     * @param id 数据ID
     * @param index 索引名
     * @param seqNo 序号
     * @param primaryTerm 主项
     * @param refresh 是否立即刷新索引
     * @return 成功返回true，否则返回false
     * @throws ConflictVersionException 版本冲突异常
     */
    public boolean delete(String id, String index,
                          Long seqNo, Long primaryTerm,
                          Refresh refresh) throws ConflictVersionException {
        DeleteRequest request = DeleteRequest.of(builder -> {
            builder.index(index).id(id).
                    refresh(refresh == null ? Refresh.False : refresh);
            if (seqNo != null) builder.ifSeqNo(seqNo);
            if (primaryTerm != null) builder.ifPrimaryTerm(primaryTerm);
            return builder;
        });
        try {
            DeleteResponse response = client.delete(request);
            logger.debug("delete success for id:{}, version:{}", id, response.version());
            return true;
        } catch (Exception e) {
            checkVersionConflict(e);
            logger.error("delete failed for id:{}, index:{}", id, index);
            return false;
        }
    }

    /**
     * 更新数据，不存在插入数据
     *
     * @param id 数据ID
     * @param doc 更新数据
     * @param index 索引名
     * @param documentClass 完整数据类型
     * @return 成功返回true，否则返回false
     * @param <TDocument> 完整数据类型
     * @param <TPartialDocument> 更新数据类型
     */
    public <TDocument, TPartialDocument> boolean upsert(String id, TPartialDocument doc,
                                                        String index, Class<TDocument> documentClass) {
        try {
            return upsert(id, doc, index, documentClass, null, null, Refresh.False);
        } catch (ConflictVersionException e) {
            return false;
        }
    }

    /**
     * 更新数据，不存在插入数据
     *
     * @param id 数据ID
     * @param doc 更新数据
     * @param index 索引名
     * @param documentClass 完整数据类型
     * @param seqNo 序号
     * @param primaryTerm 主项
     * @param refresh 是否立即刷新索引
     * @return 成功返回true，否则返回false
     * @param <TDocument> 完整数据类型
     * @param <TPartialDocument> 更新数据类型
     * @throws ConflictVersionException 版本冲突异常
     */
    public <TDocument, TPartialDocument> boolean upsert(String id, TPartialDocument doc,
                                                        String index, Class<TDocument> documentClass,
                                                        Long seqNo, Long primaryTerm,
                                                        Refresh refresh) throws ConflictVersionException {
        UpdateRequest<TDocument, TPartialDocument> request = UpdateRequest.of(
                builder -> {
                    builder.index(index).id(id).doc(doc).docAsUpsert(true).
                            refresh(refresh == null ? Refresh.False : refresh);
                    if (seqNo != null) builder.ifSeqNo(seqNo);
                    if (primaryTerm != null) builder.ifPrimaryTerm(primaryTerm);
                    return builder;
                });
        return update(request, documentClass);
    }

    /**
     * 更新数据
     *
     * @param id 数据ID
     * @param doc 更新数据
     * @param index 索引名
     * @param documentClass 完整数据类型
     * @return 成功返回true，否则返回false
     * @param <TDocument> 完整数据类型
     * @param <TPartialDocument> 更新数据类型
     */
    public <TDocument, TPartialDocument> boolean update(String id, TPartialDocument doc,
                                                        String index, Class<TDocument> documentClass) {
        try {
            return update(id, doc, index, documentClass, null, null, Refresh.False);
        } catch (ConflictVersionException e) {
            return false;
        }
    }

    /**
     * 更新数据
     *
     * @param id 数据ID
     * @param doc 更新数据
     * @param index 索引名
     * @param documentClass 完整数据类型
     * @param seqNo 序号
     * @param primaryTerm 主项
     * @param refresh 是否立即刷新索引
     * @return 成功返回true，否则返回false
     * @param <TDocument> 完整数据类型
     * @param <TPartialDocument> 更新数据类型
     * @throws ConflictVersionException 版本冲突异常
     */
    public <TDocument, TPartialDocument> boolean update(String id, TPartialDocument doc,
                                                        String index, Class<TDocument> documentClass,
                                                        Long seqNo, Long primaryTerm, Refresh refresh)
            throws ConflictVersionException {
        UpdateRequest<TDocument, TPartialDocument> request = UpdateRequest.of(
                builder -> {
                    builder.index(index).id(id).doc(doc).docAsUpsert(false).
                            refresh(refresh == null ? Refresh.False : refresh);
                    if (seqNo != null) builder.ifSeqNo(seqNo);
                    if (primaryTerm != null) builder.ifPrimaryTerm(primaryTerm);
                    return builder;
                });
        return update(request, documentClass);
    }

    /**
     * 搜索数据
     *
     * @param searchRequest 搜索请求
     * @param documentClass 数据类型
     * @return 搜索响应
     * @param <T> 数据类型
     */
    public <T> ElasticSearchResponse<T> search(ElasticSearchRequest searchRequest, Class<T> documentClass) {
        List<SortOptions> sortOptions = buildSortOptions(searchRequest);
        SearchRequest request = SearchRequest.of(builder -> {
            builder.index(searchRequest.index).query(searchRequest.query).
                    from(searchRequest.from).size(searchRequest.size);
            if (sortOptions != null && !sortOptions.isEmpty()) builder.sort(sortOptions);
            handleIncludeExclude(builder, searchRequest);
            handleHighlight(builder, searchRequest);
            return builder;
        });
        return search(request, documentClass);
    }

    /**
     * 根据游标搜索数据
     *
     * @param searchRequest 搜索请求
     * @param documentClass 数据类型
     * @return 搜索响应
     * @param <T> 数据类型
     */
    public <T> ElasticSearchResponse<T> searchWithCursor(ElasticSearchRequest searchRequest,
                                                         Class<T> documentClass) {
        assert searchRequest.sortOptions != null && !searchRequest.sortOptions.isEmpty();
        assert searchRequest.cursor == null || searchRequest.cursor.size() == searchRequest.sortOptions.size();
        List<SortOptions> sortOptions = buildSortOptions(searchRequest);
        SearchRequest request = SearchRequest.of(builder -> {
            builder.index(searchRequest.index).query(searchRequest.query).size(searchRequest.size).sort(sortOptions);
            if (searchRequest.cursor != null) builder.searchAfter(searchRequest.cursor);
            handleIncludeExclude(builder, searchRequest);
            handleHighlight(builder, searchRequest);
            return builder;
        });
        return search(request, documentClass);
    }

    /**
     * terms聚合
     *
     * @param searchRequest 搜索请求
     * @param aggregationMap 聚合请求
     * @return 聚合结果
     */
    public Map<String, List<ElasticBucket<?>>> termsAggregate(ElasticSearchRequest searchRequest,
                                                              Map<String, Aggregation> aggregationMap) {
        assert aggregationMap != null && !aggregationMap.isEmpty();
        SearchRequest request = SearchRequest.of(builder ->
                builder.index(searchRequest.index).query(searchRequest.query).
                        size(0).aggregations(aggregationMap));
        try {
            SearchResponse<Void> response = client.search(request, Void.class);
            Map<String, Aggregate> aggregateMap = response.aggregations();
            return buildElasticBucketMap(aggregateMap);
        } catch (Exception e) {
            logger.error("Terms aggregate error for index:{}", searchRequest.index);
            logger.error(e.getMessage(), e);
            return new HashMap<>();
        }
    }

    /**
     * 根据聚合结果构建ElasticBucket结果
     *
     * @param aggregateMap 聚合结果
     * @return ElasticBucket结果
     */
    private Map<String, List<ElasticBucket<?>>> buildElasticBucketMap(Map<String, Aggregate> aggregateMap) {
        Map<String, List<ElasticBucket<?>>> bucketMap = new HashMap<>();
        for (Map.Entry<String, Aggregate> entry : aggregateMap.entrySet()) {
            Aggregate aggregate = entry.getValue();
            List<ElasticBucket<?>> buckets = new ArrayList<>();
            Buckets<? extends TermsBucketBase> termsBuckets = getTermsBuckets(aggregate);
            for (TermsBucketBase termsBucket : termsBuckets.array()) {
                ElasticBucket<?> elasticBucket = buildElasticBucket(termsBucket);
                buckets.add(elasticBucket);
                if (termsBucket.aggregations().isEmpty()) continue;
                elasticBucket.bucketMap = buildElasticBucketMap(termsBucket.aggregations());
            }
            bucketMap.put(entry.getKey(), buckets);
        }
        return bucketMap;
    }

    /**
     * 嵌套terms聚合
     *
     * @param searchRequest 搜索请求
     * @param bucketSize 聚合桶大小
     * @param fields 嵌套聚合字段
     * @return 聚合结果
     */
    public List<ElasticBucket<?>> nestedTermsAggregate(ElasticSearchRequest searchRequest,
                                                       int bucketSize, ElasticAggField ... fields) {
        assert fields != null && fields.length > 0;
        SearchRequest request = SearchRequest.of(builder ->
                builder.index(searchRequest.index).query(searchRequest.query).size(0).
                aggregations(fields[0].name, buildNestedTermsAggRequest(0, fields, bucketSize)));
        try {
            SearchResponse<Void> response = client.search(request, Void.class);
            Map<String, Aggregate> aggregateMap = response.aggregations();
            return buildNestedAggResponse(0, fields, aggregateMap);
        } catch (Exception e) {
            logger.error("nested terms aggregate error for index:{}", searchRequest.index);
            logger.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 多terms聚合
     *
     * @param searchRequest 搜索请求
     * @param fields 聚合字段
     * @param bucketSize 聚合桶大小
     * @return 聚合结果
     */
    public Map<String, List<ElasticBucket<?>>> multiTermsAggregate(ElasticSearchRequest searchRequest,
                                                                   List<ElasticAggField> fields, int bucketSize) {
        Map<String, List<ElasticBucket<?>>> aggregateMap = new HashMap<>();
        Map<String, Aggregation> aggregationRequest = new HashMap<>();
        fields.forEach(field -> aggregationRequest.put(field.name, buildAggregation(field, bucketSize)));
        SearchRequest request = SearchRequest.of(builder -> builder.index(searchRequest.index).
                query(searchRequest.query).size(0).aggregations(aggregationRequest));
        try {
            SearchResponse<Void> response = client.search(request, Void.class);
            for (ElasticAggField field : fields) {
                List<ElasticBucket<?>> elasticBuckets = new ArrayList<>();
                Aggregate aggregate = response.aggregations().get(field.name);
                while (aggregate.isNested()) aggregate = aggregate.nested().aggregations().get(field.name);
                Buckets<? extends TermsBucketBase> buckets = getTermsBuckets(aggregate);
                for (TermsBucketBase bucket : buckets.array()) {
                    elasticBuckets.add(buildElasticBucket(bucket));
                }
                aggregateMap.put(field.name, elasticBuckets);
            }
            return aggregateMap;
        } catch (Exception e) {
            logger.error("multi terms aggregate error for index:{}", searchRequest.index);
            logger.error(e.getMessage(), e);
            return aggregateMap;
        }
    }

    /**
     * 处理字段包含排除
     *
     * @param builder ES搜索请求builder
     * @param searchRequest 搜索请求
     */
    private void handleIncludeExclude(SearchRequest.Builder builder, ElasticSearchRequest searchRequest) {
        if ((searchRequest.includes == null || searchRequest.includes.isEmpty()) &&
            (searchRequest.excludes == null || searchRequest.excludes.isEmpty())) return;
        builder.source(SourceConfig.of(b -> b.filter(SourceFilter.of(filter -> {
            if (searchRequest.includes != null && !searchRequest.includes.isEmpty()) {
                filter.includes(searchRequest.includes);
            }
            if (searchRequest.excludes != null && !searchRequest.excludes.isEmpty()) {
                filter.excludes(searchRequest.excludes);
            }
            return filter;
        }))));
    }

    /**
     * 处理高亮请求
     *
     * @param builder 搜索请求构建器
     * @param searchRequest ES搜索请求
     */
    private void handleHighlight(SearchRequest.Builder builder, ElasticSearchRequest searchRequest) {
        if (searchRequest.highlights == null || searchRequest.highlights.isEmpty()) return;
        Map<String, HighlightField> highlightFieldMap = new HashMap<>();
        for (ElasticHighlight highlight : searchRequest.highlights) {
            HighlightField.Builder b = new HighlightField.Builder();
            b.fragmentSize(highlight.fragmentSize).numberOfFragments(highlight.fragmentNum);
            if (highlight.preTags != null) b.preTags(highlight.preTags);
            if (highlight.postTags != null) b.postTags(highlight.postTags);
            highlightFieldMap.put(highlight.field, b.build());
        }
        builder.highlight(b -> b.fields(highlightFieldMap));
    }

    /**
     * 更新数据
     *
     * @param request 更新请求
     * @param documentClass 完整数据类型
     * @return 成功返回true，否则返回false
     * @param <TDocument> 完整数据类型
     * @param <TPartialDocument> 更新数据类型
     * @throws ConflictVersionException 版本冲突异常
     */
    private <TDocument, TPartialDocument> boolean update(UpdateRequest<TDocument, TPartialDocument> request,
                                                         Class<TDocument> documentClass) throws ConflictVersionException {
        try {
            UpdateResponse<TDocument> response = client.update(request, documentClass);
            logger.debug("update success for id:{}, version:{}", request.id(), response.version());
            return true;
        } catch (Exception e) {
            checkVersionConflict(e);
            logger.error("update failed for id:{}, index:{}", request.id(), request.index());
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 搜索数据
     *
     * @param request 搜索请求
     * @param documentClass 数据类型
     * @return 搜索响应
     * @param <T> 数据类型
     */
    private <T> ElasticSearchResponse<T> search(SearchRequest request, Class<T> documentClass) {
        try {
            SearchResponse<T> response = client.search(request, documentClass);
            HitsMetadata<T> hitsMetadata = response.hits();
            ElasticSearchResponse<T> searchResponse = new ElasticSearchResponse<>();
            searchResponse.from = request.from();
            searchResponse.size = request.size();
            searchResponse.total = hitsMetadata.total() == null ? 0L : hitsMetadata.total().value();
            if (hitsMetadata.total() != null) {
                searchResponse.totalHitRelation = hitsMetadata.total().relation().jsonValue();
            }
            List<Hit<T>> hits = hitsMetadata.hits();
            for (Hit<T> hit : hits) {
                T record = hit.source();
                if (record instanceof ElasticHighlightRecord) {
                    ((ElasticHighlightRecord) record).injectHighlight(hit.highlight());
                }
                searchResponse.records.add(record);

            }
            if (!hits.isEmpty()) searchResponse.cursor = hits.get(hits.size() - 1).sort();
            return searchResponse;
        } catch (Exception e) {
            logger.error("search error for index:{}", request.index());
            logger.error(e.getMessage(), e);
            return new ElasticSearchResponse<>();
        }
    }

    /**
     * 构建嵌套terms聚合请求
     *
     * @param cursor 聚合字段游标
     * @param fields 聚合字段数组
     * @param bucketSize 聚合桶大小
     * @return 聚合请求
     */
    private Aggregation buildNestedTermsAggRequest(int cursor, ElasticAggField[] fields, int bucketSize) {
        if (cursor == fields.length - 1) {
            return buildAggregation(fields[cursor], bucketSize);
        }
        Aggregation subAggregation = buildNestedTermsAggRequest(cursor + 1, fields, bucketSize);
        Aggregation aggregation = Aggregation.of(builder -> builder.terms(
                t -> t.field(fields[cursor].name).size(bucketSize)).
                aggregations(fields[cursor+1].name, subAggregation));
        if (fields[cursor].nested) {
            return Aggregation.of(builder -> builder.nested(n ->
                    n.path(fields[cursor].path)).aggregations(fields[cursor].name, aggregation));
        }
        return aggregation;
    }

    /**
     * 构建嵌套terms聚合结果
     *
     * @param cursor 聚合字段游标
     * @param fields 聚合字段数组
     * @param aggregateMap 当前字段聚合结果
     * @return 聚合结果
     */
    private List<ElasticBucket<?>> buildNestedAggResponse(int cursor, ElasticAggField[] fields,
                                                          Map<String, Aggregate> aggregateMap) {
        List<ElasticBucket<?>> elasticBuckets = new ArrayList<>();
        if (cursor < 0 || cursor >= fields.length) return elasticBuckets;
        Aggregate aggregate = aggregateMap.get(fields[cursor].name);
        while (aggregate.isNested()) aggregate = aggregate.nested().aggregations().get(fields[cursor].name);
        Buckets<? extends TermsBucketBase> termsBuckets = getTermsBuckets(aggregate);
        for (TermsBucketBase termsBucket : termsBuckets.array()) {
            ElasticBucket<?> elasticBucket = buildElasticBucket(termsBucket);
            elasticBuckets.add(elasticBucket);
            if (termsBucket.aggregations().isEmpty()) continue;
            elasticBucket.bucketMap = new HashMap<>();
            elasticBucket.bucketMap.put(fields[cursor+1].name,
                    buildNestedAggResponse(cursor + 1, fields, termsBucket.aggregations()));
        }
        return elasticBuckets;
    }

    /**
     * 从聚合结果中获取terms桶
     *
     * @param aggregate 聚合结果
     * @return terms桶
     */
    private Buckets<? extends TermsBucketBase> getTermsBuckets(Aggregate aggregate) {
        if (aggregate.isDterms()) return aggregate.dterms().buckets();
        else if (aggregate.isLterms()) return aggregate.lterms().buckets();
        return aggregate.sterms().buckets();
    }

    /**
     * 根据term桶生成ElasticBucket
     *
     * @param termBucket term桶
     * @return ElasticBucket
     */
    private ElasticBucket<?> buildElasticBucket(TermsBucketBase termBucket) {
        if (termBucket instanceof LongTermsBucket lTermsBucket) {
            return new ElasticBucket<>(lTermsBucket.key(), lTermsBucket.docCount());
        } else if (termBucket instanceof DoubleTermsBucket dTermsBucket) {
            return new ElasticBucket<>(dTermsBucket.key(), dTermsBucket.docCount());
        }
        StringTermsBucket sTermsBucket = (StringTermsBucket) termBucket;
        return new ElasticBucket<>(sTermsBucket.key().stringValue(), sTermsBucket.docCount());
    }

    /**
     * 构建排序选项
     *
     * @param searchRequest 搜索请求
     * @return 排序选项
     */
    private List<SortOptions> buildSortOptions(ElasticSearchRequest searchRequest) {
        if (searchRequest.sortOptions == null) return null;
        List<SortOptions> sortOptions = new ArrayList<>();
        for (ElasticSortOption sortRequest : searchRequest.sortOptions) {
            sortOptions.add(SortOptions.of(b -> b.field(
                    f -> f.field(sortRequest.field).order(sortRequest.sortOrder))));
        }
        return sortOptions.isEmpty() ? null : sortOptions;
    }

    /**
     * 构建聚合：区分普通字段聚合和nested字段聚合
     *
     * @param field 字段
     * @param bucketSize 桶大小
     * @return 聚合
     */
    private Aggregation buildAggregation(ElasticAggField field, int bucketSize) {
        Aggregation aggregation = Aggregation.of(builder ->
                builder.terms(t -> t.field(field.name).size(bucketSize)));
        if (field.nested) {
            return Aggregation.of(builder -> builder.nested(n ->
                    n.path(field.path)).aggregations(field.name, aggregation));
        }
        return aggregation;
    }

    /**
     * 检测是否版本冲突
     *
     * @param e 异常
     * @throws ConflictVersionException 版本冲突抛出异常
     */
    private void checkVersionConflict(Exception e) throws ConflictVersionException {
        if (!(e instanceof ResponseException responseException)) return;
        if (responseException.getResponse().getStatusLine().getStatusCode() == 409) {
            throw new ConflictVersionException(e);
        }
    }
}
