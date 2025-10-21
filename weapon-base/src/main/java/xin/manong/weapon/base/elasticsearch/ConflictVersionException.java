package xin.manong.weapon.base.elasticsearch;

/**
 * 版本冲突异常
 *
 * @author frankcl
 * @date 2025-10-21 17:01:25
 */
public class ConflictVersionException extends Exception {

    public ConflictVersionException(String message) {
        super(message);
    }

    public ConflictVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictVersionException(Throwable cause) {
        super(cause);
    }
}
