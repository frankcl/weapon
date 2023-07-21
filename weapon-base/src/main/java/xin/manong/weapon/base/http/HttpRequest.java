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
     * 请求构建器
     */
    public static class Builder {
        private HttpRequest httpRequest;

        public Builder() {
            httpRequest = new HttpRequest();
        }

        /**
         * 设置请求URL
         *
         * @param requestURL 请求URL
         * @return Builder
         */
        public Builder requestURL(String requestURL) {
            httpRequest.requestURL = requestURL;
            return this;
        }

        /**
         * 设置请求方式
         *
         * @param method 请求方式
         * @return Builder
         */
        public Builder method(RequestMethod method) {
            httpRequest.method = method;
            return this;
        }

        /**
         * 设置请求数据格式
         *
         * @param format 请求数据格式
         * @return Builder
         */
        public Builder format(RequestFormat format) {
            httpRequest.format = format;
            return this;
        }

        /**
         * 设置请求参数
         *
         * @param params 请求参数
         * @return Builder
         */
        public Builder params(Map<String, Object> params) {
            httpRequest.params = params;
            return this;
        }

        /**
         * 设置请求头信息
         *
         * @param headers 请求头信息
         * @return Builder
         */
        public Builder headers(Map<String, String> headers) {
            httpRequest.headers = headers;
            return this;
        }

        /**
         * 构建HTTP请求对象
         *
         * @return HTTP请求对象
         */
        public HttpRequest build() {
            HttpRequest replica = new HttpRequest();
            replica.requestURL = httpRequest.requestURL;
            replica.method = httpRequest.method;
            replica.format = httpRequest.format;
            replica.headers = httpRequest.headers;
            replica.params = httpRequest.params;
            return replica;
        }
    }
    /**
     * 构建HTTP GET请求
     *
     * @param requestURL 请求URL
     * @param params 请求参数
     * @return HTTP请求
     */
    public static HttpRequest buildGetRequest(String requestURL, Map<String, Object> params) {
        return new HttpRequest.Builder().requestURL(requestURL).
                method(RequestMethod.GET).params(params).build();
    }

    /**
     * 构建HTTP POST请求
     *
     * @param requestURL 请求URL
     * @param format 请求格式
     * @param body 请求体
     * @return HTTP请求
     */
    public static HttpRequest buildPostRequest(String requestURL, RequestFormat format,
                                               Map<String, Object> body) {
        return new HttpRequest.Builder().requestURL(requestURL).method(RequestMethod.POST).
                format(format).params(body).build();
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
        if (method == RequestMethod.PUT && format == null) {
            logger.error("http put body format is null");
            return false;
        }
        if (method == RequestMethod.DELETE && format == null) {
            logger.error("http delete body format is null");
            return false;
        }
        if (method == RequestMethod.PATCH && format == null) {
            logger.error("http patch body format is null");
            return false;
        }
        return true;
    }
}
