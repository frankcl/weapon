package xin.manong.weapon.aliyun.ons;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 订阅关系
 *
 * @author frankcl
 * @date 2022-11-01 19:21:44
 */
public class Subscribe {

    private final static Logger logger = LoggerFactory.getLogger(Subscribe.class);

    private final static String DEFAULT_TAGS = "*";

    public String topic;
    public String tags;

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
        if (StringUtils.isEmpty(tags)) tags = DEFAULT_TAGS;
        return true;
    }
}
