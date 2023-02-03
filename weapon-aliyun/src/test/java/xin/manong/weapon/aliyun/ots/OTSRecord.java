package xin.manong.weapon.aliyun.ots;

import lombok.Data;
import xin.manong.weapon.aliyun.ots.annotation.Column;
import xin.manong.weapon.aliyun.ots.annotation.PrimaryKey;

/**
 * @author frankcl
 * @date 2023-02-03 17:55:49
 */
@Data
public class OTSRecord {

    @PrimaryKey(name = "key")
    private String key;
    @Column(name = "c_1")
    protected Long c1;
    @Column
    public Double c2;
    public Integer c3;
}
