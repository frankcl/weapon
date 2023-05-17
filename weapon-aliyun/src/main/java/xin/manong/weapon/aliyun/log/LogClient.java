package xin.manong.weapon.aliyun.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyun.openservices.aliyun.log.producer.*;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.response.GetLogsResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.rebuild.RebuildManager;
import xin.manong.weapon.base.rebuild.Rebuildable;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;
import xin.manong.weapon.base.secret.DynamicSecret;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 阿里云SLS客户端
 *
 * @author frankcl
 * @date 2023-05-17 16:58:58
 */
public class LogClient implements Rebuildable {

    private static final Logger logger = LoggerFactory.getLogger(LogClient.class);

    private LogClientConfig config;
    private Client client;
    private Producer producer;
    private Set<String> projects;

    public LogClient(LogClientConfig config) {
        this.config = config;
    }

    /**
     * 初始化
     *
     * @return 成功返回true，否则返回false
     */
    public boolean init() {
        logger.info("log client is init ...");
        if (!config.check()) return false;
        if (!build()) return false;
        if (config.dynamic) RebuildManager.register(this);
        logger.info("log client has finished init");
        return true;
    }

    /**
     * 销毁
     */
    public void destroy() {
        logger.info("log client is destroying ...");
        if (config.dynamic) RebuildManager.unregister(this);
        try {
            if (producer != null) producer.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (client != null) client.shutdown();
        logger.info("log client has been destroyed");
    }

    @Override
    public void rebuild() {
        logger.info("log client is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("secret is not changed, ignore log client rebuilding");
            return;
        }
        config.aliyunSecret.accessKey = DynamicSecret.accessKey;
        config.aliyunSecret.secretKey = DynamicSecret.secretKey;
        Client prevClient = client;
        Producer prevProducer = producer;
        if (!build()) throw new RuntimeException("rebuild log client failed");
        try {
            if (prevProducer != null) prevProducer.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (prevClient != null) prevClient.shutdown();
        logger.info("log client rebuild success");
    }

    /**
     * 构建LogClient
     *
     * @return 成功返回true，否则返回false
     */
    private boolean build() {
        client = new Client(config.endpoint, config.aliyunSecret.accessKey,
                config.aliyunSecret.secretKey);
        producer = new LogProducer(new ProducerConfig());
        projects = new HashSet<>();
        return true;
    }

    /**
     * 构建project连接客户端
     *
     * @param project
     */
    private void buildProjectClient(String project) {
        if (projects.contains(project)) return;
        synchronized (this) {
            if (projects.contains(project)) return;
            producer.putProjectConfig(new ProjectConfig(project, config.endpoint,
                    config.aliyunSecret.accessKey, config.aliyunSecret.secretKey));
            projects.add(project);
        }
    }

    /**
     * 转换KVRecord为LogItem
     *
     * @param kvRecord
     * @return LogItem
     */
    private LogItem convert(KVRecord kvRecord) {
        LogItem logItem = new LogItem();
        for (Map.Entry<String, Object> entry : kvRecord.getFieldMap().entrySet()) {
            Object value = entry.getValue();
            logItem.PushBack(entry.getKey(), value instanceof JSON ? JSON.toJSONString(
                    value, SerializerFeature.DisableCircularReferenceDetect) : value.toString());
        }
        return logItem;
    }

    /**
     * 推送日志
     *
     * @param project project
     * @param logStore log store
     * @param kvRecords 推送数据
     */
    public void push(String project, String logStore, KVRecords kvRecords) {
        if (StringUtils.isEmpty(project)) {
            logger.error("project is empty");
            return;
        }
        if (StringUtils.isEmpty(logStore)) {
            logger.error("log store is empty");
            return;
        }
        if (kvRecords == null || kvRecords.isEmpty()) {
            logger.error("push records are empty");
            return;
        }
        buildProjectClient(project);
        for (int i = 0; i < kvRecords.getRecordCount(); i++) {
            KVRecord kvRecord = kvRecords.getRecord(i);
            LogItem logItem = convert(kvRecord);
            LogPushCallback callback = new LogPushCallback(project, logStore, logItem);
            try {
                producer.send(project, logStore, logItem, callback);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * 搜索日志
     *
     * @param request 搜索请求
     * @return 搜索响应
     */
    public LogSearchResponse search(LogSearchRequest request) {
        if (request == null || !request.check()) {
            logger.error("search request is not valid");
            return LogSearchResponse.buildError("search request is not valid");
        }
        GetLogsRequest getLogsRequest = new GetLogsRequest(request.project, request.logStore,
                (int) (request.startTime / 1000), (int) (request.stopTime / 1000), request.topic,
                request.query, request.offset, request.lines, request.reverse);
        try {
            GetLogsResponse getLogsResponse = client.GetLogs(getLogsRequest);
            if (getLogsResponse == null) {
                logger.error("search response is null");
                return LogSearchResponse.buildError("search response is null");
            }
            KVRecords kvRecords = new KVRecords();
            List<QueriedLog> queriedLogs = getLogsResponse.getLogs();
            for (QueriedLog queriedLog : queriedLogs) {
                KVRecord kvRecord = new KVRecord();
                for (LogContent logContent : queriedLog.mLogItem.mContents) {
                    kvRecord.put(logContent.mKey, logContent.mValue);
                }
                kvRecords.addRecord(kvRecord);
            }
            return LogSearchResponse.buildOK(kvRecords, getLogsResponse.getProcessedRow());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return LogSearchResponse.buildError(e.getMessage());
        }
    }
}
