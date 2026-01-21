package xin.manong.weapon.base.elasticsearch;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * ES客户端配置
 *
 * @author frankcl
 * @date 2025-09-11 12:25:15
 */
@Data
public class ElasticSearchClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchClientConfig.class);

    public String serverURL;
    public String apiKey;

    /**
     * 验证有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(serverURL)) {
            logger.error("Server url is empty");
            return false;
        }
        try {
            new URL(serverURL);
        } catch (MalformedURLException e) {
            logger.error("Server url:{} is invalid", serverURL);
            return false;
        }
        return true;
    }
}
