package xin.manong.weapon.spring.web.ws.handler;

import org.apache.commons.lang3.StringUtils;
import xin.manong.weapon.spring.web.WebResponse;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 异常处理器：异常统一转换为WebResponse
 *
 * @author frankcl
 * @date 2022-09-19 16:32:41
 */
public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof WebApplicationException) {
            WebApplicationException webApplicationException = (WebApplicationException) e;
            return Response.status(Response.Status.OK.getStatusCode()).type(MediaType.APPLICATION_JSON).entity(
                    WebResponse.buildError(webApplicationException.getResponse().getStatus(), e.getMessage())).build();
        }
        String message = e.getMessage();
        if (StringUtils.isEmpty(message) && e instanceof UndeclaredThrowableException) {
            message = ((UndeclaredThrowableException) e).getUndeclaredThrowable().getMessage();
        }
        return Response.status(Response.Status.OK.getStatusCode()).type(MediaType.APPLICATION_JSON).entity(
                WebResponse.buildError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message)).build();
    }
}
