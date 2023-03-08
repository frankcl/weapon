package xin.manong.weapon.spring.web.ws.aspect;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.common.ThreadContext;
import xin.manong.weapon.base.log.JSONLogger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web日志切面
 *
 * @author frankcl
 * @date 2022-08-23 13:04:01
 */
@Component
@Aspect
public class WebLogAspect {

    private final static Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    private final static String HTTP_METHOD_POST = "POST";
    private final static String HTTP_METHOD_PUT = "PUT";

    private final static List<String> ADDRESS_HEADERS = new ArrayList<String>() { {
        add("HTTP_CLIENT_IP");
        add("x-forwarded-for");
        add("Proxy-Client-IP");
        add("WL-Proxy-Client-IP");
        add("HTTP_X_FORWARDED_FOR");
    } };

    @Resource
    protected JSONLogger webAspectLogger;

    @Pointcut("@annotation(xin.manong.weapon.spring.web.ws.aspect.EnableWebLogAspect) && execution(public * *(..))")
    public void intercept() {
    }

    @Before("intercept()")
    public void beforeIntercept(JoinPoint joinPoint) {
    }

    @AfterReturning(value = "intercept()", returning = "response")
    public void afterReturn(JoinPoint joinPoint, Object response) {
    }

    /**
     * 拦截控制器方法，进行日志记录
     * 1. 记录服务请求
     * 2. 记录服务响应
     *
     * @param joinPoint 拦截目标
     * @return 响应对象
     */
    @Around("intercept()")
    public Object aroundIntercept(ProceedingJoinPoint joinPoint) throws Exception {
        long startTime = System.currentTimeMillis();
        Object returnObject = null;
        Context context = new Context();
        try {
            ThreadContext.setContext(context);
            processHttpRequest(context);
            processRequest(joinPoint, context);
            returnObject = joinPoint.proceed();
            context.put(WebAspectConstants.SUCCESS, true);
            return returnObject;
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            context.put(WebAspectConstants.SUCCESS, false);
            context.put(WebAspectConstants.MESSAGE, t.getMessage());
            context.put(WebAspectConstants.STACK_TRACE, ExceptionUtils.getStackTrace(t));
            throw new Exception(t);
        } finally {
            if (returnObject != null) context.put(WebAspectConstants.RESPONSE, returnObject);
            context.put(WebAspectConstants.PROCESS_TIME, System.currentTimeMillis() - startTime);
            if (webAspectLogger != null) webAspectLogger.commit(context.featureMap);
            else logger.warn("web aspect logger is null");
            ThreadContext.removeContext();
        }
    }

    /**
     * 获取请求路径
     *
     * @param joinPoint 切入点
     * @return 存在返回请求路径，否则返回null
     */
    private String getRequestPath(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Path classPath = joinPoint.getTarget().getClass().getAnnotation(Path.class);
        Path methodPath = method.getAnnotation(Path.class);
        String path = classPath == null ? "" : classPath.value();
        if (methodPath == null || StringUtils.isEmpty(methodPath.value())) return path;
        if (StringUtils.isEmpty(path)) return methodPath.value();
        return String.format(!path.endsWith("/") && !methodPath.value().startsWith("/") ?
                    "%s/%s" : "%s%s", path, methodPath.value());
    }

    /**
     * 获取请求方式
     *
     * @param joinPoint 切入点
     * @return 存在返回请求方式，否则返回null
     */
    private String getRequestMethod(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (method.getAnnotation(GET.class) != null) return GET.class.getSimpleName();
        else if (method.getAnnotation(POST.class) != null) return POST.class.getSimpleName();
        else if (method.getAnnotation(PUT.class) != null) return PUT.class.getSimpleName();
        else if (method.getAnnotation(DELETE.class) != null) return DELETE.class.getSimpleName();
        return null;
    }

    /**
     * 处理服务请求
     *
     * @param joinPoint 切面点
     * @param context 上下文
     */
    private void processRequest(JoinPoint joinPoint, Context context) {
        if (context == null) return;
        String path = getRequestPath(joinPoint);
        if (StringUtils.isEmpty(path)) return;
        context.put(WebAspectConstants.PATH, path);
        String requestMethod = getRequestMethod(joinPoint);
        if (StringUtils.isEmpty(requestMethod)) return;
        context.put(WebAspectConstants.METHOD, requestMethod);
        if (joinPoint.getArgs() == null) return;
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Parameter[] parameters = method.getParameters();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        String bodyKey = null;
        Map<String, Object> requestMap = new HashMap<>();
        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            Object arg = joinPoint.getArgs()[i];
            if (arg instanceof HttpServletRequest) continue;
            if (arg instanceof HttpServletResponse) continue;
            if (arg instanceof InputStream) continue;
            if (requestMethod.equals(HTTP_METHOD_POST) || requestMethod.equals(HTTP_METHOD_PUT)) {
                bodyKey = parameters[i].getName();
                requestMap.put(parameters[i].getName(), arg);
                continue;
            }
            String annotatedKey = getAnnotatedParamKey(paramAnnotations[i]);
            requestMap.put(StringUtils.isEmpty(annotatedKey) ? parameters[i].getName() : annotatedKey, arg);
        }
        if (requestMap.isEmpty()) return;
        context.put(WebAspectConstants.REQUEST, requestMap);
        if ((requestMethod.equals(HTTP_METHOD_POST) || requestMethod.equals(HTTP_METHOD_PUT)) &&
                requestMap.size() == 1) {
            context.put(WebAspectConstants.REQUEST, JSON.toJSON(requestMap.get(bodyKey)));
        }
    }

    /**
     * 通过注解获取参数名
     *
     * @param annotations 参数注解数组
     * @return 存在返回注解参数名，否则返回null
     */
    private String getAnnotatedParamKey(Annotation[] annotations) {
        if (annotations == null || annotations.length == 0) return null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof QueryParam) return ((QueryParam) annotation).value();
            else if (annotation instanceof PathParam) return ((PathParam) annotation).value();
        }
        return null;
    }

    /**
     * 处理HTTP请求
     * 1. 记录访问IP信息
     *
     * @param context 上下文
     */
    private void processHttpRequest(Context context) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.
                currentRequestAttributes()).getRequest();
        if (httpRequest == null || context == null) return;
        String remoteAddress = httpRequest.getRemoteAddr();
        for (String header : ADDRESS_HEADERS) {
            String value = httpRequest.getHeader(header);
            if (StringUtils.isEmpty(value)) continue;
            if (value.equalsIgnoreCase("unknown")) continue;
            remoteAddress = value;
            break;
        }
        if (!StringUtils.isEmpty(remoteAddress)) {
            if (remoteAddress.equals("[0:0:0:0:0:0:0:1]")) remoteAddress = "127.0.0.1";
            context.put(WebAspectConstants.REMOTE_ADDRESS, remoteAddress);
        }
    }
}