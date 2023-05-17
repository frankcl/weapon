package xin.manong.weapon.aliyun.log;

import com.aliyun.openservices.aliyun.log.producer.Callback;
import com.aliyun.openservices.aliyun.log.producer.Result;
import com.aliyun.openservices.log.common.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志推送回调
 *
 * @author frankcl
 * @date 2023-05-17 18:36:05
 */
public class LogPushCallback implements Callback {

    private static final Logger logger = LoggerFactory.getLogger(LogPushCallback.class);

    private String project;
    private String logStore;
    private LogItem logItem;

    public LogPushCallback(String project, String logStore, LogItem logItem) {
        this.project = project;
        this.logStore = logStore;
        this.logItem = logItem;
    }

    @Override
    public void onCompletion(Result result) {
        if (result.isSuccessful()) return;
        logger.warn("push record[{}] failed for project[{}] and log store[{}]",
                logItem.ToJsonString(), project, logStore);
    }
}
