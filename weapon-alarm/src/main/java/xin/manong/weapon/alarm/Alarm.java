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
}
