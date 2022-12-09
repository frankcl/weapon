package xin.manong.weapon.aliyun.ots;

import com.alicloud.openservices.tablestore.ClientConfiguration;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.TableStoreException;
import com.alicloud.openservices.tablestore.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.common.RebuildManager;
import xin.manong.weapon.aliyun.common.Rebuildable;
import xin.manong.weapon.aliyun.secret.DynamicSecret;
import xin.manong.weapon.base.record.KVRecord;

import java.util.*;

/**
 * OTS客户端
 *
 * @author frankcl
 * @create 2019-05-28 20:29
 */
public class OTSClient implements Rebuildable {

    private final static Logger logger = LoggerFactory.getLogger(OTSClient.class);

    private final static String ERR_CODE_CONDITION_CHECK_FAIL = "OTSConditionCheckFail";

    private OTSClientConfig config;
    private SyncClient syncClient;

    public OTSClient(OTSClientConfig config) {
        this.config = config;
        build();
        RebuildManager.register(this);
    }

    /**
     * 关闭OTS客户端
     */
    public void close() {
        logger.info("OTS client is closing ...");
        RebuildManager.unregister(this);
        if (syncClient != null) syncClient.shutdown();
        logger.info("OTS client has been closed");
    }

    @Override
    public void rebuild() {
        logger.info("OTS client is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("secret is not changed, ignore OTS client rebuilding");
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
        if (StringUtils.isEmpty(tableName)) throw new RuntimeException("table is empty");
        PrimaryKey startPrimaryKey = OTSConverter.convertPrimaryKey(startKeyMap);
        PrimaryKey endPrimaryKey = OTSConverter.convertPrimaryKey(endKeyMap);
        if (startKeyMap.size() != endKeyMap.size() ||
                !startKeyMap.keySet().containsAll(endKeyMap.keySet())) {
            throw new RuntimeException("start keys and end keys are not consistent");
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
        if (StringUtils.isEmpty(tableName)) throw new RuntimeException("table is empty");
        PrimaryKey startPrimaryKey = OTSConverter.convertPrimaryKey(startKeyMap);
        PrimaryKey endPrimaryKey = OTSConverter.convertPrimaryKey(endKeyMap);
        if (startKeyMap.size() != endKeyMap.size() ||
                !startKeyMap.keySet().containsAll(endKeyMap.keySet())) {
            throw new RuntimeException("start keys and end keys are not consistent");
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
        if (StringUtils.isEmpty(tableName)) throw new RuntimeException("table is empty");
        PrimaryKey primaryKey = OTSConverter.convertPrimaryKey(keyMap);
        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(tableName, primaryKey);
        criteria.setMaxVersions(1);
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                GetRowResponse response = syncClient.getRow(new GetRowRequest(criteria));
                Row row = response.getRow();
                return row == null ? null : OTSConverter.convertRecord(row);
            } catch (Exception e) {
                logger.error("get failed for table[{}] and primary keys[{}], retry {} times",
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
        if (StringUtils.isEmpty(tableName)) throw new RuntimeException("table is empty");
        PrimaryKey primaryKey = OTSConverter.convertPrimaryKey(keyMap);
        RowDeleteChange change = new RowDeleteChange(tableName, primaryKey);
        if (condition != null) change.setCondition(condition);
        try {
            DeleteRowResponse response = syncClient.deleteRow(new DeleteRowRequest(change));
            return response == null ? OTSStatus.FAIL : OTSStatus.SUCCESS;
        } catch (Exception e) {
            if (e instanceof TableStoreException && ERR_CODE_CONDITION_CHECK_FAIL.equals(
                    ((TableStoreException) e).getErrorCode())) {
                logger.warn("delete condition check failed for table[{}] and primary keys[{}]",
                        tableName, primaryKey.toString());
                return OTSStatus.CHECK_CONDITION_FAIL;
            }
            logger.error("delete failed for table[{}] and primary keys[{}]", tableName, primaryKey.toString());
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
        if (StringUtils.isEmpty(tableName)) throw new RuntimeException("table name is empty");
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
                    logger.debug("put condition failed for table[{}] and primary keys[{}]",
                            tableName, record.getPrimaryKey().toString());
                    return OTSStatus.CHECK_CONDITION_FAIL;
                }
                logger.error("put failed for table[{}] and primary keys[{}], retry {} times",
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
        if (StringUtils.isEmpty(tableName)) throw new RuntimeException("table is empty");
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
                    logger.warn("update condition check failed for table[{}] and primary keys[{}]",
                            tableName, record.getPrimaryKey().toString());
                    return OTSStatus.CHECK_CONDITION_FAIL;
                }
                logger.error("update failed for table[{}] and primary keys[{}], retry {} times",
                        tableName, record.getPrimaryKey().toString(), i + 1);
                logger.error(e.getMessage(), e);
            }
        }
        return OTSStatus.FAIL;
    }
}
