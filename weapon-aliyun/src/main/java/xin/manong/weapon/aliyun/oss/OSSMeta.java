package xin.manong.weapon.aliyun.oss;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSS元数据信息
 *
 * @author frankcl
 * @date 2023-02-14 16:22:20
 */
public class OSSMeta {

    private final static Logger logger = LoggerFactory.getLogger(OSSMeta.class);

    public String region;
    public String bucket;
    public String key;

    public OSSMeta() {
    }

    public OSSMeta(String region, String bucket, String key) {
        this.region = region;
        this.bucket = bucket;
        this.key = key;
    }

    /**
     * 检测合法性
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(region)) {
            logger.error("region is empty");
            return false;
        }
        if (StringUtils.isEmpty(bucket)) {
            logger.error("bucket is empty");
            return false;
        }
        if (StringUtils.isEmpty(key)) {
            logger.error("key is empty");
            return false;
        }
        if (key.startsWith("/")) {
            logger.error("oss key is not allowed to start with '/'");
            return false;
        }
        return true;
    }
}
