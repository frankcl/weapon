package xin.manong.weapon.aliyun.ots;

import com.alicloud.openservices.tablestore.ClientConfiguration;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.TableStoreException;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.filter.ColumnPaginationFilter;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.rebuild.RebuildManager;
import xin.manong.weapon.base.rebuild.Rebuildable;
import xin.manong.weapon.base.record.KVRecords;
import xin.manong.weapon.aliyun.secret.DynamicSecret;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.util.EqualsUtil;

import java.util.*;

/**
 * OTS客户端
 *
 * @author frankcl
 * @date 2019-05-28 20:29
 */
public class OTSClient implements Rebuildable {

    private final static Logger logger = LoggerFactory.getLogger(OTSClient.class);

    private final static String ERR_CODE_CONDITION_CHECK_FAIL = "OTSConditionCheckFail";

    private final OTSClientConfig config;
    private SyncClient syncClient;

    public OTSClient(OTSClientConfig config) {
        this.config = config;
        if (!this.config.check()) throw new IllegalArgumentException("OTS client config is invalid");
        build();
        if (this.config.dynamic) RebuildManager.register(this);
    }

    /**
     * 关闭OTS客户端
     */
    public void close() {
        logger.info("OTS client is closing ...");
        if (config.dynamic) RebuildManager.unregister(this);
        if (syncClient != null) syncClient.shutdown();
        logger.info("OTS client has been closed");
    }

    @Override
    public void rebuild() {
        logger.info("OTS client is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("Secret is not changed, ignore OTS client rebuilding");
            return;
        }
        config.aliyunSecret.accessKey = DynamicSecret.accessKey;
        config.aliyunSecret.secretKey = DynamicSecret.secretKey;
        SyncClient prevClient = syncClient;
        build();
        if (prevClient != null) prevClient.shutdown();
        logger.info("OTS client rebuild success");
    }

    /**
     * 构建实例
     */
    private void build() {
        ClientConfiguration clientConf = new ClientConfiguration();
        clientConf.setConnectionTimeoutInMillisecond(config.connectionTimeoutMs);
        clientConf.setSocketTimeoutInMillisecond(config.socketTimeoutMs);
        clientConf.setConnectionRequestTimeoutInMillisecond(config.connectionRequestTimeoutMs);
        syncClient = new SyncClient(config.endpoint, config.aliyunSecret.accessKey,
                config.aliyunSecret.secretKey, config.instance, clientConf);
    }

    /**
     * 按照范围迭代数据
     *
     * @param tableName 表名
     * @param startKeyMap 起始主键
     * @param endKeyMap 结束主键
     * @return 数据迭代器
     */
    public RecordIterator rangeIterator(String tableName, Map<String, Object> startKeyMap,
                                        Map<String, Object> endKeyMap) {
        if (StringUtils.isEmpty(tableName)) throw new IllegalArgumentException("Table is empty");
        PrimaryKey startPrimaryKey = OTSConverter.convertPrimaryKey(startKeyMap);
        PrimaryKey endPrimaryKey = OTSConverter.convertPrimaryKey(endKeyMap);
        if (startKeyMap.size() != endKeyMap.size() ||
                !startKeyMap.keySet().containsAll(endKeyMap.keySet())) {
            throw new IllegalArgumentException("Start keys and end keys are not consistent");
        }
        RangeIteratorParameter rangeIteratorParameter = new RangeIteratorParameter(tableName);
        rangeIteratorParameter.setInclusiveStartPrimaryKey(startPrimaryKey);
        rangeIteratorParameter.setExclusiveEndPrimaryKey(endPrimaryKey);
        rangeIteratorParameter.setMaxVersions(1);
        Iterator<Row> iterator = syncClient.createRangeIterator(rangeIteratorParameter);
        return new RecordIterator(iterator);
    }

    /**
     * 获取范围数据
     *
     * @param tableName 表名
     * @param startKeyMap 起始主键
     * @param endKeyMap 结束主键
     * @return 数据列表
     */
    public List<KVRecord> getRange(String tableName, Map<String, Object> startKeyMap,
                                   Map<String, Object> endKeyMap) {
        if (StringUtils.isEmpty(tableName)) throw new IllegalArgumentException("Table is empty");
        PrimaryKey startPrimaryKey = OTSConverter.convertPrimaryKey(startKeyMap);
        PrimaryKey endPrimaryKey = OTSConverter.convertPrimaryKey(endKeyMap);
        if (startKeyMap.size() != endKeyMap.size() ||
                !startKeyMap.keySet().containsAll(endKeyMap.keySet())) {
            throw new IllegalArgumentException("Start keys and end keys are not consistent");
        }
        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(tableName);
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(startPrimaryKey);
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(endPrimaryKey);
        rangeRowQueryCriteria.setMaxVersions(1);
        List<KVRecord> kvRecords = new ArrayList<>();
        while (true) {
            GetRangeResponse response = syncClient.getRange(new GetRangeRequest(rangeRowQueryCriteria));
            for (Row row : response.getRows()) {
                if (row == null) continue;
                KVRecord kvRecord = OTSConverter.convertRecord(row);
                kvRecords.add(kvRecord);
            }
            PrimaryKey nextPrimaryKey = response.getNextStartPrimaryKey();
            if (nextPrimaryKey == null) break;
            rangeRowQueryCriteria.setInclusiveStartPrimaryKey(nextPrimaryKey);
        }
        return kvRecords;
    }

    /**
     * 获取数据
     *
     * @param tableName 表名
     * @param keyMap 主键映射
     * @return 如果存在返回数据，否则返回null
     */
    public KVRecord get(String tableName, Map<String, Object> keyMap) {
        if (StringUtils.isEmpty(tableName)) throw new IllegalArgumentException("Table is empty");
        PrimaryKey primaryKey = OTSConverter.convertPrimaryKey(keyMap);
        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(tableName, primaryKey);
        criteria.setMaxVersions(1);
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                GetRowResponse response = syncClient.getRow(new GetRowRequest(criteria));
                Row row = response.getRow();
                return row == null ? null : OTSConverter.convertRecord(row);
            } catch (Exception e) {
                logger.error("Get failed for table:{} and primary keys:{}, retry {} times",
                        tableName, primaryKey.toString(), i + 1);
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 根据key删除数据
     *
     * @param tableName 表名
     * @param keyMap 主键
     * @param condition 删除条件，无条件删除使用null
     * @return OTS状态
     */
    public OTSStatus delete(String tableName, Map<String, Object> keyMap, Condition condition) {
        if (StringUtils.isEmpty(tableName)) throw new IllegalArgumentException("Table is empty");
        PrimaryKey primaryKey = OTSConverter.convertPrimaryKey(keyMap);
        RowDeleteChange change = new RowDeleteChange(tableName, primaryKey);
        if (condition != null) change.setCondition(condition);
        try {
            DeleteRowResponse response = syncClient.deleteRow(new DeleteRowRequest(change));
            return response == null ? OTSStatus.FAIL : OTSStatus.SUCCESS;
        } catch (Exception e) {
            if (e instanceof TableStoreException && ERR_CODE_CONDITION_CHECK_FAIL.equals(
                    ((TableStoreException) e).getErrorCode())) {
                logger.warn("Delete condition check failed for table:{} and primary keys:{}",
                        tableName, primaryKey.toString());
                return OTSStatus.CHECK_CONDITION_FAIL;
            }
            logger.error("Delete failed for table:{} and primary keys:{}", tableName, primaryKey.toString());
            logger.error(e.getMessage(), e);
            return OTSStatus.FAIL;
        }
    }

    /**
     * 添加数据
     *
     * @param tableName 表名
     * @param kvRecord 数据
     * @param condition 添加条件，无条件添加传递null
     * @return OTS状态
     */
    public OTSStatus put(String tableName, KVRecord kvRecord, Condition condition) {
        if (StringUtils.isEmpty(tableName)) throw new IllegalArgumentException("Table name is empty");
        Row record = OTSConverter.convertRecord(kvRecord);
        RowPutChange change = new RowPutChange(tableName, record.getPrimaryKey());
        change.addColumns(record.getColumns());
        if (condition != null) change.setCondition(condition);
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                PutRowResponse response = syncClient.putRow(new PutRowRequest(change));
                return response == null ? OTSStatus.FAIL : OTSStatus.SUCCESS;
            } catch (Exception e) {
                if (e instanceof TableStoreException && ERR_CODE_CONDITION_CHECK_FAIL.equals(
                        ((TableStoreException) e).getErrorCode())) {
                    logger.debug("Put condition failed for table:{} and primary keys:{}",
                            tableName, record.getPrimaryKey().toString());
                    return OTSStatus.CHECK_CONDITION_FAIL;
                }
                logger.error("Put failed for table:{} and primary keys:{}, retry {} times",
                        tableName, record.getPrimaryKey().toString(), i + 1);
                logger.error(e.getMessage(), e);
            }
        }
        return OTSStatus.FAIL;
    }

    /**
     * 更新数据
     *
     * @param tableName 表名
     * @param kvRecord 更新数据
     * @param condition 更新条件，无条件更新使用null
     * @return OTS状态
     */
    public OTSStatus update(String tableName, KVRecord kvRecord, Condition condition) {
        if (StringUtils.isEmpty(tableName)) throw new IllegalArgumentException("Table is empty");
        Row record = OTSConverter.convertRecord(kvRecord);
        RowUpdateChange change = new RowUpdateChange(tableName, record.getPrimaryKey());
        change.put(Arrays.asList(record.getColumns()));
        if (condition != null) change.setCondition(condition);
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                UpdateRowResponse response = syncClient.updateRow(new UpdateRowRequest(change));
                return response == null ? OTSStatus.FAIL : OTSStatus.SUCCESS;
            } catch (Exception e) {
                if (e instanceof TableStoreException && ERR_CODE_CONDITION_CHECK_FAIL.equals(
                        ((TableStoreException) e).getErrorCode())) {
                    logger.warn("Update condition check failed for table:{} and primary keys:{}",
                            tableName, record.getPrimaryKey().toString());
                    return OTSStatus.CHECK_CONDITION_FAIL;
                }
                logger.error("Update failed for table:{} and primary keys:{}, retry {} times",
                        tableName, record.getPrimaryKey().toString(), i + 1);
                logger.error(e.getMessage(), e);
            }
        }
        return OTSStatus.FAIL;
    }

    /**
     * 批量获取数据
     *
     * @param tableName 表名
     * @param keyMaps 主键列表
     * @return 批处理响应
     */
    public BatchResponse batchGet(String tableName, List<Map<String, Object>> keyMaps) {
        if (StringUtils.isEmpty(tableName)) throw new IllegalArgumentException("Table is empty");
        if (keyMaps == null || keyMaps.isEmpty()) throw new IllegalArgumentException("Key map list are empty");
        MultiRowQueryCriteria multiRowQueryCriteria = new MultiRowQueryCriteria(tableName);
        for (Map<String, Object> keyMap : keyMaps) {
            PrimaryKey primaryKey = OTSConverter.convertPrimaryKey(keyMap);
            multiRowQueryCriteria.addRow(primaryKey);
        }
        multiRowQueryCriteria.setMaxVersions(1);
        ColumnPaginationFilter filter = new ColumnPaginationFilter(128);
        multiRowQueryCriteria.setFilter(filter);
        BatchGetRowRequest request = new BatchGetRowRequest();
        request.addMultiRowQueryCriteria(multiRowQueryCriteria);
        BatchResponse batchResponse = new BatchResponse();
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                BatchGetRowResponse response = syncClient.batchGetRow(request);
                List<BatchGetRowResponse.RowResult> results = response.getSucceedRows();
                for (BatchGetRowResponse.RowResult result : results) {
                    batchResponse.addRecordResult(buildGetResult(result, request));
                }
                if (response.isAllSucceed()) return batchResponse;
                for (BatchGetRowResponse.RowResult result : response.getFailedRows()) {
                    logger.warn("Batch get record:{} failed, cause:{}, retry {} times",
                            request.getPrimaryKey(result.getTableName(), result.getIndex()),
                            result.getError().toString(), i + 1);
                    if (i >= config.retryCnt) batchResponse.addRecordResult(buildGetResult(result, request));
                }
                request = request.createRequestForRetry(response.getFailedRows());
            } catch (Exception e) {
                logger.error("Batch get records failed for table:{}, retry {} times", tableName, i + 1);
                logger.error(e.getMessage(), e);
                if (i >= config.retryCnt) processUnhandledKeyMaps(batchResponse, keyMaps);
            }
        }
        return batchResponse;
    }

    /**
     * 批量写入数据
     *
     * @param tableName 表名
     * @param kvRecords 写入数据列表
     * @return 批处理响应
     */
    public BatchResponse batchPut(String tableName, List<KVRecord> kvRecords) {
        if (StringUtils.isEmpty(tableName)) throw new IllegalArgumentException("Table is empty");
        if (kvRecords == null || kvRecords.isEmpty()) throw new IllegalArgumentException("Put records are empty");
        List<Row> records = new ArrayList<>();
        for (KVRecord kvRecord : kvRecords) records.add(OTSConverter.convertRecord(kvRecord));
        BatchWriteRowRequest request = new BatchWriteRowRequest();
        for (Row record : records) {
            RowPutChange putChange = new RowPutChange(tableName, record.getPrimaryKey());
            for (Column column : record.getColumns()) putChange.addColumn(column);
            request.addRowChange(putChange);
        }
        BatchResponse batchResponse = new BatchResponse();
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                BatchWriteRowResponse response = syncClient.batchWriteRow(request);
                List<BatchWriteRowResponse.RowResult> results = response.getSucceedRows();
                for (BatchWriteRowResponse.RowResult result : results) {
                    batchResponse.addRecordResult(buildWriteResult(result, request, kvRecords));
                }
                if (response.isAllSucceed()) return batchResponse;
                for (BatchWriteRowResponse.RowResult result : response.getFailedRows()) {
                    logger.warn("Batch put record:{} failed, cause:{}, retry {} times",
                            request.getRowChange(result.getTableName(), result.getIndex()).getPrimaryKey(),
                            result.getError().toString(), i + 1);
                    if (i >= config.retryCnt) batchResponse.addRecordResult(buildWriteResult(result, request, kvRecords));
                }
                request = request.createRequestForRetry(response.getFailedRows());
            } catch (Exception e) {
                logger.error("Batch put records failed for table:{}, retry {} times", tableName, i + 1);
                logger.error(e.getMessage(), e);
                if (i >= config.retryCnt) processUnhandledRecords(batchResponse, kvRecords);
            }
        }
        return batchResponse;
    }

    /**
     * 批量更新数据
     *
     * @param tableName 表名
     * @param kvRecords 更新数据列表
     * @return 批处理响应
     */
    public BatchResponse batchUpdate(String tableName, List<KVRecord> kvRecords) {
        if (StringUtils.isEmpty(tableName)) throw new IllegalArgumentException("Table is empty");
        if (kvRecords == null || kvRecords.isEmpty()) throw new IllegalArgumentException("Update records are empty");
        List<Row> records = new ArrayList<>();
        for (KVRecord kvRecord : kvRecords) records.add(OTSConverter.convertRecord(kvRecord));
        BatchWriteRowRequest request = new BatchWriteRowRequest();
        for (Row record : records) {
            RowUpdateChange updateChange = new RowUpdateChange(tableName, record.getPrimaryKey());
            for (Column column : record.getColumns()) updateChange.put(column);
            request.addRowChange(updateChange);
        }
        BatchResponse batchResponse = new BatchResponse();
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                BatchWriteRowResponse response = syncClient.batchWriteRow(request);
                List<BatchWriteRowResponse.RowResult> results = response.getSucceedRows();
                for (BatchWriteRowResponse.RowResult result : results) {
                    batchResponse.addRecordResult(buildWriteResult(result, request, kvRecords));
                }
                if (response.isAllSucceed()) return batchResponse;
                for (BatchWriteRowResponse.RowResult result : response.getFailedRows()) {
                    logger.warn("Batch update record:{} failed, cause:{}, retry {} times",
                            request.getRowChange(result.getTableName(), result.getIndex()).getPrimaryKey(),
                            result.getError().toString(), i + 1);
                    if (i >= config.retryCnt) batchResponse.addRecordResult(buildWriteResult(result, request, kvRecords));
                }
                request = request.createRequestForRetry(response.getFailedRows());
            } catch (Exception e) {
                logger.error("Batch update records failed for table:{}, retry {} times", tableName, i + 1);
                logger.error(e.getMessage(), e);
                if (i >= config.retryCnt) processUnhandledRecords(batchResponse, kvRecords);
            }
        }
        return batchResponse;
    }

    /**
     * 批量删除数据
     *
     * @param tableName 表名
     * @param keyMaps 删除主键列表
     * @return 批处理响应
     */
    public BatchResponse batchDelete(String tableName, List<Map<String, Object>> keyMaps) {
        if (StringUtils.isEmpty(tableName)) throw new IllegalArgumentException("Table is empty");
        if (keyMaps == null || keyMaps.isEmpty()) throw new IllegalArgumentException("Delete key map list are empty");
        BatchWriteRowRequest request = new BatchWriteRowRequest();
        for (Map<String, Object> keyMap : keyMaps) {
            PrimaryKey primaryKey = OTSConverter.convertPrimaryKey(keyMap);
            RowDeleteChange deleteChange = new RowDeleteChange(tableName, primaryKey);
            request.addRowChange(deleteChange);
        }
        BatchResponse batchResponse = new BatchResponse();
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                BatchWriteRowResponse response = syncClient.batchWriteRow(request);
                List<BatchWriteRowResponse.RowResult> results = response.getSucceedRows();
                for (BatchWriteRowResponse.RowResult result : results) {
                    batchResponse.addRecordResult(buildWriteResult(result, request));
                }
                if (response.isAllSucceed()) return batchResponse;
                for (BatchWriteRowResponse.RowResult result : response.getFailedRows()) {
                    logger.warn("Batch delete record:{} failed, cause:{}, retry {} times",
                            request.getRowChange(result.getTableName(), result.getIndex()).getPrimaryKey(),
                            result.getError().toString(), i + 1);
                    if (i >= config.retryCnt) batchResponse.addRecordResult(buildWriteResult(result, request));
                }
                request = request.createRequestForRetry(response.getFailedRows());
            } catch (Exception e) {
                logger.error("Batch delete records failed for table:{}, retry {} times", tableName, i + 1);
                logger.error(e.getMessage(), e);
                if (i >= config.retryCnt) processUnhandledKeyMaps(batchResponse, keyMaps);
            }
        }
        return batchResponse;
    }

    /**
     * 数据搜索
     *
     * @param request 搜素请求
     * @return 搜索响应
     */
    public OTSSearchResponse search(OTSSearchRequest request) {
        if (request == null || !request.check()) {
            logger.error("Invalid OTS search request");
            return OTSSearchResponse.buildError("非法搜索请求");
        }
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(request.query);
        searchQuery.setOffset(request.offset);
        searchQuery.setLimit(request.limit);
        searchQuery.setGetTotalCount(true);
        SearchRequest searchRequest = new SearchRequest(request.tableName, request.indexName, searchQuery);
        searchRequest.setTimeoutInMillisecond(request.searchTimeoutMs);
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        if (request.returnColumns == null || request.returnColumns.isEmpty()) columnsToGet.setReturnAll(true);
        else columnsToGet.setColumns(request.returnColumns);
        searchRequest.setColumnsToGet(columnsToGet);

        try {
            SearchResponse searchResponse = syncClient.search(searchRequest);
            if (!searchResponse.isAllSuccess()) return OTSSearchResponse.buildError("搜索失败");
            List<Row> rows = searchResponse.getRows();
            KVRecords kvRecords = new KVRecords();
            if (rows == null) return OTSSearchResponse.buildOK(kvRecords, searchResponse.getTotalCount());
            for (Row row : rows) {
                if (row == null) continue;
                kvRecords.addRecord(OTSConverter.convertRecord(row));
            }
            return OTSSearchResponse.buildOK(kvRecords, searchResponse.getTotalCount());
        } catch (Exception e) {
            logger.error("Search failed for table:{} and index:{}", request.tableName, request.indexName);
            logger.error(e.getMessage(), e);
            return OTSSearchResponse.buildError(String.format("搜索异常:%s", e.getMessage()));
        }
    }

    /**
     * 构建批处理写单行结果
     *
     * @param result 单行批处理写结果
     * @param request 批处理写请求
     * @param kvRecords 处理数据列表
     * @return 批处理写单行结果
     */
    private RecordResult buildWriteResult(BatchWriteRowResponse.RowResult result,
                                          BatchWriteRowRequest request, List<KVRecord> kvRecords) {
        RowChange rowChange = request.getRowChange(result.getTableName(), result.getIndex());
        PrimaryKey primaryKey = rowChange.getPrimaryKey();
        Map<String, Object> keyMap = OTSConverter.convertPrimaryKey(primaryKey);
        String message = result.getError() == null ? "" : result.getError().toString();
        for (KVRecord kvRecord : kvRecords) {
            if (!EqualsUtil.mapEquals(keyMap, kvRecord.getKeyMap())) continue;
            return result.isSucceed() ? RecordResult.buildSuccessResult(kvRecord) :
                    RecordResult.buildFailResult(kvRecord, message);
        }
        KVRecord kvRecord = new KVRecord(new HashSet<>(keyMap.keySet()), new HashMap<>(keyMap));
        return result.isSucceed() ? RecordResult.buildSuccessResult(kvRecord) :
                RecordResult.buildFailResult(kvRecord, message);
    }

    /**
     * 构建批处理写单行结果
     *
     * @param result 单行批处理写结果
     * @param request 批处理写请求
     * @return 批处理写单行结果
     */
    private RecordResult buildWriteResult(BatchWriteRowResponse.RowResult result, BatchWriteRowRequest request) {
        RowChange rowChange = request.getRowChange(result.getTableName(), result.getIndex());
        PrimaryKey primaryKey = rowChange.getPrimaryKey();
        Map<String, Object> keyMap = OTSConverter.convertPrimaryKey(primaryKey);
        String message = result.getError() == null ? "" : result.getError().toString();
        KVRecord kvRecord = new KVRecord(new HashSet<>(keyMap.keySet()), new HashMap<>(keyMap));
        return result.isSucceed() ? RecordResult.buildSuccessResult(kvRecord) :
                RecordResult.buildFailResult(kvRecord, message);
    }

    /**
     * 构建读处理失败数据结果
     *
     * @param result 失败结果
     * @param request 批处理请求
     */
    private RecordResult buildGetResult(BatchGetRowResponse.RowResult result, BatchGetRowRequest request) {
        PrimaryKey primaryKey = request.getPrimaryKey(result.getTableName(), result.getIndex());
        Map<String, Object> keyMap = OTSConverter.convertPrimaryKey(primaryKey);
        String message = result.getError() == null ? "" : result.getError().toString();
        KVRecord kvRecord = result.isSucceed() ?
                (result.getRow() == null ? null : OTSConverter.convertRecord(result.getRow())) :
                new KVRecord(new HashSet<>(keyMap.keySet()), new HashMap<>(keyMap));
        return result.isSucceed() ? RecordResult.buildSuccessResult(kvRecord) :
                RecordResult.buildFailResult(kvRecord, message);
    }

    /**
     * 将未处理数据添加到批处理响应结果中
     *
     * @param batchResponse 批处理结果
     * @param keyMaps 待处理主键列表
     */
    private void processUnhandledKeyMaps(BatchResponse batchResponse, List<Map<String, Object>> keyMaps) {
        for (Map<String, Object> keyMap : keyMaps) {
            if (findRecordResult(keyMap, batchResponse) != null) continue;
            KVRecord kvRecord = new KVRecord(new HashSet<>(keyMap.keySet()), new HashMap<>(keyMap));
            batchResponse.addRecordResult(RecordResult.buildFailResult(kvRecord, "异常发生，数据未处理"));
        }
    }

    /**
     * 将未处理数据添加到批处理响应结果中
     *
     * @param batchResponse 批处理结果
     * @param kvRecords 待处理数据列表
     */
    private void processUnhandledRecords(BatchResponse batchResponse, List<KVRecord> kvRecords) {
        for (KVRecord kvRecord : kvRecords) {
            if (findRecordResult(kvRecord.getKeyMap(), batchResponse) != null) continue;
            batchResponse.addRecordResult(RecordResult.buildFailResult(kvRecord, "异常发生，数据未处理"));
        }
    }

    /**
     * 在批处理结果中寻找与主键一致的结果
     *
     * @param keyMap 主键列表
     * @param batchResponse 批处理结果
     * @return 如果存在返回行结果，否则返回null
     */
    private RecordResult findRecordResult(Map<String, Object> keyMap, BatchResponse batchResponse) {
        for (RecordResult recordResult : batchResponse.recordResults) {
            if (EqualsUtil.mapEquals(keyMap, recordResult.record.getKeyMap())) return recordResult;
        }
        return null;
    }
}
