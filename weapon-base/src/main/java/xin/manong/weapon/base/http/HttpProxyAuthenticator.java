package xin.manong.weapon.base.http;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * HTTP代理认证
 *
 * @author frankcl
 * @date 2023-12-12 16:13:15
 */
public class HttpProxyAuthenticator implements Authenticator {

    private static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (response.request().header(HEADER_PROXY_AUTHORIZATION) != null) return null;
        HttpProxy proxy = (HttpProxy) route.proxy();
        if (StringUtils.isEmpty(proxy.username) || StringUtils.isEmpty(proxy.password)) return null;
        if (response.code() == 407) {
            String credential = Credentials.basic(proxy.username, proxy.password);
            return response.request().newBuilder().header(HEADER_PROXY_AUTHORIZATION, credential).build();
        }
        return null;
    }
}
