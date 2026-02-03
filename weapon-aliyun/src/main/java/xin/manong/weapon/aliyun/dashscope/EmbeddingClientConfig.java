package xin.manong.weapon.aliyun.dashscope;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Embedding客户端配置
 *
 * @author frankcl
 * @date 2026-02-03 11:33:28
 */
public class EmbeddingClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingClientConfig.class);

    public String baseURL;

    /**
     * 检测有效性
     */
    public void check() {
        if (StringUtils.isEmpty(baseURL)) {
            logger.error("BaseURL is empty");
            throw new IllegalArgumentException("BaseURL is empty");
        }
    }
}
