package com.manong.weapon.base.html;

import com.manong.weapon.base.http.*;
import okhttp3.Response;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2022-09-15 14:57:46
 */
public class HTMLExtractorSuite {

    private HttpClient httpClient;

    @Before
    public void setUp() {
        HttpClientConfig config = new HttpClientConfig();
        httpClient = new HttpClient(config);
    }

    @Test
    public void testExtractMainElement() throws Exception {
        HttpRequest request = new HttpRequest();
        request.requestURL = "http://mp.weixin.qq.com/s?__biz=MzUzODk3NzAxNw==&mid=2247502782&idx=1&sn=f6834efc68398d43b17047ca1c851e27";
        request.method = RequestMethod.GET;
        Response response = httpClient.execute(request);
        Assert.assertEquals(200, response.code());
        String html = response.body().string();
        response.close();
        Element element = HTMLExtractor.extractMainElement(html, request.requestURL);
        Assert.assertTrue(element != null);
        HTMLExtractor.buildMainHTML(element);
    }

    @Test
    public void testBuildMainHTML() throws Exception {
        HttpRequest request = new HttpRequest();
        request.requestURL = "https://mp.weixin.qq.com/s?__biz=MjM5MzI5NTU3MQ==&mid=2652091313&idx=6&sn=034e6d431d2ffb1a94422a26841e8816";
        request.method = RequestMethod.GET;
        Response response = httpClient.execute(request);
        Assert.assertEquals(200, response.code());
        String html = response.body().string();
        response.close();
        Element element = HTMLExtractor.extractMainElement(html, request.requestURL);
        Assert.assertTrue(element != null);
        String content = HTMLExtractor.buildMainHTML(element);
        Assert.assertTrue(content != null && content.length() > 0);
    }
}
