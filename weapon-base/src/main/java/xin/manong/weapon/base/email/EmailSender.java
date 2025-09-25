package xin.manong.weapon.base.email;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import javax.net.ssl.SSLSocketFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 电子邮件发送
 *
 * @author frankcl
 * @date 2025-09-25 15:39:11
 */
public class EmailSender {

    private static final String PROP_SMTP_HOST = "mail.smtp.host";
    private static final String PROP_SMTP_PORT = "mail.smtp.port";
    private static final String PROP_SMTP_AUTH = "mail.smtp.auth";
    private static final String PROP_SMTP_STARTTLS = "mail.smtp.starttls.enable";
    private static final String PROP_SMTP_SOCKET_FACTORY = "mail.smtp.socketFactory.class";

    private static final String CONTENT_TYPE_PLAIN = "text/plain; charset=utf-8";
    private static final String CONTENT_TYPE_HTML = "text/html; charset=utf-8";

    private final EmailSendConfig config;

    public EmailSender(EmailSendConfig config) {
        this.config = config;
        this.config.check();
    }

    /**
     * 构建邮件发送会话
     *
     * @return 会话
     */
    private Session buildSession() {
        Properties properties = new Properties();
        properties.put(PROP_SMTP_HOST, config.smtpHost);
        properties.put(PROP_SMTP_PORT, config.smtpPort);
        properties.put(PROP_SMTP_AUTH, "true");
        if (config.smtpPort == 587) properties.put(PROP_SMTP_STARTTLS, "true");
        else if (config.smtpPort == 465) properties.put(PROP_SMTP_SOCKET_FACTORY, SSLSocketFactory.class.getName());
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.username, config.password);
            }
        });
    }

    /**
     * 设置收件人信息
     *
     * @param message 消息
     * @param recipients 收件人地址列表
     * @param type 收件人类型
     * @throws MessagingException 消息异常
     */
    private void setRecipients(MimeMessage message, List<String> recipients,
                               Message.RecipientType type) throws MessagingException {
        if (recipients == null || recipients.isEmpty()) return;
        List<Address> addresses = new ArrayList<>();
        for (String recipient : recipients) addresses.add(new InternetAddress(recipient));
        message.setRecipients(type, addresses.toArray(new Address[0]));
    }

    /**
     * 发送邮件
     *
     * @param request 发送请求
     * @param contentType 内容类型
     * @throws MessagingException 邮件异常
     */
    private void sendMail(EmailRequest request, String contentType) throws MessagingException {
        request.check();
        Session session = buildSession();
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(config.from));
        setRecipients(message, request.to, Message.RecipientType.TO);
        setRecipients(message, request.cc, Message.RecipientType.CC);
        setRecipients(message, request.bcc, Message.RecipientType.BCC);
        message.setSubject(request.subject);
        Multipart multipart = new MimeMultipart();
        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(request.content, contentType);
        multipart.addBodyPart(bodyPart);
        if (request.attachments != null && !request.attachments.isEmpty()) {
            for (EmailAttachment attachment : request.attachments) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = attachment.buildDataSource();
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(attachment.fileName);
                multipart.addBodyPart(attachmentBodyPart);
            }
        }
        message.setContent(multipart);
        Transport.send(message);
    }

    /**
     * 发送文本邮件
     *
     * @param request 发送请求
     * @throws MessagingException 消息异常
     */
    public void sendTextMail(EmailRequest request) throws MessagingException {
        sendMail(request, CONTENT_TYPE_PLAIN);
    }

    /**
     * 发送HTML邮件
     *
     * @param request 发送请求
     * @throws MessagingException 消息异常
     */
    public void sendHtmlMail(EmailRequest request) throws MessagingException {
        sendMail(request, CONTENT_TYPE_HTML);
    }
}
