package xin.manong.weapon.jersey.handler;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.jersey.WebResponse;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * 异常处理器：异常统一转换为WebResponse
 *
 * @author frankcl
 * @date 2022-09-19 16:32:41
 */
public class ExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof UndeclaredThrowableException) {
            Exception cause = (Exception) (((UndeclaredThrowableException) e).getUndeclaredThrowable().getCause());
            if (cause != null) e = cause;
        }
        if (e instanceof WebApplicationException webApplicationException) {
            return Response.status(Response.Status.OK.getStatusCode()).type(MediaType.APPLICATION_JSON).entity(
                    WebResponse.buildError(webApplicationException.getResponse().getStatus(), e.getMessage())).build();
        }
        logger.error(e.getMessage(), e);
        String message = e.getMessage();
        return Response.status(Response.Status.OK.getStatusCode()).type(MediaType.APPLICATION_JSON).entity(
                WebResponse.buildError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message)).build();
    }
}
