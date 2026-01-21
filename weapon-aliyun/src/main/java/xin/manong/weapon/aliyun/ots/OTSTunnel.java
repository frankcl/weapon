package xin.manong.weapon.aliyun.ots;

import com.alicloud.openservices.tablestore.TunnelClient;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.alarm.AlarmProducer;
import xin.manong.weapon.base.event.EventListener;
import xin.manong.weapon.base.event.RebuildEvent;
import xin.manong.weapon.base.rebuild.RebuildManager;
import xin.manong.weapon.base.rebuild.Rebuildable;
import xin.manong.weapon.aliyun.secret.DynamicSecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * OTS数据通道
 *
 * @author frankcl
 * @date 2022-08-03 19:11:02
 */
public class OTSTunnel implements Rebuildable {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnel.class);

    @Setter
    protected String appName;
    protected OTSTunnelConfig config;
    protected OTSTunnelMonitor monitor;
    protected TunnelClient tunnelClient;
    protected Map<String, OTSTunnelWorker> workerMap;
    protected List<EventListener> eventListeners;
    @Setter
    protected AlarmProducer alarmProducer;

    public OTSTunnel(OTSTunnelConfig config) {
        this.config = config;
        this.workerMap = new ConcurrentHashMap<>();
        this.eventListeners = new ArrayList<>();
    }

    /**
     * 构建OTS通道
     *
     * @return 构建成功返回true，否则返回false
     */
    private boolean build() {
        tunnelClient = new TunnelClient(config.endpoint, config.aliyunSecret.accessKey,
                config.aliyunSecret.secretKey, config.instance);
        workerMap.clear();
        for (OTSTunnelWorkerConfig workerConfig : config.workerConfigs) {
            OTSTunnelWorker worker = new OTSTunnelWorker(workerConfig, tunnelClient);
            if (!worker.start()) return false;
            String key = DigestUtils.md5Hex(String.format("%s_%s", workerConfig.table, workerConfig.tunnel));
            workerMap.put(key, worker);
        }
        monitor = new OTSTunnelMonitor(config, tunnelClient);
        monitor.setAppName(appName);
        monitor.setAlarmProducer(alarmProducer);
        monitor.start();
        return true;
    }

    @Override
    public void rebuild() {
        logger.info("OTS tunnel is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("Secret is not changed, ignore OTS tunnel rebuilding");
            return;
        }
        config.aliyunSecret.accessKey = DynamicSecret.accessKey;
        config.aliyunSecret.secretKey = DynamicSecret.secretKey;
        OTSTunnelMonitor prevMonitor = monitor;
        TunnelClient prevClient = tunnelClient;
        if (prevMonitor != null) prevMonitor.stop();
        for (OTSTunnelWorker worker : workerMap.values()) worker.stop();
        workerMap.clear();
        if (prevClient != null) prevClient.shutdown();
        for (EventListener eventListener : eventListeners) {
            eventListener.onRebuild(new RebuildEvent<>(this));
        }
        if (!build()) throw new RuntimeException("Rebuild OTS tunnel failed");
        logger.info("OTS tunnel rebuild success");
    }

    /**
     * 启动OTS通道
     *
     * @return 启动成功返回true，否则返回false
     */
    public boolean start() {
        logger.info("OTS tunnel is starting ...");
        if (config == null) {
            logger.error("OTS tunnel config is null");
            return false;
        }
        if (!config.check()) return false;
        if (!build()) return false;
        if (config.dynamic) RebuildManager.register(this);
        logger.info("OTS tunnel has been started");
        return true;
    }

    /**
     * 停止OTS通道
     */
    public void stop() {
        logger.info("OTS tunnel is stopping ...");
        if (config.dynamic) RebuildManager.unregister(this);
        if (monitor != null) monitor.stop();
        for (OTSTunnelWorker worker : workerMap.values()) worker.stop();
        workerMap.clear();
        if (tunnelClient != null) tunnelClient.shutdown();
        logger.info("OTS tunnel has been stopped");
    }

    /**
     * 添加重建监听器
     *
     * @param eventListener 重建监听器
     */
    public void addRebuildListener(EventListener eventListener) {
        if (eventListener == null) return;
        eventListeners.add(eventListener);
    }

    /**
     * 启动通道worker
     *
     * @param workerConfig 通道worker配置
     * @return 启动成功返回true，否则返回false
     */
    public boolean startTunnelWorker(OTSTunnelWorkerConfig workerConfig) {
        if (workerConfig == null || !workerConfig.check()) {
            logger.error("Invalid OTS tunnel worker config");
            return false;
        }
        String key = DigestUtils.md5Hex(String.format("%s_%s", workerConfig.table, workerConfig.tunnel));
        if (workerMap.containsKey(key)) {
            logger.warn("Tunnel worker:{}/{} has existed", workerConfig.table, workerConfig.tunnel);
            return false;
        }
        if (!config.addTunnelWorkerConfig(workerConfig)) return false;
        OTSTunnelWorker worker = new OTSTunnelWorker(workerConfig, tunnelClient);
        if (!worker.start()) {
            config.removeTunnelWorkerConfig(workerConfig);
            return false;
        }
        workerMap.put(key, worker);
        return true;
    }

    /**
     * 停止通道worker
     *
     * @param workerConfig 通道worker配置
     */
    public void stopTunnelWorker(OTSTunnelWorkerConfig workerConfig) {
        if (workerConfig == null) return;
        if (StringUtils.isEmpty(workerConfig.table)) {
            logger.warn("Table is null, ignore remove request");
            return;
        }
        if (StringUtils.isEmpty(workerConfig.tunnel)) {
            logger.warn("Tunnel is null, ignore remove request");
            return;
        }
        String key = DigestUtils.md5Hex(String.format("%s_%s", workerConfig.table, workerConfig.tunnel));
        OTSTunnelWorker worker = workerMap.remove(key);
        if (worker == null) {
            logger.warn("Tunnel worker:{}/{} is not found", workerConfig.table, workerConfig.tunnel);
            return;
        }
        worker.stop();
        config.removeTunnelWorkerConfig(workerConfig);
    }
}
