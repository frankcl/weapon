package xin.manong.weapon.alarm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 第三方webHook处理器
 *
 * @author frankcl
 * @date 2023-12-07 14:35:04
 */
public class WebHookHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebHookHandler.class);

    private static final String SHA256 = "HmacSHA256";
    private static final String KEY_SIGN = "sign";
    private static final String KEY_TIMESTAMP = "timestamp";

    /**
     * 钉钉webHook地址签名
     * webHookSecret为空返回原始webHook地址
     *
     * @param webHookURL 钉钉webHook地址
     * @param webHookSecret 钉钉webHook签名秘钥
     * @return 成功返回webhook签名地址，否则返回null
     */
    public final String encryptDingWebHookURL(String webHookURL, String webHookSecret) {
        if (StringUtils.isEmpty(webHookURL)) return null;
        if (StringUtils.isEmpty(webHookSecret)) {
            logger.warn("web hook secret is empty");
            return webHookURL;
        }
        Long timestamp = System.currentTimeMillis();
        try {
            Mac mac = Mac.getInstance(SHA256);
            mac.init(new SecretKeySpec(webHookSecret.getBytes(StandardCharsets.UTF_8), SHA256));
            byte[] bytes = mac.doFinal(String.format("%d\n%s", timestamp, webHookSecret).getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(new String(Base64.getEncoder().encode(bytes)), StandardCharsets.UTF_8);
            return String.format("%s&%s=%d&%s=%s", webHookURL, KEY_TIMESTAMP, timestamp, KEY_SIGN, sign);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return webHookURL;
        }
    }
}
