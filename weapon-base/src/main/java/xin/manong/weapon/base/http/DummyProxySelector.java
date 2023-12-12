package xin.manong.weapon.base.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * dummy代理选择器
 *
 * @author frankcl
 * @date 2023-12-12 15:31:05
 */
public class DummyProxySelector extends ProxySelector {

    private static final Logger logger = LoggerFactory.getLogger(DummyProxySelector.class);

    private Random random;
    private List<Proxy> proxies;

    public DummyProxySelector(List<Proxy> proxies) {
        this.random = new Random();
        this.proxies = proxies;
    }

    @Override
    public List<Proxy> select(URI uri) {
        return proxies == null || proxies.isEmpty() ? new ArrayList<>() :
                Collections.singletonList(proxies.get(random.nextInt(proxies.size())));
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        logger.warn("connect proxy failed");
    }
}
