package xin.manong.weapon.base.http;

import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author frankcl
 * @date 2022-06-29 16:58:53
 */
public class HttpClientTest {

    private HttpClient httpClient;
    private HttpClient proxyHttpClient;

    @Before
    public void setUp() {
        HttpClientConfig config = new HttpClientConfig();
        httpClient = new HttpClient(config);
        List<Proxy> proxies = new ArrayList<>();
        proxies.add(new HttpProxy(Proxy.Type.HTTP, new InetSocketAddress("121.224.73.47", 888)));
        proxies.add(new HttpProxy(Proxy.Type.HTTP, new InetSocketAddress("121.224.78.14", 888)));
        ProxySelector proxySelector = new DummyProxySelector(proxies);
        proxyHttpClient = new HttpClient(config, proxySelector, new HttpProxyAuthenticator());
    }

    @Test
    public void testProxyGet() throws Exception {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestURL = "https://www.sina.com.cn";
        httpRequest.method = RequestMethod.GET;
        httpRequest.headers.put("Host", "www.sina.com.cn");
        try (Response response = proxyHttpClient.execute(httpRequest)) {
            Assert.assertTrue(response != null && response.isSuccessful());
            Assert.assertEquals(200, response.code());
            assert response.body() != null;
            System.out.println(response.body().string());
        }
    }

    @Test
    public void testDoPostByForm() throws Exception {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestURL = "http://kg.inner.xinhuazhiyun.com/kg/checkentity";
        httpRequest.method = RequestMethod.POST;
        httpRequest.format = RequestFormat.FORM;
        httpRequest.params = new HashMap<>();
        httpRequest.params.put("text", "留言对象——浙江省舟山市市长 徐仁标（代） 地区—— 浙江省 舟山市 舟山市市长徐仁标（代）\\n 您好，首先非常感谢您能在百忙之中查看我的留言！！！ 舟山要发展，交通必须先行，甬舟铁路是带动舟山发展的重要基础设施，麻烦问一下，甬舟铁路什么时候能实质性开工？");
        httpRequest.params.put("title", "询问甬舟铁路开工时间");
        httpRequest.params.put("bizName", "news-general-process");
        httpRequest.params.put("type", "all");
        Response response = httpClient.execute(httpRequest);
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(200, response.code());
        Assert.assertNotNull(response.body());
        response.close();
    }

    @Test
    public void testDoPostByJson() throws Exception {
        HttpRequest httpRequest = new HttpRequest.Builder().
                requestURL("http://query.process.xinhuazhiyun.com/api/query/analyze").
                method(RequestMethod.POST).format(RequestFormat.JSON).build();
        httpRequest.params = new HashMap<>();
        httpRequest.params.put("appId", "test");
        httpRequest.params.put("scope", Arrays.asList("KEYWORDS", "RECALL"));
        httpRequest.params.put("query", "儿子醉驾误伤母亲父亲报警大义灭亲");
        httpRequest.params.put("mode", "NORMAL");
        Response response = httpClient.execute(httpRequest);
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(200, response.code());
        Assert.assertNotNull(response.body());
        response.close();
    }

    @Test
    public void testDoHeadHttp() throws Exception {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestURL = "http://www.sina.com.cn";
        httpRequest.method = RequestMethod.HEAD;
        httpRequest.headers.put("Host", "www.sina.com.cn");
        Response response = httpClient.execute(httpRequest);
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(200, response.code());
        Assert.assertNotNull(response.body());
        String content = response.body().string();
        Assert.assertTrue(StringUtils.isEmpty(content));
        response.close();
    }

    @Test
    public void testDoGetHttp() throws Exception {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestURL = "https://www.163.com/news/article/JTTVS0VK000189FH.html?clickfrom=w_yw";
        httpRequest.method = RequestMethod.GET;
        httpRequest.headers.put("Host", "www.163.com");
        Response response = httpClient.execute(httpRequest);
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(200, response.code());
        Assert.assertNotNull(response.body());
        String content = response.body().string();
        Assert.assertNotNull(content);
        response.close();
    }

    @Test
    public void testDoGetHttps() throws Exception {
        HttpRequest httpRequest = new HttpRequest.Builder().
                requestURL("https://mail.shuwen.com/").method(RequestMethod.GET).build();
        Response response = httpClient.execute(httpRequest);
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(200, response.code());
        Assert.assertNotNull(response.body());
        String content = response.body().string();
        Assert.assertNotNull(content);
        response.close();
    }

    @Test(expected = UnknownHostException.class)
    public void testUnknownHostHttp() throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestURL = "https://www.sina.com.cnm/";
        httpRequest.method = RequestMethod.GET;
        httpRequest.headers = new HashMap<>();
        httpRequest.headers.put("key1", "value1");
        try (Response response = httpClient.execute(httpRequest)) {
            Assert.assertNull(response);
        }
    }
}
