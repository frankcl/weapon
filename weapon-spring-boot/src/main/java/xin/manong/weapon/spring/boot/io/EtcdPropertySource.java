package xin.manong.weapon.spring.boot.io;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.PropertySource;
import xin.manong.weapon.base.etcd.EtcdClient;

/**
 * 从etcd服务获取配置信息
 *
 * @author frankcl
 * @date 2024-11-15 19:52:35
 */
public class EtcdPropertySource extends PropertySource<EtcdClient> {

    public EtcdPropertySource(String name, EtcdClient etcdClient) {
        super(name, etcdClient);
    }

    @Override
    public Object getProperty(@NotNull String name) {
        return source.get(name);
    }
}
