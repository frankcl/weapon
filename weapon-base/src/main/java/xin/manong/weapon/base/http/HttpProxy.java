package xin.manong.weapon.base.http;


import org.apache.commons.lang3.StringUtils;

import java.net.Proxy;
import java.net.SocketAddress;

/**
 * HTTP代理封装
 * 支持用户名和密码
 *
 * @author frankcl
 * @date 2023-12-12 15:53:23
 */
public class HttpProxy extends Proxy {

    public String username;
    public String password;

    /**
     * Creates an entry representing a PROXY connection.
     * Certain combinations are illegal. For instance, for types Http, and
     * Socks, a SocketAddress <b>must</b> be provided.
     * <p>
     * Use the {@code Proxy.NO_PROXY} constant
     * for representing a direct connection.
     *
     * @param type the {@code Type} of the proxy
     * @param socketAddress   the {@code SocketAddress} for that proxy
     * @throws IllegalArgumentException when the type and the address are
     *                                  incompatible
     */
    public HttpProxy(Type type, SocketAddress socketAddress) {
        super(type, socketAddress);
    }

    public HttpProxy(Type type, SocketAddress socketAddress,
                     String username, String password) {
        this(type, socketAddress);
        if (StringUtils.isEmpty(username)) throw new IllegalArgumentException("Username is empty");
        if (StringUtils.isEmpty(password)) throw new IllegalArgumentException("Password is empty");
        this.username = username;
        this.password = password;
    }
}
