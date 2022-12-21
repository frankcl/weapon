package xin.manong.weapon.aliyun.secret;

import lombok.Data;
import xin.manong.weapon.base.secret.DynamicSecret;

/**
 * 动态秘钥配置
 * 阿里云工具配置继承此类
 *
 * @author frankcl
 * @date 2022-12-15 10:24:09
 */
@Data
public class DynamicSecretConfig {

    public boolean dynamic = true;
    public AliyunSecret aliyunSecret = new AliyunSecret();

    /**
     * 检测配置有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (dynamic) {
            aliyunSecret.accessKey = DynamicSecret.accessKey;
            aliyunSecret.secretKey = DynamicSecret.secretKey;
        }
        return aliyunSecret != null && aliyunSecret.check();
    }
}
