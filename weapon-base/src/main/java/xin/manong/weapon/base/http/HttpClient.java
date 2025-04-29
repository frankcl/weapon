package xin.manong.weapon.base.http;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import okhttp3.Authenticator;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
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

    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_READ_TIMEOUT = "Read-Timeout";
    public static final String HEADER_WRITE_TIMEOUT = "Write-Timeout";
    public static final String HEADER_CONNECT_TIMEOUT = "Connect-Timeout";

    private static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";
    private static final String ACCEPT_ALL = "*/*";
    private static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    private static final String BROWSER_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36";

    private final HttpClientConfig config;
    private Proxy proxy;
    private ProxySelector proxySelector;
    private final Authenticator authenticator;
    private OkHttpClient okHttpClient;

    /**
     * 普通模式，使用默认配置，不启用代理
     */
    public HttpClient() {
        this(new HttpClientConfig());
    }

    /**
     * 普通模式，不启用代理
     *
     * @param config http client配置
     */
    public HttpClient(HttpClientConfig config) {
        this(config, (ProxySelector) null, null);
    }

    /**
     * 启用代理及认证机制
     *
     * @param config http client配置信息
     * @param proxy 代理信息
     * @param authenticator 代理认证
     */
    public HttpClient(HttpClientConfig config,
                      Proxy proxy, Authenticator authenticator) {
        this.config = config;
        this.proxy = proxy;
        this.authenticator = authenticator;
        init();
    }

    /**
     * 启用代理选择器及认证机制
     *
     * @param config http client配置信息
     * @param proxySelector 代理选择器
     * @param authenticator 代理认证
     */
    public HttpClient(HttpClientConfig config,
                      ProxySelector proxySelector,
                      Authenticator authenticator) {
        this.config = config;
        this.proxySelector = proxySelector;
        this.authenticator = authenticator;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        try {
            if (!config.check()) throw new IllegalArgumentException("HttpClient config is invalid");
            UnsafeTrustManager unsafeTrustManager = new UnsafeTrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{unsafeTrustManager}, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            ConnectionPool connectionPool = new ConnectionPool(config.maxIdleConnections,
                    config.keepAliveMinutes, TimeUnit.MINUTES);
            OkHttpClient.Builder builder = new OkHttpClient.Builder().
                    retryOnConnectionFailure(true).
                    connectionPool(connectionPool).
                    followRedirects(config.followRedirect).
                    followSslRedirects(config.followSSLRedirect).
                    addInterceptor(new TimeoutModifyInterceptor()).
                    addNetworkInterceptor(new HostModifyInterceptor()).
                    sslSocketFactory(sslSocketFactory, unsafeTrustManager).
                    hostnameVerifier(new UnsafeHostnameVerifier()).
                    connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS).
                    readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS).
                    writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS);
            if (proxySelector != null) {
                builder.proxySelector(proxySelector);
                if (authenticator != null) builder.proxyAuthenticator(authenticator);
            } else if (proxy != null) {
                builder.proxy(proxy);
                if (authenticator != null) builder.proxyAuthenticator(authenticator);
            }
            this.okHttpClient = builder.build();
        } catch (Exception e) {
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
    @NotNull
    public Response execute(HttpRequest httpRequest) throws IOException {
        if (httpRequest == null || !httpRequest.check()) {
            logger.error("Invalid http request");
            throw new IOException("Invalid http request");
        }
        if (httpRequest.method == RequestMethod.GET) return doGet(httpRequest);
        else if (httpRequest.method == RequestMethod.HEAD) return doHead(httpRequest);
        else if (httpRequest.method == RequestMethod.POST) return doPost(httpRequest);
        else if (httpRequest.method == RequestMethod.PUT) return doPut(httpRequest);
        else if (httpRequest.method == RequestMethod.DELETE) return doDelete(httpRequest);
        else if (httpRequest.method == RequestMethod.PATCH) return doPatch(httpRequest);
        throw new UnsupportedOperationException("Unsupported HTTP method:" + httpRequest.method.name());
    }

    /**
     * 编码GET请求URL
     *
     * @param httpRequest HTTP请求
     * @return 编码URL
     */
    private String encodeGetURL(HttpRequest httpRequest) {
        String requestURL = httpRequest.requestURL;
        if (httpRequest.params == null || httpRequest.params.isEmpty()) return requestURL;
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : httpRequest.params.entrySet()) {
            if (!builder.isEmpty()) builder.append("&");
            builder.append(entry.getKey()).append("=").append(
                    URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        int pos = requestURL.indexOf("?");
        return pos == -1 ? String.format("%s?%s", requestURL, builder) : (requestURL.endsWith("&") ?
               String.format("%s%s", requestURL, builder) : String.format("%s&%s", requestURL, builder));
    }

    /**
     * 执行HEAD请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    @NotNull
    private Response doHead(HttpRequest httpRequest) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        String requestURL = encodeGetURL(httpRequest);
        requestBuilder.url(requestURL).head();
        handleRequestHeaders(requestBuilder, httpRequest);
        handleTimeout(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 执行GET请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    @NotNull
    private Response doGet(HttpRequest httpRequest) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        String requestURL = encodeGetURL(httpRequest);
        requestBuilder.url(requestURL).get();
        handleRequestHeaders(requestBuilder, httpRequest);
        handleTimeout(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 执行HTTP POST请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    @NotNull
    private Response doPost(HttpRequest httpRequest) throws IOException {
        RequestBody requestBody = buildRequestBody(httpRequest);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpRequest.requestURL).post(requestBody);
        handleRequestHeaders(requestBuilder, httpRequest);
        handleTimeout(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 执行HTTP DELETE请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    @NotNull
    private Response doDelete(HttpRequest httpRequest) throws IOException {
        RequestBody requestBody = buildRequestBody(httpRequest);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpRequest.requestURL).delete(requestBody);
        handleRequestHeaders(requestBuilder, httpRequest);
        handleTimeout(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 执行HTTP PUT请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    @NotNull
    private Response doPut(HttpRequest httpRequest) throws IOException {
        RequestBody requestBody = buildRequestBody(httpRequest);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpRequest.requestURL).put(requestBody);
        handleRequestHeaders(requestBuilder, httpRequest);
        handleTimeout(requestBuilder, httpRequest);
        Request request = requestBuilder.build();
        return executeRequest(request);
    }

    /**
     * 执行HTTP PATCH请求
     *
     * @param httpRequest HTTP请求
     * @return HTTP响应对象
     */
    @NotNull
    private Response doPatch(HttpRequest httpRequest) throws IOException {
        RequestBody requestBody = buildRequestBody(httpRequest);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpRequest.requestURL).patch(requestBody);
        handleRequestHeaders(requestBuilder, httpRequest);
        handleTimeout(requestBuilder, httpRequest);
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
     * @param builder HTTP请求builder
     * @param httpRequest HTTP请求
     */
    private void handleRequestHeaders(Request.Builder builder, HttpRequest httpRequest) {
        if (httpRequest.headers == null || !httpRequest.headers.containsKey(HEADER_USER_AGENT)) {
            builder.addHeader(HEADER_USER_AGENT, BROWSER_USER_AGENT);
        }
        if (httpRequest.headers == null || !httpRequest.headers.containsKey(HEADER_CONNECTION)) {
            builder.addHeader(HEADER_CONNECTION, CONNECTION_KEEP_ALIVE);
        }
        if (httpRequest.headers == null || !httpRequest.headers.containsKey(HEADER_ACCEPT)) {
            builder.addHeader(HEADER_ACCEPT, ACCEPT_ALL);
        }
        if (httpRequest.headers == null || httpRequest.headers.isEmpty()) return;
        for (Map.Entry<String, String> entry : httpRequest.headers.entrySet()) {
            if (StringUtils.isEmpty(entry.getKey()) || entry.getValue() == null) continue;
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 处理请求超时设置
     *
     * @param builder HTTP请求builder
     * @param httpRequest HTTP请求
     */
    private void handleTimeout(Request.Builder builder, HttpRequest httpRequest) {
        if (httpRequest.connectTimeoutMs != null && httpRequest.connectTimeoutMs > 0) {
            builder.addHeader(HEADER_CONNECT_TIMEOUT, String.valueOf(httpRequest.connectTimeoutMs));
        }
        if (httpRequest.readTimeoutMs != null && httpRequest.readTimeoutMs > 0) {
            builder.addHeader(HEADER_READ_TIMEOUT, String.valueOf(httpRequest.readTimeoutMs));
        }
        if (httpRequest.writeTimeoutMs != null && httpRequest.writeTimeoutMs > 0) {
            builder.addHeader(HEADER_WRITE_TIMEOUT, String.valueOf(httpRequest.writeTimeoutMs));
        }
    }

    /**
     * 执行HTTP请求
     *
     * @param request HTTP请求
     * @return HTTP响应
     */
    @NotNull
    private Response executeRequest(Request request) throws IOException {
        for (int i = 0; i < config.retryCnt; i++) {
            try {
                return okHttpClient.newCall(request).execute();
            } catch (SocketTimeoutException e) {
                logger.warn("Fetch timeout for url:{}, retry {} times", request.url().url(), i + 1);
                logger.error(e.getMessage(), e);
                if (i >= config.retryCnt - 1) throw e;
            }
        }
        logger.error("Fetch failed for url: {}", request.url().url());
        throw new IOException("Fetch failed");
    }
}
