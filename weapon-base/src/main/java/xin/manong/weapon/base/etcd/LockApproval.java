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
public class LockApproval {

    /**
     * 锁key
     */
    @Getter
    private final String key;
    /**
     * 批准后锁路径
     */
    @Getter
    @Setter
    private String path;
    /**
     * 租约ID
     */
    @Getter
    @Setter
    private Long leaseId;
    /**
     * 租约保持观察者移除
     */
    @Getter
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
