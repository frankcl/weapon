package xin.manong.weapon.base.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import xin.manong.weapon.base.util.CommonUtil;

import java.io.IOException;

/**
 * Host修改拦截处理
 * 当HTTP header中Host与实际请求Host不一致时，修正HTTP header
 *
 * @author frankcl
 * @date 2023-02-09 18:19:12
 */
public class HostModifyInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String requestHost = request.url().url().getHost();
        String headerHost = request.header(HttpClient.HEADER_HOST);
        if (StringUtils.isNotEmpty(headerHost) && !headerHost.equals(requestHost) && !CommonUtil.isIP(requestHost)) {
            request = request.newBuilder().header(HttpClient.HEADER_HOST, requestHost).build();
        }
        return chain.proceed(request);
    }
}
