package xin.manong.weapon.base.kafka;

import lombok.Data;

/**
 * kafka认证支持
 *
 * @author frankcl
 * @date 2025-03-13 20:01:08
 */
@Data
public class KafkaAuthSupport {

    public KafkaAuthConfig authConfig;

    /**
     * 检测认证信息有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        return authConfig == null || authConfig.check();
    }
}
