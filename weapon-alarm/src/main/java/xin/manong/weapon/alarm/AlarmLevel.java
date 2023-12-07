package xin.manong.weapon.alarm;

/**
 * 报警级别
 *
 * @author frankcl
 * @date 2022-12-09 16:01:11
 */
public enum AlarmLevel {

    INFO("通知"),
    WARN("警告"),
    ERROR("错误"),
    FATAL("严重");

    final String level;

    AlarmLevel(String level) {
        this.level = level;
    }
}
