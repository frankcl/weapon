package xin.manong.weapon.base.http;

import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.util.HashSet;
import java.util.Set;

/**
 * 不安全的站点证书验证器
 *
 * @author frankcl
 * @date 2022-06-29 16:44:54
 */
public class UnsafeHostnameVerifier implements HostnameVerifier {

    private final Set<String> verifiedHosts;

    public UnsafeHostnameVerifier() {
        this.verifiedHosts = new HashSet<>();
    }

    public UnsafeHostnameVerifier(Set<String> verifiedHosts) {
        this.verifiedHosts = verifiedHosts;
    }

    @Override
    public boolean verify(String hostname, SSLSession sslSession) {
        if (StringUtils.isEmpty(hostname)) return false;
        return verifiedHosts == null || !verifiedHosts.contains(hostname);
    }
}
