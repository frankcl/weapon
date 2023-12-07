package xin.manong.weapon.dynamic.secret;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.aliyun.secret.AliyunSecret;
import xin.manong.weapon.base.secret.DynamicSecret;
import xin.manong.weapon.base.secret.DynamicSecretListener;
import xin.manong.weapon.base.secret.Holder;

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
 * 秘钥配置文件格式
 * {
 *   "accessKey": "ak",
 *   "secretKey": "sk"
 * }
 *
 * 默认秘钥配置文件secret.json
 * 也可通过运行参数进行设置-DdynamicSecret=resourcePath
 *
 * @author frankcl
 * @date 2022-12-17 10:15:18
 */
public class LocalDynamicSecretListener implements DynamicSecretListener {

    private final static Logger logger = LoggerFactory.getLogger(LocalDynamicSecretListener.class);

    private final static String SECRET_RESOURCE_PARAM_NAME = "dynamicSecret";
    private final static String DEFAULT_SECRET_RESOURCE_FILE = "secret.json";

    public LocalDynamicSecretListener() {
    }

    @Override
    public boolean start() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        classLoader = (classLoader == null) ? ClassLoader.getSystemClassLoader() : classLoader;
        if (!Holder.hold(this)) {
            logger.error("hold local dynamic secret listener failed");
            return false;
        }
        Enumeration<URL> enumeration = getSecretResources(classLoader);
        try {
            if (!parseSecret(enumeration)) return false;
            logger.info("dynamic secret listener[{}] has been started", this.getClass().getName());
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void process(String changedSecret) {
    }

    /**
     * 通过资源内容解析秘钥
     *
     * @param enumeration 资源迭代器
     * @return 解析成功返回true，否则返回false
     * @throws Exception 解析失败抛出异常
     */
    private boolean parseSecret(Enumeration<URL> enumeration) throws Exception {
        if (enumeration == null) {
            logger.error("resource is not found");
            return false;
        }
        while (enumeration.hasMoreElements()) {
            URL resourceURL = enumeration.nextElement();
            InputStream inputStream = resourceURL.openStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);
            int n = 4096;
            byte[] buffer = new byte[n];
            while ((n = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, n);
            }
            String content = new String(outputStream.toByteArray(), Charset.forName("UTF-8"));
            outputStream.close();
            inputStream.close();
            AliyunSecret aliyunSecret = JSON.parseObject(content, AliyunSecret.class);
            if (aliyunSecret == null || !aliyunSecret.check()) {
                logger.error("invalid dynamic secret for resource[{}]", resourceURL);
                continue;
            }
            DynamicSecret.accessKey = aliyunSecret.accessKey;
            DynamicSecret.secretKey = aliyunSecret.secretKey;
            logger.info("parse dynamic AK/SK[{}/{}] success from path[{}]",
                    DynamicSecret.accessKey, DynamicSecret.secretKey, resourceURL);
            return true;
        }
        return false;
    }

    /**
     * 获取秘钥资源
     *
     * @param classLoader 类加载器
     * @return 秘钥资源迭代器
     */
    private Enumeration<URL> getSecretResources(ClassLoader classLoader) {
        String secretResourceFile = System.getProperty(SECRET_RESOURCE_PARAM_NAME, DEFAULT_SECRET_RESOURCE_FILE);
        try {
            if (classLoader == null) return ClassLoader.getSystemResources(secretResourceFile);
            return classLoader.getResources(secretResourceFile);
        } catch (Exception e) {
            logger.error("can not load dynamic secret for path[{}]", secretResourceFile);
            return null;
        }
    }
}
