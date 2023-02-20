package xin.manong.weapon.base.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import xin.manong.weapon.base.util.CommonUtil;

import java.io.IOException;

/**
 * HTTP请求拦截处理
 * 处理请求头Host与实际请求URL不一致问题
 *
 * @author frankcl
 * @date 2023-02-09 18:19:12
 */
public class RequestInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        String requestHost = request.url().url().getHost();
        String host = request.header("Host");
        if (StringUtils.isNotEmpty(host) && !host.equals(requestHost) && !CommonUtil.isIP(requestHost)) {
            request = request.newBuilder().header("Host", requestHost).build();
        }
        return chain.proceed(request);
    }
}
