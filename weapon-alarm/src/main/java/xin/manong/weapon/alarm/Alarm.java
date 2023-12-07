package xin.manong.weapon.alarm;

import org.apache.commons.lang3.StringUtils;

/**
 * 报警信息
 *
 * @author frankcl
 * @date 2022-12-09 15:47:24
 */
public class Alarm {

    /* 报警毫秒时间戳 */
    public Long time;
    /* 报警标题 */
    public String title;
    /* 报警内容 */
    public String content;
    /* 报警所属应用 */
    public String appName;
    /* 报警级别 */
    public AlarmLevel level;

    public Alarm(AlarmLevel level) {
        this(null, null, level);
    }

    public Alarm(String content, AlarmLevel level) {
        this(null, content, level);
    }

    public Alarm(String title, String content, AlarmLevel level) {
        this.title = StringUtils.isEmpty(title) ? "无标题" : title;
        this.content = StringUtils.isEmpty(content) ? "" : content;
        this.time = System.currentTimeMillis();
        this.level = level == null ? AlarmLevel.INFO : level;
        this.appName = "未知应用";
    }

    /**
     * 设置应用名
     *
     * @param appName 应用名
     * @return 报警信息
     */
    public Alarm setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    /**
     * 设置报警级别
     *
     * @param level 报警级别
     * @return 报警信息
     */
    public Alarm setLevel(AlarmLevel level) {
        this.level = level;
        return this;
    }

    /**
     * 设置报警标题
     *
     * @param title 报警标题
     * @return 报警信息
     */
    public Alarm setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置报警内容
     *
     * @param content 报警内容
     * @return 报警信息
     */
    public Alarm setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 设置报警时间
     *
     * @param time 报警时间
     * @return 报警信息
     */
    public Alarm setTime(Long time) {
        this.time = time;
        return this;
    }
}
