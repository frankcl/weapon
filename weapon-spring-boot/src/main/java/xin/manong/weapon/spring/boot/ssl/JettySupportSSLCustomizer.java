package xin.manong.weapon.spring.boot.ssl;

import lombok.Data;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * 同时支持HTTP和HTTPS的Jetty Web服务工厂
 * 通过配置forceRedirect控制HTTP是否跳转HTTPS
 *
 * @author frankcl
 * @date 2025-03-25 16:01:35
 */
@Data
@Configuration
@ConfigurationProperties("server")
public class JettySupportSSLCustomizer implements WebServerFactoryCustomizer<ConfigurableJettyWebServerFactory> {

    private boolean forceRedirect = false;
    private int insecurePort = 80;
    private int port = 443;

    @Override
    public void customize(ConfigurableJettyWebServerFactory factory) {
        factory.addServerCustomizers(server -> {
            HttpConfiguration httpConfiguration = new HttpConfiguration();
            httpConfiguration.setSecureScheme(HttpScheme.HTTPS.asString());
            httpConfiguration.setSecurePort(port);
            ServerConnector connector = new ServerConnector(server);
            connector.addConnectionFactory(new HttpConnectionFactory(httpConfiguration));
            connector.setPort(insecurePort);
            server.addConnector(connector);
            if (forceRedirect) {
                SecuredRedirectHandler redirectHandler = new SecuredRedirectHandler();
                ContextHandlerCollection handlerCollection = new ContextHandlerCollection();
                for (Handler handler : server.getHandlers()) handlerCollection.addHandler(handler);
                redirectHandler.setHandler(handlerCollection);
                server.setHandler(redirectHandler);
            }
        });
    }
}
