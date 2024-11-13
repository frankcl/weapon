package xin.manong.weapon.aliyun.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.rebuild.RebuildManager;
import xin.manong.weapon.base.rebuild.Rebuildable;
import xin.manong.weapon.aliyun.secret.DynamicSecret;
import xin.manong.weapon.base.util.CommonUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OSS客户端
 *
 * @author frankcl
 * @date 2019-08-26 16:10:36
 */
public class OSSClient implements Rebuildable {

    private final static Logger logger = LoggerFactory.getLogger(OSSClient.class);

    private final static String PREFIX_HTTP = "http://";
    private final static String PREFIX_HTTPS = "https://";
    private final static String ALIYUN_OSS_DOMAIN = "aliyuncs.com";
    private final static String INTERNAL = "-internal";

    private final static int BUFFER_SIZE = 4096;
    private final static long EXPIRED_TIME_1H = 60 * 60 * 1000L;

    private final OSSClientConfig config;
    private OSS instance;

    public OSSClient(OSSClientConfig config) {
        this.config = config;
        if (!this.config.check()) throw new RuntimeException("oss client config is invalid");
        build();
        if (this.config.dynamic) RebuildManager.register(this);
    }

    /**
     * 关闭客户端实例
     */
    public void close() {
        logger.info("OSS client is closing ...");
        if (config.dynamic) RebuildManager.unregister(this);
        if (instance != null) instance.shutdown();
        logger.info("OSS client has been closed");
    }

    @Override
    public void rebuild() {
        logger.info("OSS client is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("secret is not changed, ignore OSS client rebuilding");
            return;
        }
        config.aliyunSecret.accessKey = DynamicSecret.accessKey;
        config.aliyunSecret.secretKey = DynamicSecret.secretKey;
        OSS prevInstance = instance;
        build();
        if (prevInstance != null) prevInstance.shutdown();
        logger.info("OSS client rebuild success");
    }

    /**
     * 构建实例
     */
    private void build() {
        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        configuration.setConnectionTimeout(config.connectionTimeoutMs);
        configuration.setSocketTimeout(config.socketTimeoutMs);
        instance = new OSSClientBuilder().build(config.endpoint, config.aliyunSecret.accessKey, config.aliyunSecret.secretKey);
    }

    /**
     * 获取数据
     *
     * @param request OSS请求对象
     * @return 如果成功返回内容，否则返回null
     */
    private byte[] getObjectOnce(GetObjectRequest request) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            OSSObject ossObject = instance.getObject(request);
            if (ossObject == null || ossObject.getObjectContent() == null) return null;
            int size;
            byte[] buffer = new byte[BUFFER_SIZE];
            inputStream = ossObject.getObjectContent();
            outputStream = new ByteArrayOutputStream(BUFFER_SIZE);
            while ((size = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                outputStream.write(buffer, 0, size);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 获取数据流
     *
     * @param bucket bucket
     * @param key key
     * @return 如果成功返回数据流，否则返回null
     */
    public InputStream getObjectStream(String bucket, String key) {
        GetObjectRequest request = new GetObjectRequest(bucket, key);
        try {
            OSSObject ossObject = instance.getObject(request);
            if (ossObject == null || ossObject.getObjectContent() == null) {
                logger.warn("oss object is not found for key[{}] and bucket[{}]",
                        request.getKey(), request.getBucketName());
                return null;
            }
            return ossObject.getObjectContent();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取数据
     *
     * @param bucket bucket
     * @param key key
     * @return 如果成功返回内容，否则返回null
     */
    public byte[] getObject(String bucket, String key) {
        GetObjectRequest request = new GetObjectRequest(bucket, key);
        for (int i = 0; i < config.retryCnt; i++) {
            byte[] content = getObjectOnce(request);
            if (content != null) return content;
            logger.warn("get oss object failed for key[{}] and bucket[{}], retry {} times",
                    key, bucket, i + 1);
        }
        logger.error("get oss object failed for key[{}] and bucket[{}]", key, bucket);
        return null;
    }

    /**
     * 上传数据
     *
     * @param bucket bucket
     * @param key key
     * @param content 内容
     * @return 成功返回true，否则返回false
     */
    public boolean putObject(String bucket, String key, byte[] content) {
        if (content == null || content.length == 0) {
            logger.warn("put content is empty for bucket[{}] and key[{}]", bucket, key);
            return false;
        }
        for (int i = 0; i < config.retryCnt; i++) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            if (putObject(bucket, key, inputStream)) return true;
        }
        return false;
    }

    /**
     * 上传数据
     *
     * @param bucket bucket
     * @param key key
     * @param inputStream 输入流
     * @return 成功返回true，否则返回false
     */
    public boolean putObject(String bucket, String key, InputStream inputStream) {
        if (inputStream == null) {
            logger.error("input stream is null");
            return false;
        }
        PutObjectResult result = instance.putObject(bucket, key, inputStream);
        if (result == null || StringUtils.isEmpty(result.getETag())) {
            logger.error("put object failed for bucket[{}] and key[{}]", bucket, key);
            return false;
        }
        return true;
    }

    /**
     * 删除数据
     *
     * @param bucket bucket
     * @param key key
     */
    public void deleteObject(String bucket, String key) {
        try {
            instance.deleteObject(bucket, key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取数据信息
     *
     * @param bucket bucket
     * @param key key
     * @return 成功返回数据信息，否则返回null
     */
    public ObjectMetadata getObjectMeta(String bucket, String key) {
        try {
            return instance.getObjectMetadata(bucket, key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 列表目录文件
     * 最多不超过10000个文件
     *
     * @param bucket bucket
     * @param directory 目录
     * @return 成功返回key列表，否则返回null
     */
    public List<String> list(String bucket, String directory) {
        try {
            int count = 0;
            final int maxKeys = 1000, maxNum = 10000;
            String nextMarker = null;
            ObjectListing objectListing;
            List<String> keys = new ArrayList<>();
            do {
                objectListing = instance.listObjects(new ListObjectsRequest(bucket).withPrefix(directory).
                        withMarker(nextMarker).withMaxKeys(maxKeys));
                List<OSSObjectSummary> summaryList = objectListing.getObjectSummaries();
                for (OSSObjectSummary summary : summaryList) {
                    if (summary.getKey().equals(directory)) continue;
                    keys.add(summary.getKey());
                    if (++count >= maxNum) break;
                }
                nextMarker = objectListing.getNextMarker();
            } while (objectListing.isTruncated());
            return keys;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 数据加签，过期时间1小时
     *
     * @param bucket bucket
     * @param key key
     * @return 加签URL
     */
    public String sign(String bucket, String key) {
        return sign(bucket, key, EXPIRED_TIME_1H);
    }

    /**
     * 数据加签
     *
     * @param bucket bucket
     * @param key key
     * @param ttl 过期时间（毫秒）
     * @return 加签URL
     */
    public String sign(String bucket, String key, long ttl) {
        long currentTime = new Date().getTime();
        Date expiredTime = ttl > 0 ? new Date(currentTime + ttl) : new Date(currentTime + EXPIRED_TIME_1H);
        URL url = instance.generatePresignedUrl(bucket, key, expiredTime);
        return OSSClient.eraseInternal(url.toString());
    }

    /**
     * 数据是否存在
     *
     * @param bucket bucket
     * @param key key
     * @return 存在返回true，否则返回false
     */
    public boolean exist(String bucket, String key) {
        return instance.doesObjectExist(bucket, key);
    }

    /**
     * 解析OSS URL，返回元信息
     *
     * @param ossURL OSS URL
     * @return 解析成功返回元信息，否则返回null
     */
    public static OSSMeta parseURL(String ossURL) {
        String host = CommonUtil.getHost(ossURL);
        if (StringUtils.isEmpty(host)) {
            logger.error("parse host failed for url[{}]", ossURL);
            return null;
        }
        int from = host.indexOf(".");
        if (from == -1) {
            logger.error("parse bucket failed for url[{}]", ossURL);
            return null;
        }
        OSSMeta ossMeta = new OSSMeta();
        ossMeta.bucket = host.substring(0, from);
        int to = host.indexOf(".", from + 1);
        if (to == -1) {
            logger.error("parse region failed for url[{}]", ossURL);
            return null;
        }
        ossMeta.region = host.substring(from + 1, to);
        if (ossMeta.region.startsWith("oss-")) ossMeta.region = ossMeta.region.substring("oss-".length());
        String key = parseKey(ossURL);
        if (StringUtils.isEmpty(key)) {
            logger.error("parse key failed for url[{}]", ossURL);
            return null;
        }
        ossMeta.key = key;
        return ossMeta;
    }

    /**
     * 根据OSS元信息构建OSS URL
     *
     * @param ossMeta OSS元信息
     * @return 构建失败返回null，否则返回URL
     */
    public static String buildURL(OSSMeta ossMeta) {
        if (ossMeta == null || !ossMeta.check()) {
            logger.error("oss meta is null or invalid");
            return null;
        }
        return String.format(ossMeta.region.startsWith("oss-") ? "http://%s.%s.%s/%s" : "http://%s.oss-%s.%s/%s",
                ossMeta.bucket, ossMeta.region, ALIYUN_OSS_DOMAIN, ossMeta.key);
    }

    /**
     * 抹除OSS URL中internal信息
     *
     * @param ossURL OSS URL
     * @return 抹除internal信息OSS URL
     */
    public static String eraseInternal(String ossURL) {
        OSSMeta ossMeta = parseURL(ossURL);
        if (ossMeta == null) return ossURL;
        if (!ossMeta.region.endsWith(INTERNAL)) return ossURL;
        int pos = ossURL.indexOf(ossMeta.region);
        if (pos == -1) return ossURL;
        return String.format("%s%s%s", ossURL.substring(0, pos),
                ossMeta.region.substring(0, ossMeta.region.length() - INTERNAL.length()),
                ossURL.substring(pos + ossMeta.region.length()));
    }

    /**
     * 根据OSS URL解析OSS key
     *
     * @param ossURL OSS URL
     * @return 如果成功返回OSS key，否则返回null或空字符串
     */
    private static String parseKey(String ossURL) {
        int from = -1;
        if (ossURL.startsWith(PREFIX_HTTP)) {
            from = PREFIX_HTTP.length();
        } else if (ossURL.startsWith(PREFIX_HTTPS)) {
            from = PREFIX_HTTPS.length();
        }
        if (from == -1) {
            logger.error("invalid protocol for url[{}]", ossURL);
            return null;
        }
        int index = ossURL.indexOf("/", from);
        String key = index == -1 ? "" : ossURL.substring(index + 1);
        if (key.contains("?")) key = key.substring(0, key.indexOf("?"));
        try {
            key = URLDecoder.decode(key, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return key;
    }
}