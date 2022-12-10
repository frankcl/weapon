package xin.manong.weapon.alarm;

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
    /* 报警状态 */
    public AlarmStatus status;

    public Alarm(AlarmStatus status) {
        this.time = System.currentTimeMillis();
        this.status = status == null ? AlarmStatus.INFO : status;
    }

    public Alarm(String title, String content, AlarmStatus status) {
        this(status);
        this.title = title;
        this.content = content;
    }

    public Alarm(String content, AlarmStatus status) {
        this(status);
        this.content = content;
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
     * 设置报警状态
     *
     * @param status 报警状态
     * @return 报警信息
     */
    public Alarm setStatus(AlarmStatus status) {
        this.status = status;
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
