package xin.manong.weapon.alarm;

/**
 * 报警接收人信息
 *
 * @author frankcl
 * @date 2022-12-09 16:38:37
 */
public class AlarmReceiver {

    /* 唯一ID */
    public String uid;
    /* 邮箱地址 */
    public String email;
    /* 电话号码 */
    public String phone;

    public AlarmReceiver() {
    }

    public AlarmReceiver(String uid) {
        this.uid = uid;
    }

    public AlarmReceiver(String uid, String email, String phone) {
        this(uid);
        this.email = email;
        this.phone = phone;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof AlarmReceiver)) return false;
        if (this == object) return true;
        AlarmReceiver other = (AlarmReceiver) object;
        return uid == other.uid || (uid != null && other.uid != null && uid.equals(other.uid));
    }

    @Override
    public int hashCode() {
        return uid == null ? 0 : uid.hashCode();
    }
}
