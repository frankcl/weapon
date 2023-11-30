package xin.manong.weapon.base.http;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OKHttpClient封装
 *
 * @author frankcl
 * @date 2022-06-29 14:57:18
 */
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_CONNECTION = "Connection";
    private static final String HEADER_ACCEPT = "Accept";

    private static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";
    private static final String BROWSER_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36";

    private HttpClientConfig config;
    private OkHttpClient okHttpClient;

    public HttpClient() {
        this(new HttpClientConfig());
    }

    public HttpClient(HttpClientConfig config) {
        this.config = config;
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        try {
            logger.info("http client is init ...");
            if (!config.check()) throw new RuntimeException("http client config is invalid");
            UnsafeTrustManager unsafeTrustManager = new UnsafeTrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{unsafeTrustManager}, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            ConnectionPool connectionPool = new ConnectionPool(config.maxIdleConnections,
                    config.keepAliveMinutes, TimeUnit.MINUTES);
            this.okHttpClient = new OkHttpClient.Builder().
                    retryOnConnectionFailure(true).
                    connectionPool(connectionPool).
                    followRedirects(config.followRedirect).
                    followSslRedirects(config.followSSLRedirect).
                    addNetworkInterceptor(new RequestInterceptor()).
                    sslSocketFactory(sslSocketFactory, unsafeTrustManager).
                    hostnameVerifier(new UnsafeHostnameVerifier()).
                    connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS).
                    readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS).build();
            logger.info("http client init success");
        } catch (Exception e) {
            logger.info("http client init failed");
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行HTTP请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应
     */
    public Response execute(HttpRequest httpRequest) {
        if (httpRequest == null || !httpRequest.check()) {
            logger.error("invalid http request");
            return null;
        }
        if (httpRequest.method == RequestMethod.GET) return doGet(httpRequest);
        else if (httpRequest.method == RequestMethod.HEAD) return doHead(httpRequest);
        else if (httpRequest.method == RequestMethod.POST) return doPost(httpRequest);
        else if (httpRequest.method == RequestMethod.PUT) return doPut(httpRequest);
        else if (httpRequest.method == RequestMethod.DELETE) return doDelete(httpRequest);
        else if (httpRequest.method == RequestMethod.PATCH) return doPatch(httpRequest);
        return null;
    }

    /**
     * 编码GET请求URL
     *
     * @param httpRequest HTTP请求
     * @return 编码URL
     * @throws UnsupportedEncodingException
     */
    private String encodeGetURL(HttpRequest httpRequest) throws UnsupportedEncodingException {
        String requestURL = httpRequest.requestURL;
        if (httpRequest.params == null || httpRequest.params.isEmpty()) return requestURL;
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, Object> entry : httpRequest.params.entrySet()) {
            if (buffer.length() > 0) buffer.append("&");
            buffer.append(entry.getKey()).append("=").append(
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }
        int pos = requestURL.indexOf("?");
        return pos == -1 ? String.format("%s?%s", requestURL, buffer) : (requestURL.endsWith("&") ?
               String.format("%s%s", requestURL, buffer) : String.format("%s&%s", requestURL, buffer));
    }

    /**
     * 执行HEAD请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    private Response doHead(HttpRequest httpRequest) {
        try {
            Request.Builder requestBuilder = new Request.Builder();
            String requestURL = encodeGetURL(httpRequest);
            requestBuilder.url(requestURL).head();
            handleRequestHeaders(requestBuilder, httpRequest);
            Request request = requestBuilder.build();
            return executeRequest(request);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 执行GET请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    private Response doGet(HttpRequest httpRequest) {
        try {
            Request.Builder requestBuilder = new Request.Builder();
            String requestURL = encodeGetURL(httpRequest);
            requestBuilder.url(requestURL).get();
            handleRequestHeaders(requestBuilder, httpRequest);
            Request request = requestBuilder.build();
            return executeRequest(request);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 执行HTTP POST请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    private Response doPost(HttpRequest httpRequest) {
        RequestBody requestBody = buildRequestBody(httpRequest);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpRequest.requestURL).post(requestBody);
        handleRequestHeaders(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 执行HTTP DELETE请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    private Response doDelete(HttpRequest httpRequest) {
        RequestBody requestBody = buildRequestBody(httpRequest);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpRequest.requestURL).delete(requestBody);
        handleRequestHeaders(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 执行HTTP PUT请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    private Response doPut(HttpRequest httpRequest) {
        RequestBody requestBody = buildRequestBody(httpRequest);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpRequest.requestURL).put(requestBody);
        handleRequestHeaders(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 执行HTTP PATCH请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    private Response doPatch(HttpRequest httpRequest) {
        RequestBody requestBody = buildRequestBody(httpRequest);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpRequest.requestURL).patch(requestBody);
        handleRequestHeaders(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 构建请求体
     * HTTP GET/PUT/DELETE请求
     *
     * @param httpRequest HTTP请求
     * @return 请求体
     */
    private RequestBody buildRequestBody(HttpRequest httpRequest) {
        if (httpRequest.params == null || httpRequest.params.isEmpty()) {
            return RequestBody.create("", null);
        } else if (httpRequest.format == RequestFormat.FORM) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : httpRequest.params.entrySet()) {
                if (StringUtils.isEmpty(entry.getKey()) || entry.getValue() == null) continue;
                builder.add(entry.getKey(), entry.getValue().toString());
            }
            return builder.build();
        } else {
            JSONObject body = new JSONObject(httpRequest.params);
            return RequestBody.create(body.toJSONString(), MediaType.parse(MEDIA_TYPE_JSON));
        }
    }

    /**
     * 处理请求header
     *
     * @param builder 请求builder
     * @param httpRequest HTTP请求
     */
    private void handleRequestHeaders(Request.Builder builder, HttpRequest httpRequest) {
        if (httpRequest.headers == null || !httpRequest.headers.containsKey(HEADER_USER_AGENT)) {
            builder.addHeader(HEADER_USER_AGENT, BROWSER_USER_AGENT);
        }
        if (httpRequest.headers == null || !httpRequest.headers.containsKey(HEADER_CONNECTION)) {
            builder.addHeader(HEADER_CONNECTION, "keep-alive");
        }
        if (httpRequest.headers == null || !httpRequest.headers.containsKey(HEADER_ACCEPT)) {
            builder.addHeader(HEADER_ACCEPT, "*/*");
        }
        if (httpRequest.headers == null || httpRequest.headers.isEmpty()) return;
        for (Map.Entry<String, String> entry : httpRequest.headers.entrySet()) {
            if (StringUtils.isEmpty(entry.getKey()) || entry.getValue() == null) continue;
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 执行HTTP请求
     *
     * @param request HTTP请求
     * @return HTTP响应
     */
    private Response executeRequest(Request request) {
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                return okHttpClient.newCall(request).execute();
            } catch (Exception e) {
                logger.warn("get resource failed for request url[{}], retry it", request.url().url());
                logger.error(e.getMessage(), e);
            }
        }
        logger.error("get resource failed for request url[{}]", request.url().url());
        return null;
    }
}
