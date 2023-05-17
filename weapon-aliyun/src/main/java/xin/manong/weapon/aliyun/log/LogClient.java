package xin.manong.weapon.aliyun.log;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.response.GetLogsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.rebuild.RebuildManager;
import xin.manong.weapon.base.rebuild.Rebuildable;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;
import xin.manong.weapon.base.secret.DynamicSecret;

import java.util.List;

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
        if (!build()) throw new RuntimeException("rebuild log client failed");
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
        return true;
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
