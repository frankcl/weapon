package xin.manong.weapon.base.http;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * dummy代理认证
 *
 * @author frankcl
 * @date 2023-12-12 16:13:15
 */
public class DummyProxyAuthenticator implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(DummyProxyAuthenticator.class);

    private static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";

    private String username;
    private String password;

    public DummyProxyAuthenticator(String username, String password) {
        if (StringUtils.isEmpty(username)) throw new IllegalArgumentException("username is empty");
        if (StringUtils.isEmpty(password)) throw new IllegalArgumentException("password is empty");
        this.username = username;
        this.password = password;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (response.request().header(HEADER_PROXY_AUTHORIZATION) != null) return null;
        if (response.code() == 407) {
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder().header(HEADER_PROXY_AUTHORIZATION, credential).build();
        }
        return null;
    }
}
