package xin.manong.weapon.aliyun.ons;

import com.aliyun.openservices.ons.api.MessageListener;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 订阅关系
 *
 * @author frankcl
 * @date 2022-11-01 19:21:44
 */
@Data
public class Subscribe {

    private final static Logger logger = LoggerFactory.getLogger(Subscribe.class);

    private final static String DEFAULT_TAGS = "*";

    public String topic;
    public String tags;
    /* 消息监听器bean名称，支持spring boot */
    public String listenerName;
    public MessageListener listener;

    public Subscribe() {
    }

    public Subscribe(String topic) {
        this.topic = topic;
    }

    public Subscribe(String topic, String tags) {
        this.topic = topic;
        this.tags = tags;
    }

    /**
     * 检测有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(topic)) {
            logger.error("consume topic is empty");
            return false;
        }
        if (listener == null) {
            logger.error("message listener is not config");
            return false;
        }
        if (StringUtils.isEmpty(tags)) tags = DEFAULT_TAGS;
        return true;
    }
}
