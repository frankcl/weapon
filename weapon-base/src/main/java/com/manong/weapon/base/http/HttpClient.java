package com.manong.weapon.base.http;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.net.URL;
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

    private final static Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private final static String BROWSER_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36";

    private HttpClientConfig config;
    private OkHttpClient okHttpClient;

    public HttpClient(HttpClientConfig config) {
        this.config = config;
    }

    /**
     * 初始化
     *
     * @return 如果成功返回true，否则返回false
     */
    public boolean init() {
        try {
            logger.info("http client is init ...");
            if (!config.check()) return false;
            UnsafeTrustManager unsafeTrustManager = new UnsafeTrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{unsafeTrustManager}, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            ConnectionPool connectionPool = new ConnectionPool(config.maxIdleConnections,
                    config.keepAliveMinutes, TimeUnit.MINUTES);
            this.okHttpClient = new OkHttpClient.Builder().retryOnConnectionFailure(true).
                    connectionPool(connectionPool).followRedirects(true).followSslRedirects(true).
                    sslSocketFactory(sslSocketFactory, unsafeTrustManager).
                    hostnameVerifier(new UnsafeHostnameVerifier()).
                    connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS).
                    readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS).build();
            logger.info("http client init success");
            return true;
        } catch (Exception e) {
            logger.info("http client init failed");
            logger.error(e.getMessage(), e);
            return false;
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
        else if (httpRequest.method == RequestMethod.POST) return doPost(httpRequest);
        return null;
    }

    /**
     * 执行GET请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    private Response doGet(HttpRequest httpRequest) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpRequest.requestURL);
        handleRequestHeaders(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 通过JSON方式执行POST请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    private Response doPost(HttpRequest httpRequest) {
        RequestBody requestBody = RequestBody.create("", null);
        if (httpRequest.params != null) {
            if (httpRequest.format == RequestFormat.FORM) {
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, Object> entry : httpRequest.params.entrySet()) {
                    if (StringUtils.isEmpty(entry.getKey()) || entry.getValue() == null) continue;
                    builder.add(entry.getKey(), entry.getValue().toString());
                }
                requestBody = builder.build();
            } else {
                JSONObject body = new JSONObject(httpRequest.params);
                requestBody = RequestBody.create(body.toJSONString(), MediaType.parse("application/json; charset=utf-8"));
            }
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpRequest.requestURL).post(requestBody);
        handleRequestHeaders(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 处理请求header
     *
     * @param builder 请求builder
     * @param httpRequest HTTP请求
     */
    private void handleRequestHeaders(Request.Builder builder, HttpRequest httpRequest) {
        builder.addHeader("User-Agent", BROWSER_USER_AGENT);
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Accept", "*/*");
        try {
            URL url = new URL(httpRequest.requestURL);
            String host = url.getHost();
            if (!StringUtils.isEmpty(host)) builder.addHeader("Host", host);
        } catch (Exception e) {
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
