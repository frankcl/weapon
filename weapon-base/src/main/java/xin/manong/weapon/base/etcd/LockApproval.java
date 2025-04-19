package xin.manong.weapon.base.etcd;

import io.etcd.jetcd.support.CloseableClient;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取锁批准
 *
 * @author frankcl
 * @date 2025-04-19 20:00:18
 */
@Getter
public class LockApproval {

    /**
     * 锁key
     */
    private final String key;
    /**
     * 批准后锁路径
     */
    @Setter
    private String path;
    /**
     * 租约ID
     */
    @Setter
    private Long leaseId;
    /**
     * 租约保持观察者移除
     */
    @Setter
    private CloseableClient observerRemove;

    public LockApproval(LockRequest request) {
        this.key = request.getKey();
    }

    /**
     * 移除观察者
     */
    public void removeObserver() {
        if (observerRemove != null) {
            observerRemove.close();
            observerRemove = null;
        }
    }
}
