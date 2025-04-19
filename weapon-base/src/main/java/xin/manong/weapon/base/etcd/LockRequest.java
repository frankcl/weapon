package xin.manong.weapon.base.etcd;

import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.grpc.stub.StreamObserver;
import lombok.Getter;

/**
 * 锁申请请求
 *
 * @author frankcl
 * @date 2025-04-19 19:52:58
 */
@Getter
public class LockRequest {

    /**
     * 锁key
     */
    private final String key;
    /**
     * 租约TTL
     */
    private final long leaseTTL;
    /**
     * 租约保持观察者
     */
    private final StreamObserver<LeaseKeepAliveResponse> observer;

    public LockRequest(String key, long leaseTTL) {
        this(key, leaseTTL, null);
    }

    public LockRequest(String key, long leaseTTL,
                       StreamObserver<LeaseKeepAliveResponse> observer) {
        this.key = key;
        this.leaseTTL = leaseTTL;
        this.observer = observer;
    }
}
