package xin.manong.weapon.base.email;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 发送邮件请求
 *
 * @author frankcl
 * @date 2025-09-25 15:53:41
 */
@Data
public class EmailRequest {

    private static final Logger logger = LoggerFactory.getLogger(EmailRequest.class);

    public List<String> to;
    public List<String> cc;
    public List<String> bcc;
    public String subject;
    public String content;
    public List<EmailAttachment> attachments;

    /**
     * 检测有效性
     */
    public void check() {
        if (to == null || to.isEmpty()) {
            logger.error("Send targets are empty");
            throw new IllegalArgumentException("收件人地址为空");
        }
        if (StringUtils.isEmpty(subject)) {
            logger.error("Email subject is empty");
            throw new IllegalArgumentException("邮件主题为空");
        }
        if (attachments != null) {
            for (EmailAttachment attachment : attachments) attachment.check();
        }
    }
}
