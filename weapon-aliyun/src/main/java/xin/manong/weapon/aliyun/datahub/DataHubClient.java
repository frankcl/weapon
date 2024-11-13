package xin.manong.weapon.aliyun.datahub;

import com.aliyun.datahub.DatahubClient;
import com.aliyun.datahub.DatahubConfiguration;
import com.aliyun.datahub.auth.AliyunAccount;
import com.aliyun.datahub.exception.DatahubClientException;
import com.aliyun.datahub.model.GetTopicResult;
import com.aliyun.datahub.model.PutRecordsResult;
import com.aliyun.datahub.model.RecordEntry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.rebuild.RebuildManager;
import xin.manong.weapon.base.rebuild.Rebuildable;
import xin.manong.weapon.aliyun.secret.DynamicSecret;

import java.util.List;

/**
 * DataHub客户端
 *
 * @author frankcl
 * @date 2023-07-06 14:33:01
 */
public class DataHubClient implements Rebuildable {

    private static final Logger logger = LoggerFactory.getLogger(DataHubClient.class);

    private final DataHubClientConfig config;
    private DatahubClient client;

    public DataHubClient(DataHubClientConfig config) {
        this.config = config;
        if (this.config == null || !this.config.check()) throw new RuntimeException("data hub client config is invalid");
        build();
        if (this.config.dynamic) RebuildManager.register(this);
    }

    /**
     * 关闭客户端实例
     */
    public void close() {
        logger.info("data hub client is closing ...");
        if (config.dynamic) RebuildManager.unregister(this);
        logger.info("data hub client has been closed");
    }

    @Override
    public void rebuild() {
        logger.info("data hub client is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("secret is not changed, ignore data hub client rebuilding");
            return;
        }
        config.aliyunSecret.accessKey = DynamicSecret.accessKey;
        config.aliyunSecret.secretKey = DynamicSecret.secretKey;
        build();
        logger.info("data hub client rebuild success");
    }

    /**
     * 构建实例
     */
    private void build() {
        AliyunAccount account = new AliyunAccount(config.aliyunSecret.accessKey, config.aliyunSecret.secretKey);
        DatahubConfiguration conf = new DatahubConfiguration(account, config.endpoint);
        client = new DatahubClient(conf);
    }

    /**
     * 写入数据
     *
     * @param project 项目名称
     * @param topic 专题名称
     * @param recordEntries 数据列表
     * @return 成功返回true，否则返回false
     */
    public boolean putRecords(String project, String topic, List<RecordEntry> recordEntries) {
        if (StringUtils.isEmpty(project)) {
            logger.error("project is empty");
            return false;
        }
        if (StringUtils.isEmpty(topic)) {
            logger.error("topic is empty");
            return false;
        }
        if (recordEntries == null || recordEntries.isEmpty()) {
            logger.warn("record entries are empty");
            return false;
        }
        PutRecordsResult result = client.putRecords(project, topic, recordEntries, config.retryCnt);
        return result.getFailedRecordCount() == 0;
    }

    /**
     * 获取专题信息
     *
     * @param project 项目名称
     * @param topic 专题名称
     * @return 专题信息
     * @throws DatahubClientException 异常
     */
    public GetTopicResult getTopicInfo(String project, String topic) throws DatahubClientException {
        if (StringUtils.isEmpty(project)) throw new DatahubClientException("project is empty");
        if (StringUtils.isEmpty(topic)) throw new DatahubClientException("topic is empty");
        return client.getTopic(project, topic);
    }
}
