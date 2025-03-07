package xin.manong.weapon.base.etcd;

import lombok.Data;

/**
 * ETCD分布式锁封装
 *
 * @author frankcl
 * @date 2025-03-07 17:14:50
 */
@Data
public class EtcdLock {

    /* 锁key */
    private String lockKey;
    /* 获取成功后锁路径 */
    private String lockPath;
    /* 租约ID */
    private long leaseId;
    /* 租约生命周期，单位秒 */
    private long leaseTTL;

    public EtcdLock(String lockKey, long leaseTTL) {
        this.lockKey = lockKey;
        this.leaseTTL = leaseTTL;
    }
}
