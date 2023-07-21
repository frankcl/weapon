package xin.manong.weapon.base.html;

import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
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
    public void testFormatHTML() throws Exception {
        String url = "http://www.gov.cn/xinwen/2023-02/26/content_5743376.htm";
        String html = "<div class=\"article oneColumn\">\n" +
                " <h1 id=\"ti\">中国共产党第二十届中央委员会第二次全体会议在北京开始举行</h1>\n" +
                " <div class=\"pages-date\">\n" +
                "  2023-02-26 18:01 <span class=\"font\">来源： 新华社微博 </span>\n" +
                "  <div class=\"pages_print mhide\"><span class=\"font index_switchsize\">字号：<span class=\"default on\">默认</span> <span class=\"big\">大</span> <span class=\"bigger\">超大</span> </span> <span class=\"split\">|</span> <span class=\"font printIco\" onclick=\"forPrintEventListenerFn('beforeprint')\">打印</span> <span class=\"split\">|</span> <!-- share -->\n" +
                "   <div class=\"share\" id=\"share\">\n" +
                "    <div class=\"share-box\" id=\"share-box\"><a class=\"share-btn gwds_tsina\" data-w=\"gwds_tsina\" title=\"新浪微博\">&nbsp;</a> <a class=\"share-btn gwds_weixin\" data-w=\"gwds_weixin\" title=\"微信\">&nbsp;</a> <a class=\"share-btn gwds_qzone\" data-w=\"gwds_qzone\" title=\"qq空间\">&nbsp;</a>\n" +
                "    </div>\n" +
                "   </div>\n" +
                "  </div>\n" +
                " </div>\n" +
                " <div class=\"pages_content\" id=\"UCAP-CONTENT\">\n" +
                "  <p style=\"text-indent: 2em; font-family: 宋体; font-size: 12pt;\">中国共产党第二十届中央委员会第二次全体会议26日下午在北京开始举行。中央委员会总书记习近平代表中央政治局向全会作工作报告。全会将审议党和国家机构改革方案（草案）、中央政治局拟向十四届全国人大一次会议推荐的国家机构领导人员人选建议名单和拟向全国政协十四届一次会议推荐的全国政协领导人员人选建议名单。</p>\n" +
                "  <div style=\"display:none\">\n" +
                "  </div>\n" +
                " </div>\n" +
                " <div class=\"editor\"><span><a href=\"https://www.gov.cn/fuwu/jiucuo.htm\" target=\"_blank\">【我要纠错】</a></span><span class=\"zrbj\">责任编辑：王洋</span>\n" +
                " </div>\n" +
                " <div class=\"pageInfo pageGray\" id=\"pagination\"></div>\n" +
                " <div id=\"div_div\" class=\"mhide\">\n" +
                "  <div id=\"qr_container\" class=\"sweep\">\n" +
                "   扫一扫在手机打开当前页\n" +
                "  </div>\n" +
                " </div> <!-- 用来校验该浏览器是否支持HTML5 -->\n" +
                " <canvas id=\"Canvas\"></canvas> <!--相关稿件-->\n" +
                " <div class=\"xg-list related\">\n" +
                "  <div class=\"pannel-title\">\n" +
                "   相关稿件\n" +
                "  </div>\n" +
                "  <ul class=\"list01\">\n" +
                "  </ul>\n" +
                " </div> <!--相关稿件-->\n" +
                " <div class=\"clear\"></div>\n" +
                "</div>";
        String formatHTML = HTMLExtractor.formatHTML(html, url);
        Assert.assertTrue(!StringUtils.isEmpty(formatHTML));
    }

    @Test
    public void testMainHTMLElement() throws Exception {
        HttpRequest request = new HttpRequest();
        request.requestURL = "http://www.gov.cn/xinwen/2023-02/26/content_5743376.htm";
        request.method = RequestMethod.GET;
        Response response = httpClient.execute(request);
        Assert.assertEquals(200, response.code());
        String html = response.body().string();
        response.close();
        Element element = HTMLExtractor.mainHTMLElement(html, request.requestURL);
        Assert.assertTrue(element != null);
        String content = HTMLExtractor.formatHTMLElement(element);
        Assert.assertTrue(content != null && content.length() > 0);
    }

    @Test
    public void testFormatHTMLElement() throws Exception {
        HttpRequest request = new HttpRequest();
        request.requestURL = "https://www.bjnews.com.cn/detail/167755237214171.html";
        request.method = RequestMethod.GET;
        Response response = httpClient.execute(request);
        Assert.assertEquals(200, response.code());
        String html = response.body().string();
        response.close();
        Element element = HTMLExtractor.mainHTMLElement(html, request.requestURL);
        Assert.assertTrue(element != null);
        String content = HTMLExtractor.formatHTMLElement(element);
        Assert.assertTrue(content != null && content.length() > 0);
    }

    @Test
    public void testPublishTime() throws Exception {
        HttpRequest request = new HttpRequest();
        request.requestURL = "http://politics.people.com.cn/n1/2022/1014/c1001-32544932.html";
        request.method = RequestMethod.GET;
        Response response = httpClient.execute(request);
        Assert.assertEquals(200, response.code());
        String html = response.body().string();
        response.close();
        Element element = HTMLExtractor.mainHTMLElement(html, request.requestURL);
        Assert.assertTrue(element != null);
        Long publishTime = HTMLExtractor.publishTime(element);
        Assert.assertTrue(publishTime != null);
    }
}
