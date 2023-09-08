package xin.manong.weapon.spring.web.ws.filter;

import xin.manong.weapon.spring.web.WebResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * web响应封装
 *
 * @author frankcl
 * @date 2022-09-17 17:12:30
 */
@Provider
@Consumes
@Produces
public class WebResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext containerRequestContext,
                       ContainerResponseContext containerResponseContext) throws IOException {
        MediaType mediaType = containerResponseContext.getMediaType();
        if (mediaType == null || mediaType != MediaType.APPLICATION_JSON_TYPE) return;
        if (containerResponseContext.getEntity() instanceof WebResponse) return;
        containerResponseContext.setEntity(WebResponse.buildOK(containerResponseContext.getEntity()));
    }
}
