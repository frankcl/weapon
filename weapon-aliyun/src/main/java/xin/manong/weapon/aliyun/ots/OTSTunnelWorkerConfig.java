package xin.manong.weapon.aliyun.ots;

import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OTS通道worker配置
 *
 * @author frankcl
 * @date 2022-08-04 23:08:14
 */
public class OTSTunnelWorkerConfig {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelWorkerConfig.class);

    private final static int DEFAULT_CONSUME_THREAD_NUM = 5;
    private final static int DEFAULT_HEARTBEAT_INTERVAL_SEC = 30;
    private final static int DEFAULT_MAX_CONSUME_DELAY_MS = 60000;
    private final static int DEFAULT_MAX_RETRY_INTERVAL_MS = 2000;
    private final static int DEFAULT_MAX_CHANNEL_PARALLEL = -1;
    private final static int MAX_CONSUME_THREAD_NUM = 32;

    public int consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
    public int heartBeatIntervalSec = DEFAULT_HEARTBEAT_INTERVAL_SEC;
    public int maxRetryIntervalMs = DEFAULT_MAX_RETRY_INTERVAL_MS;
    public int maxChannelParallel = DEFAULT_MAX_CHANNEL_PARALLEL;
    public long maxConsumeDelayMs = DEFAULT_MAX_CONSUME_DELAY_MS;
    public String table;
    public String tunnel;
    public IChannelProcessor channelProcessor;

    /**
     * 检测OTS通道worker配置有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (channelProcessor == null) {
            logger.error("channel processor is null");
            return false;
        }
        if (StringUtils.isEmpty(table)) {
            logger.error("table is empty");
            return false;
        }
        if (StringUtils.isEmpty(tunnel)) {
            logger.error("tunnel is empty");
            return false;
        }
        if (consumeThreadNum <= 0) consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
        if (heartBeatIntervalSec <= 0) heartBeatIntervalSec = DEFAULT_HEARTBEAT_INTERVAL_SEC;
        if (maxRetryIntervalMs <= 0) maxRetryIntervalMs = DEFAULT_MAX_RETRY_INTERVAL_MS;
        if (maxConsumeDelayMs <= 0) maxConsumeDelayMs = DEFAULT_MAX_CONSUME_DELAY_MS;
        if (consumeThreadNum > MAX_CONSUME_THREAD_NUM) consumeThreadNum = MAX_CONSUME_THREAD_NUM;
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof OTSTunnelWorkerConfig)) return false;
        OTSTunnelWorkerConfig workerConfig = (OTSTunnelWorkerConfig) object;
        if (workerConfig.tunnel == null && tunnel != null) return false;
        if (workerConfig.tunnel != null && tunnel == null) return false;
        if (workerConfig.table == null && table != null) return false;
        if (workerConfig.table != null && table == null) return false;
        if (workerConfig.table == table && workerConfig.tunnel == tunnel) return true;
        return workerConfig.tunnel.equals(tunnel) && workerConfig.table.equals(table);
    }

    @Override
    public int hashCode() {
        int hash = table == null ? 0 : table.hashCode();
        hash = tunnel == null ? hash : hash * 31 + tunnel.hashCode();
        return hash;
    }
}
