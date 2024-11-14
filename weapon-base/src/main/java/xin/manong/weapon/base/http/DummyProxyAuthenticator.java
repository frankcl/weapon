package xin.manong.weapon.base.http;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * dummy代理认证
 *
 * @author frankcl
 * @date 2023-12-12 16:13:15
 */
public class DummyProxyAuthenticator implements Authenticator {

    private static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";

    private final String username;
    private final String password;

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
