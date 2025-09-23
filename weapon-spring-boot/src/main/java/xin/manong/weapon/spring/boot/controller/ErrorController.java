package xin.manong.weapon.spring.boot.controller;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 错误处理控制器
 *
 * @author frankcl
 * @date 2025-09-23 21:51:09
 */
@RestController
@Controller
@Path("error")
@RequestMapping("error")
public class ErrorController {

    public static final String ATTRIBUTE_EXCEPTION = "exception";

    /**
     * 错误处理
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String check(@Context ServletContext servletContext) throws Exception {
        Exception e = (Exception) servletContext.getAttribute(ATTRIBUTE_EXCEPTION);
        if (e != null) throw e;
        throw new InternalServerErrorException("Unknown error");
    }
}
