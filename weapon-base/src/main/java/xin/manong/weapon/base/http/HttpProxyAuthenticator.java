package xin.manong.weapon.base.http;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HTTP代理认证
 *
 * @author frankcl
 * @date 2023-12-12 16:13:15
 */
public class HttpProxyAuthenticator implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(HttpProxyAuthenticator.class);

    private static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (response.request().header(HEADER_PROXY_AUTHORIZATION) != null) return null;
        HttpProxy proxy = (HttpProxy) route.proxy();
        if (proxy == null) {
            logger.warn("http proxy is not found");
            return null;
        }
        if (StringUtils.isEmpty(proxy.username) || StringUtils.isEmpty(proxy.password)) return null;
        String credential = Credentials.basic(proxy.username, proxy.password);
        return response.request().newBuilder().header(HEADER_PROXY_AUTHORIZATION, credential).build();
    }
}
