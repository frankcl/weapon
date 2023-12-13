package xin.manong.weapon.base.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * HTTP超时修改拦截处理
 *
 * @author frankcl
 * @date 2023-02-09 18:19:12
 */
public class TimeoutModifyInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        chain = resetTimeout(chain, HttpClient.HEADER_CONNECT_TIMEOUT_MS);
        chain = resetTimeout(chain, HttpClient.HEADER_READ_TIMEOUT_MS);
        chain = resetTimeout(chain, HttpClient.HEADER_WRITE_TIMEOUT_MS);
        return chain.proceed(request);
    }

    /**
     * 重置超时时间
     *
     * @param chain 请求链
     * @param timeoutHeader 超时header
     * @return 修改后chain
     */
    private Chain resetTimeout(Chain chain, String timeoutHeader) {
        Request request = chain.request();
        String value = request.header(timeoutHeader);
        if (StringUtils.isEmpty(value)) return chain;
        int timeout = Integer.parseInt(value);
        if (timeoutHeader.equals(HttpClient.HEADER_READ_TIMEOUT_MS)) {
            chain = chain.withReadTimeout(timeout, TimeUnit.MILLISECONDS);
        } else if (timeoutHeader.equals(HttpClient.HEADER_WRITE_TIMEOUT_MS)) {
            chain = chain.withWriteTimeout(timeout, TimeUnit.MILLISECONDS);
        } else if (timeoutHeader.equals(HttpClient.HEADER_CONNECT_TIMEOUT_MS)) {
            chain = chain.withConnectTimeout(timeout, TimeUnit.MILLISECONDS);
        }
        return chain;
    }
}
