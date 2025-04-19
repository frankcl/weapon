package xin.manong.weapon.listener;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.secret.*;
import xin.manong.weapon.base.event.EventListener;
import xin.manong.weapon.base.event.Priority;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * 通过本地资源文件监听加载秘钥
 * 不支持秘钥动态更新监听
 * 仅为通过本地资源文件统一配置AK/SK提供支持
 * 秘钥配置文件格式
 * {
 *   "accessKey": "ak",
 *   "secretKey": "sk"
 * }
 * 默认秘钥配置文件aliyun_secret.json
 * 也可通过运行参数进行设置-Dsecret_path=path
 *
 * @author frankcl
 * @date 2022-12-17 10:15:18
 */
@Priority(2000)
public class DummySecretEventListener implements EventListener {

    private final static Logger logger = LoggerFactory.getLogger(DummySecretEventListener.class);

    private static final int BUFFER_SIZE = 4096;
    private static final String JVM_PARAM_SECRET_PATH = "secret_path";
    private static final String DEFAULT_SECRET_PATH = "aliyun_secret.json";

    public DummySecretEventListener() {
    }

    @Override
    public void init() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = System.getProperty(JVM_PARAM_SECRET_PATH, DEFAULT_SECRET_PATH);
        Enumeration<URL> enumeration = classLoader == null ?
                ClassLoader.getSystemResources(path) :
                classLoader.getResources(path);
        while (enumeration.hasMoreElements()) {
            URL resourceURL = enumeration.nextElement();
            try (InputStream input = resourceURL.openStream();
                 ByteArrayOutputStream output = new ByteArrayOutputStream(BUFFER_SIZE)) {
                int n; byte[] buffer = new byte[BUFFER_SIZE];
                while ((n = input.read(buffer, 0, buffer.length)) != -1) output.write(buffer, 0, n);
                AliyunSecret aliyunSecret = JSON.parseObject(output.toString(
                        StandardCharsets.UTF_8), AliyunSecret.class);
                if (aliyunSecret == null || !aliyunSecret.check()) {
                    logger.error("invalid aliyun secret from resource[{}]", resourceURL);
                    continue;
                }
                DynamicSecret.accessKey = aliyunSecret.accessKey;
                DynamicSecret.secretKey = aliyunSecret.secretKey;
                logger.info("parse aliyun secret[{}/{}] success from resource[{}]",
                        DynamicSecret.accessKey, DynamicSecret.secretKey, resourceURL);
                break;
            }
        }
    }
}
