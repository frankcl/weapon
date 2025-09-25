package xin.manong.weapon.base.email;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发送Email配置信息
 *
 * @author frankcl
 * @date 2025-09-25 15:40:43
 */
@Data
public class EmailSendConfig {

    private static final Logger logger = LoggerFactory.getLogger(EmailSendConfig.class);

    public int smtpPort;
    public String smtpHost;
    public String username;
    public String password;
    public String from;

    /**
     * 检测有效性
     */
    public void check() {
        if (smtpPort <= 0) {
            logger.error("SMTP port:{} is not valid", smtpPort);
            throw new IllegalArgumentException("SMTP端口非法");
        }
        if (StringUtils.isEmpty(smtpHost)) {
            logger.error("SMTP host is not set");
            throw new IllegalArgumentException("SMTP服务地址未设置");
        }
        if (StringUtils.isEmpty(username)) {
            logger.error("username is not set for sending email");
            throw new IllegalArgumentException("用户名未设置");
        }
        if (StringUtils.isEmpty(password)) {
            logger.error("password is not set for sending email");
            throw new IllegalArgumentException("密码未设置");
        }
        if (StringUtils.isEmpty(from)) {
            logger.error("send from is not set for sending email");
            throw new IllegalArgumentException("发件人地址为空");
        }
    }
}
