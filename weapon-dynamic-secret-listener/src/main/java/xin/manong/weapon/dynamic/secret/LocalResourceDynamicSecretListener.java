package xin.manong.weapon.dynamic.secret;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.secret.AliyunSecret;
import xin.manong.weapon.base.secret.DynamicSecret;
import xin.manong.weapon.base.secret.DynamicSecretListener;
import xin.manong.weapon.base.secret.SecretListenerManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;

/**
 * 通过本地资源文件监听加载秘钥
 * 不支持秘钥动态更新监听
 * 仅为通过本地资源文件统一配置AK/SK提供支持
 *
 * @author frankcl
 * @date 2022-12-17 10:15:18
 */
public class LocalResourceDynamicSecretListener implements DynamicSecretListener {

    private final static Logger logger = LoggerFactory.getLogger(LocalResourceDynamicSecretListener.class);

    private final static String SECRET_RESOURCE_FILE_PARAM_NAME = "aliyunSecretFile";
    private final static String DEFAULT_SECRET_RESOURCE_FILE = "aliyun_secret.json";

    public LocalResourceDynamicSecretListener() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        classLoader = (classLoader == null) ? ClassLoader.getSystemClassLoader() : classLoader;
        if (SecretListenerManager.register(this)) {
            Enumeration<URL> enumeration = getSecretResources(classLoader);
            try {
                parseSecret(enumeration);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            logger.error("register local resource dynamic secret listener failed");
        }
    }

    @Override
    public void process(String secret) {
    }

    /**
     * 通过资源内容解析秘钥
     *
     * @param enumeration 资源迭代器
     * @throws Exception 解析失败抛出异常
     */
    private void parseSecret(Enumeration<URL> enumeration) throws Exception {
        if (enumeration == null) throw new RuntimeException("resource is not found");
        boolean success = false;
        while (enumeration.hasMoreElements()) {
            URL resourceURL = enumeration.nextElement();
            if (success) {
                logger.warn("aliyun AK/SK has been loaded, ignore resource[{}]", resourceURL);
                continue;
            }
            InputStream inputStream = resourceURL.openStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);
            int n = 4096;
            byte[] buffer = new byte[n];
            while ((n = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, n);
            }
            String content = new String(outputStream.toByteArray(), Charset.forName("UTF-8"));
            AliyunSecret aliyunSecret = JSON.parseObject(content, AliyunSecret.class);
            if (aliyunSecret == null || !aliyunSecret.check()) {
                logger.error("invalid dynamic secret for resource[{}]", resourceURL);
                continue;
            }
            DynamicSecret.accessKey = aliyunSecret.accessKey;
            DynamicSecret.secretKey = aliyunSecret.secretKey;
            success = true;
            logger.info("parse aliyun AK/SK[{}/{}] success from resource[{}]",
                    DynamicSecret.accessKey, DynamicSecret.secretKey, resourceURL);
        }
        if (!success) throw new RuntimeException("load dynamic secret failed from resources");
    }

    /**
     * 获取秘钥资源
     *
     * @param classLoader 类加载器
     * @return 秘钥资源迭代器
     */
    private Enumeration<URL> getSecretResources(ClassLoader classLoader) {
        String secretResourceFile = System.getProperty(SECRET_RESOURCE_FILE_PARAM_NAME, DEFAULT_SECRET_RESOURCE_FILE);
        try {
            if (classLoader == null) return ClassLoader.getSystemResources(secretResourceFile);
            return classLoader.getResources(secretResourceFile);
        } catch (Exception e) {
            logger.error("can not load secret resources for path[{}]", secretResourceFile);
            return null;
        }
    }
}
