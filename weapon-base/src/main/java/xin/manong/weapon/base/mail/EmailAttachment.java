package xin.manong.weapon.base.mail;

import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.activation.URLDataSource;
import jakarta.mail.MessagingException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 附件
 *
 * @author frankcl
 * @date 2025-09-25 15:55:32
 */
@Data
public class EmailAttachment {

    private static final Logger logger = LoggerFactory.getLogger(EmailAttachment.class);

    public String fileName;
    public String filePath;

    public EmailAttachment(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    /**
     * 构建数据源
     *
     * @return 数据源
     * @throws MessagingException 异常
     */
    public DataSource buildDataSource() throws MessagingException {
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            try {
                return new URLDataSource(new URL(filePath));
            } catch (MalformedURLException e) {
                throw new MessagingException("Attachment path invalid", e);
            }
        }
        return new FileDataSource(filePath);
    }

    /**
     * 检测有效性
     */
    public void check() {
        if (StringUtils.isEmpty(filePath)) {
            logger.error("Attachment path is empty");
            throw new IllegalArgumentException("附件文件路径为空");
        }
        if (StringUtils.isEmpty(fileName)) {
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            try {
                URL url = new URL(filePath);
                fileName = url.getPath();
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            } catch (MalformedURLException e) {
                logger.debug("File is not valid URL");
            }
        }
    }
}
