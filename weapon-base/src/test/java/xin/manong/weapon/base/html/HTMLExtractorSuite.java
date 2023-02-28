package xin.manong.weapon.base.html;

import okhttp3.Response;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.weapon.base.http.HttpClient;
import xin.manong.weapon.base.http.HttpClientConfig;
import xin.manong.weapon.base.http.HttpRequest;
import xin.manong.weapon.base.http.RequestMethod;

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
        request.requestURL = "http://www.gov.cn/xinwen/2023-02/26/content_5743376.htm";
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

    @Test
    public void testBuildMainHTML() throws Exception {
        HttpRequest request = new HttpRequest();
        request.requestURL = "https://www.bjnews.com.cn/detail/167755237214171.html";
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

    @Test
    public void testExtractPublishTime() throws Exception {
        HttpRequest request = new HttpRequest();
        request.requestURL = "http://politics.people.com.cn/n1/2022/1014/c1001-32544932.html";
        request.method = RequestMethod.GET;
        Response response = httpClient.execute(request);
        Assert.assertEquals(200, response.code());
        String html = response.body().string();
        response.close();
        Element element = HTMLExtractor.extractMainElement(html, request.requestURL);
        Assert.assertTrue(element != null);
        Long publishTime = HTMLExtractor.extractPublishTime(element);
        Assert.assertTrue(publishTime != null);
    }
}
