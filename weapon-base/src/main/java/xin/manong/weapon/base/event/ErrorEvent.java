package xin.manong.weapon.base.event;

import lombok.Getter;

/**
 * 错误事件
 *
 * @author frankcl
 * @date 2025-04-19 11:38:13
 */
@Getter
public class ErrorEvent implements Event {

    private final String message;
    private final Throwable throwable;

    public ErrorEvent(String message) {
        this(message, null);
    }

    public ErrorEvent(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }
}
