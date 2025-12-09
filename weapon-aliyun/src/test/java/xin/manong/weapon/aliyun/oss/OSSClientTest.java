package xin.manong.weapon.aliyun.oss;

import com.alibaba.fastjson2.JSON;
import com.aliyun.oss.model.ObjectMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.weapon.aliyun.secret.AliyunSecret;
import xin.manong.weapon.base.util.FileUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author frankcl
 * @date 2020-01-08 14:55:21
 */
public class OSSClientTest {

    private final String secretFile = Objects.requireNonNull(this.getClass().
            getResource("/secret")).getPath();
    private OSSClient ossClient;

    @Before
    public void setUp() {
        String content = FileUtil.read(secretFile, StandardCharsets.UTF_8);
        AliyunSecret aliyunSecret = JSON.parseObject(content, AliyunSecret.class);
        OSSClientConfig config = new OSSClientConfig();
        config.dynamic = false;
        config.aliyunSecret = aliyunSecret;
        config.endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        Assert.assertTrue(config.check());
        ossClient = new OSSClient(config);
    }

    @After
    public void tearDown() {
        ossClient.close();
        ossClient = null;
    }

    @Test
    public void testGetObjectMeta() {
        {
            ObjectMetadata metadata = ossClient.getObjectMeta("default-crawler-file", "frankcl/knn/deadlock_stack");
            Assert.assertNotNull(metadata);
            Assert.assertEquals("33B158DC33D972D80A67749D53DFC2AF", metadata.getETag());
        }
        {
            ObjectMetadata metadata = ossClient.getObjectMeta("xhs-news-draft", "unknown.mp4");
            Assert.assertNull(metadata);
        }
    }

    @Test
    public void testList() {
        List<String> keys = ossClient.list("oss-files-copy", "lumy/prod/image", 35);
        Assert.assertFalse(keys.isEmpty());
        Assert.assertEquals(35, keys.size());
    }

    @Test
    public void testSign() {
        String ossURL = ossClient.sign("news-image", "image-download/general_news/f434b3c156dd1ecbb8ae7993a2558f67.png");
        Assert.assertNotNull(ossURL);
    }

    @Test
    public void testExist(){
        Assert.assertFalse(ossClient.exist("media-dedup", "media-dedup/test/1-2.jpeg"));
        Assert.assertTrue(ossClient.exist("default-crawler-file", "frankcl/knn/deadlock_stack"));
    }

    @Test
    public void testGetPutNotExists() {
        String bucket = "default-crawler-file";
        String key = "frankcl/unknown.mp4";
        byte[] content = ossClient.getObject(bucket, key);
        Assert.assertNull(content);
        Assert.assertFalse(ossClient.putObject(bucket, key, (byte[]) null));
    }

    @Test
    public void testGetPutDeleteObject() {
        String bucket = "default-crawler-file";
        String key = "frankcl/stack";
        byte[] content = ossClient.getObject(bucket, key);
        Assert.assertTrue(content != null && content.length > 0);
        ossClient.deleteObject(bucket, key);
        Assert.assertTrue(ossClient.putObject(bucket, key, content));
    }

    @Test
    public void testParseURL() {
        String ossURL = "https://xhzy-data-video.oss-cn-hangzhou.aliyuncs.com/1605603493782-26246b82-671c-4839-860d-328fbb7d3353.mp4?Expires=1676364724&OSSAccessKeyId=TMP.3Kd8p57KQqZMvKhNq35qHY37AGmDcb5c9kXa15XjoHmeTALpYLBx3zXUPWdBTMV8rocZVZcWYpjo243RKMDjQQt5Fdab2V&Signature=ClDtjQxHrn5sqNDWxlHia2dAWiw%3D";
        OSSMeta ossMeta = OSSClient.parseURL(ossURL);
        Assert.assertTrue(ossMeta != null && ossMeta.check());
        Assert.assertEquals("xhzy-data-video", ossMeta.bucket);
        Assert.assertEquals("cn-hangzhou", ossMeta.region);
        Assert.assertEquals("1605603493782-26246b82-671c-4839-860d-328fbb7d3353.mp4", ossMeta.key);
    }

    @Test
    public void testBuildURL() {
        OSSMeta ossMeta = new OSSMeta("cn-hangzhou", "xhzy-data-video", "1605603493782-26246b82-671c-4839-860d-328fbb7d3353.mp4");
        String ossURL = OSSClient.buildURL(ossMeta);
        Assert.assertNotNull(ossURL);
        Assert.assertEquals("http://xhzy-data-video.oss-cn-hangzhou.aliyuncs.com/1605603493782-26246b82-671c-4839-860d-328fbb7d3353.mp4", ossURL);
    }

    @Test
    public void testEraseInternal() {
        String ossURL = "http://xhzy-data-external.oss-cn-hangzhou-internal.aliyuncs.com/xinhuashe/test/image/8628cdac0e931b19a550543a4547c24b.jpg";
        String processedURL = OSSClient.eraseInternal(ossURL);
        Assert.assertEquals("http://xhzy-data-external.oss-cn-hangzhou.aliyuncs.com/xinhuashe/test/image/8628cdac0e931b19a550543a4547c24b.jpg", processedURL);
    }
}
