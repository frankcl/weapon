package com.manong.weapon.aliyun.secret;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 秘钥
 *
 * @author frankcl
 * @date 2022-07-23 13:18:11
 */
public class KeySecret {

    private final static Logger logger = LoggerFactory.getLogger(KeySecret.class);

    public String accessKey;
    public String secretKey;

    /**
     * 检测秘钥合法性
     * accessKey和secretKey不为空
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(accessKey)) {
            logger.error("access key is empty");
            return false;
        }
        if (StringUtils.isEmpty(secretKey)) {
            logger.error("secret key is empty");
            return false;
        }
        return true;
    }
}
