package xin.manong.weapon.base.kafka;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * kafka消费配置
 *
 * @author frankcl
 * @date 2023-01-05 16:15:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KafkaConsumeConfig extends KafkaAuthSupport {

    private final static Logger logger = LoggerFactory.getLogger(KafkaConsumeConfig.class);

    private final static Integer DEFAULT_CONSUME_THREAD_NUM = 1;

    public Integer consumeThreadNum;
    public Long maxFetchWaitTimeMs;
    public String name;
    public String servers;
    public String groupId;
    public String processorName;
    public List<String> topics;

    /**
     * 检测配置有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (!super.check()) return false;
        if (topics == null || topics.isEmpty()) {
            logger.error("Consume topics are empty");
            return false;
        }
        if (StringUtils.isEmpty(servers)) {
            logger.error("Kafka servers are empty");
            return false;
        }
        if (StringUtils.isEmpty(groupId)) {
            logger.error("Subscribe group id is empty");
            return false;
        }
        if (StringUtils.isEmpty(name)) name = "unknown_consumer";
        if (consumeThreadNum == null || consumeThreadNum <= 0) consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
        topics = new ArrayList<>(new HashSet<>(topics));
        return true;
    }
}
