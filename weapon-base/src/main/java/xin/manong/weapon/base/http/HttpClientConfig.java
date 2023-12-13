package xin.manong.weapon.base.http;

/**
 * OKHttpClient配置信息
 *
 * @author frankcl
 * @date 2022-06-29 15:06:54
 */
public class HttpClientConfig {

    private final static boolean DEFAULT_FOLLOW_REDIRECT = true;
    private final static boolean DEFAULT_FOLLOW_SSL_REDIRECT = true;
    private final static int DEFAULT_RETRY_COUNT = 3;
    private final static int DEFAULT_KEEP_ALIVE_MINUTES = 3;
    private final static int DEFAULT_MAX_IDLE_CONNECTIONS = 100;
    private final static long DEFAULT_CONNECT_TIMEOUT_SECONDS = 5;
    private final static long DEFAULT_READ_TIMEOUT_SECONDS = 10;
    private final static long DEFAULT_WRITE_TIMEOUT_SECONDS = 10;

    public boolean followRedirect = DEFAULT_FOLLOW_REDIRECT;
    public boolean followSSLRedirect = DEFAULT_FOLLOW_SSL_REDIRECT;
    public int retryCnt = DEFAULT_RETRY_COUNT;
    public int keepAliveMinutes = DEFAULT_KEEP_ALIVE_MINUTES;
    public int maxIdleConnections = DEFAULT_MAX_IDLE_CONNECTIONS;
    public long connectTimeoutSeconds = DEFAULT_CONNECT_TIMEOUT_SECONDS;
    public long readTimeoutSeconds = DEFAULT_READ_TIMEOUT_SECONDS;
    public long writeTimeoutSeconds = DEFAULT_WRITE_TIMEOUT_SECONDS;

    /**
     * 检测有效性
     *
     * @return 如果有效返回true，否则返回false
     */
    public boolean check() {
        if (retryCnt <= 0) retryCnt = DEFAULT_RETRY_COUNT;
        if (keepAliveMinutes <= 0) keepAliveMinutes = DEFAULT_KEEP_ALIVE_MINUTES;
        if (maxIdleConnections <= 0) maxIdleConnections = DEFAULT_MAX_IDLE_CONNECTIONS;
        if (connectTimeoutSeconds <= 0) connectTimeoutSeconds = DEFAULT_CONNECT_TIMEOUT_SECONDS;
        if (readTimeoutSeconds <= 0) readTimeoutSeconds = DEFAULT_READ_TIMEOUT_SECONDS;
        if (writeTimeoutSeconds <= 0) writeTimeoutSeconds = DEFAULT_WRITE_TIMEOUT_SECONDS;
        return true;
    }
}
