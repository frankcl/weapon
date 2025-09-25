package xin.manong.weapon.base.email;

import jakarta.mail.MessagingException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author frankcl
 * @date 2025-09-25 16:43:48
 */
public class EmailSenderTest {

    private EmailSender emailSender;

    @Before
    public void setUp() throws Exception {
        EmailSendConfig config = new EmailSendConfig();
        config.smtpHost = "smtp.qiye.aliyun.com";
        config.smtpPort = 465;
        config.username = "marketing@junctionmagic.com";
        config.password = "";
        config.from = "marketing@junctionmagic.com";
        emailSender = new EmailSender(config);
    }

    @Test
    public void testSend() throws MessagingException {
        EmailRequest request = new EmailRequest();
        request.to = new ArrayList<>();
        request.to.add("283446486@qq.com");
        request.subject = "测试邮件";
        request.content = "<p>测试</p>";
        request.attachments = new ArrayList<>();
        request.attachments.add(new EmailAttachment(null, "https://lumy.oss-cn-hangzhou.aliyuncs.com/hylian/prod/avatar/8176c310c1a2f96d415189b1802a9e59.JPG?Expires=1758791361&OSSAccessKeyId=TMP.3KmzsfCxnMjbCUYC9dti2oScF3227wn3taNRADLHqgVxTTNuRiLk2mAPZpMHqrcz51zo3XG2CU5w3ughfuBZFXrzwg6Gzy&Signature=gO7xOBCgYa9v2PmAIxfJiyapZKM%3D"));
        emailSender.sendHtmlMail(request);
    }
}
