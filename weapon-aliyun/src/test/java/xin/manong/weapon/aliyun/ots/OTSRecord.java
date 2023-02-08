package xin.manong.weapon.aliyun.ots;

import lombok.Data;
import xin.manong.weapon.aliyun.ots.annotation.Column;
import xin.manong.weapon.aliyun.ots.annotation.PrimaryKey;

import java.util.List;
import java.util.Map;

/**
 * @author frankcl
 * @date 2023-02-03 17:55:49
 */
@Data
public class OTSRecord {

    public static class User {
        public String name;

        public User(String name) {
            this.name = name;
        }
    }

    public enum MediaType {
        TEXT, VIDEO
    }

    @PrimaryKey(name = "key")
    private String key;
    @Column(name = "c_1")
    protected Long c1;
    @Column
    public Double c2;
    public Integer c3;
    @Column(name = "user")
    public User user;
    @Column(name = "media_type")
    public MediaType mediaType;
    @Column
    public List<User> list;
    @Column
    public Map<String, User> map;
}
