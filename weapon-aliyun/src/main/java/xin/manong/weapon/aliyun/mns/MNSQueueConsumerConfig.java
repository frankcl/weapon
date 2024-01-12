package xin.manong.weapon.aliyun.mns;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MNS队列消费配置
 *
 * @author frankcl
 * @date 2024-01-12 11:10:29
 */
@Data
@ConfigurationProperties(prefix = "weapon.aliyun.mns.consumer")
public class MNSQueueConsumerConfig {

    private static final Logger logger = LoggerFactory.getLogger(MNSQueueConsumerConfig.class);

    private static final int DEFAULT_THREAD_NUM = 1;

    public int threadNum = DEFAULT_THREAD_NUM;
    public String clientName;
    public String queueName;
    public String processorName;

    /**
     * 检测合法性
     *
     * @return 如果合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(queueName)) {
            logger.error("queue name is empty");
            return false;
        }
        if (threadNum <= 0) threadNum = DEFAULT_THREAD_NUM;
        return true;
    }
}
