package xin.manong.weapon.jersey.filter;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import xin.manong.weapon.jersey.WebResponse;

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
        if (mediaType == null || !mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) return;
        if (containerResponseContext.getEntity() instanceof WebResponse) return;
        containerResponseContext.setEntity(WebResponse.buildOK(containerResponseContext.getEntity()));
    }
}
