package xin.manong.weapon.jersey.handler;

import xin.manong.weapon.jersey.WebResponse;

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
        if (e instanceof UndeclaredThrowableException) {
            Exception cause = (Exception) (((UndeclaredThrowableException) e).getUndeclaredThrowable().getCause());
            if (cause != null) e = cause;
        }
        if (e instanceof WebApplicationException) {
            WebApplicationException webApplicationException = (WebApplicationException) e;
            return Response.status(Response.Status.OK.getStatusCode()).type(MediaType.APPLICATION_JSON).entity(
                    WebResponse.buildError(webApplicationException.getResponse().getStatus(), e.getMessage())).build();
        }
        String message = e.getMessage();
        return Response.status(Response.Status.OK.getStatusCode()).type(MediaType.APPLICATION_JSON).entity(
                WebResponse.buildError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message)).build();
    }
}
