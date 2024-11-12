package xin.manong.weapon.base.etcd;

import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchResponse;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * 封装回调consumer和监听器
 *
 * @author frankcl
 * @date 2024-11-12 13:12:27
 */
@Getter
public class EtcdWatcher {

    private final Consumer<WatchResponse> onNext;
    private final Watch.Watcher watcher;

    public EtcdWatcher(Consumer<WatchResponse> onNext, Watch.Watcher watcher) {
        this.onNext = onNext;
        this.watcher = watcher;
    }
}
