package xin.manong.weapon.base.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * HTTP超时修改拦截处理
 *
 * @author frankcl
 * @date 2023-02-09 18:19:12
 */
public class TimeoutModifyInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        chain = resetTimeout(chain, HttpClient.HEADER_CONNECT_TIMEOUT);
        chain = resetTimeout(chain, HttpClient.HEADER_READ_TIMEOUT);
        chain = resetTimeout(chain, HttpClient.HEADER_WRITE_TIMEOUT);
        return chain.proceed(request);
    }

    /**
     * 重置超时时间
     *
     * @param chain 请求链
     * @param headerName 超时header
     * @return 修改后chain
     */
    private Chain resetTimeout(Chain chain, String headerName) {
        Request request = chain.request();
        String headerValue = request.header(headerName);
        if (StringUtils.isEmpty(headerValue)) return chain;
        int timeout = Integer.parseInt(headerValue);
        switch (headerName) {
            case HttpClient.HEADER_READ_TIMEOUT:
                chain = chain.withReadTimeout(timeout, TimeUnit.MILLISECONDS);
                break;
            case HttpClient.HEADER_WRITE_TIMEOUT:
                chain = chain.withWriteTimeout(timeout, TimeUnit.MILLISECONDS);
                break;
            case HttpClient.HEADER_CONNECT_TIMEOUT:
                chain = chain.withConnectTimeout(timeout, TimeUnit.MILLISECONDS);
                break;
        }
        return chain;
    }
}
