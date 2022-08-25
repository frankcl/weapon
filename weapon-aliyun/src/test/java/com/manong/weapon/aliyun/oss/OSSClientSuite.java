package com.manong.weapon.aliyun.oss;

import com.alibaba.fastjson2.JSON;
import com.aliyun.oss.model.ObjectMetadata;
import com.manong.weapon.aliyun.secret.AliyunSecret;
import com.manong.weapon.base.util.FileUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author frankcl
 * @create 2020-01-08 14:55:21
 */
public class OSSClientSuite {

    private String secretFile = this.getClass().getResource("/secret").getPath();
    private OSSClient ossClient;

    @Before
    public void setUp() {
        String content = FileUtil.read(secretFile, Charset.forName("UTF-8"));
        AliyunSecret aliyunSecret = JSON.parseObject(content, AliyunSecret.class);
        OSSClientConfig config = new OSSClientConfig();
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
            Assert.assertTrue(metadata != null);
            Assert.assertEquals("33B158DC33D972D80A67749D53DFC2AF", metadata.getETag());
        }
        {
            ObjectMetadata metadata = ossClient.getObjectMeta("xhs-news-draft", "unknown.mp4");
            Assert.assertTrue(metadata == null);
        }
    }

    @Test
    public void testList() {
        List<String> keys = ossClient.list("default-crawler-file", "frankcl/image_dedup/");
        Assert.assertTrue(!keys.isEmpty());
        Assert.assertEquals(144, keys.size());
    }

    @Test
    public void testSign(){
        Assert.assertNotNull(ossClient.sign("news-image", "image-download/general_news/f434b3c156dd1ecbb8ae7993a2558f67.png"));
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
        Assert.assertTrue(content == null);
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
}
