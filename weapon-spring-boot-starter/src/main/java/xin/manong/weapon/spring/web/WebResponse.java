package xin.manong.weapon.spring.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.Response;

/**
 * web响应对象
 *
 * @author frankcl
 * @date 2023-03-06 16:28:10
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebResponse<T> {

    @JsonProperty("status")
    public boolean status;
    @JsonProperty("code")
    public Integer code;
    @JsonProperty("message")
    public String message;
    @JsonProperty("data")
    public T data;

    public static class Builder {
        private WebResponse template = new WebResponse();

        public Builder status(boolean status) {
            template.status = status;
            return this;
        }

        public Builder code(int code) {
            template.code = code;
            return this;
        }

        public Builder message(String message) {
            template.message = StringUtils.isEmpty(message) ? "" : message;
            return this;
        }

        public Builder data(Object data) {
            template.data = data;
            return this;
        }

        public WebResponse build() {
            WebResponse response = new WebResponse();
            response.status = template.status;
            response.code = template.code;
            response.message = template.message;
            response.data = template.data;
            return response;
        }
    }

    /**
     * 构建成功响应对象
     *
     * @param code 成功HTTP编码
     * @param data 数据
     * @return 成功响应对象
     * @param <T>
     */
    public static <T> WebResponse<T> buildOK(int code, T data) {
        return new Builder().status(true).code(code).data(data).build();
    }

    /**
     * 构建成功响应对象
     *
     * @param data 数据
     * @return 成功响应对象
     * @param <T>
     */
    public static <T> WebResponse<T> buildOK(T data) {
        return buildOK(Response.Status.OK.getStatusCode(), data);
    }

    /**
     * 构建错误响应对象
     *
     * @param code HTTP错误码
     * @param message 错误信息
     * @return 错误响应对象
     * @param <T>
     */
    public static <T> WebResponse<T> buildError(int code, String message) {
        return new Builder().status(false).code(code).message(message).build();
    }
}
