package xin.manong.weapon.alarm;

/**
 * 报警状态
 *
 * @author frankcl
 * @date 2022-12-09 16:01:11
 */
public enum AlarmStatus {

    INFO, WARN, ERROR, FATAL;

    /**
     * 中文名
     *
     * @return 中文名
     */
    public String chineseName() {
        switch (this) {
            case INFO: return "通知";
            case WARN: return "警告";
            case ERROR: return "错误";
            case FATAL: return "严重";
            default: return "未知";
        }
    }
}
