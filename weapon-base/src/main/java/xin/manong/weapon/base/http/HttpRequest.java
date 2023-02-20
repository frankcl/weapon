package xin.manong.weapon.base.http;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求
 *
 * @author frankcl
 * @date 2022-06-29 15:54:02
 */
public class HttpRequest {

    private final static Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    public String requestURL;
    public RequestMethod method;
    public RequestFormat format;
    public Map<String, Object> params = new HashMap<>();
    public Map<String, String> headers = new HashMap<>();

    /**
     * 构建HTTP GET请求
     *
     * @param requestURL 请求URL
     * @param params 请求参数
     * @return HTTP请求
     */
    public static HttpRequest buildGetRequest(String requestURL, Map<String, Object> params) {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestURL = requestURL;
        httpRequest.method = RequestMethod.GET;
        httpRequest.params = params;
        return httpRequest;
    }

    /**
     * 构建HTTP POST请求
     *
     * @param requestURL 请求URL
     * @param format 请求格式
     * @param params 请求参数
     * @return HTTP请求
     */
    public static HttpRequest buildPostRequest(String requestURL, RequestFormat format,
                                               Map<String, Object> params) {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestURL = requestURL;
        httpRequest.method = RequestMethod.POST;
        httpRequest.format = format;
        httpRequest.params = params;
        return httpRequest;
    }

    /**
     * 检测请求合法性
     * 1. 请求URL不为空
     * 2. 请求方式不为空
     * 3. POST请求方式必须填写请求格式
     *
     * @return 如果合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(requestURL)) {
            logger.error("request url is empty");
            return false;
        }
        if (method == null) {
            logger.error("http request method is null");
            return false;
        }
        if (method == RequestMethod.POST && format == null) {
            logger.error("http post body format is null");
            return false;
        }
        return true;
    }
}
