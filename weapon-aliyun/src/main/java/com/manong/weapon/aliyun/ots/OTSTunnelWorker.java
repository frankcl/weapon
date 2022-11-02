package com.manong.weapon.aliyun.ots;

import com.alicloud.openservices.tablestore.TunnelClient;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelRequest;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelResponse;
import com.alicloud.openservices.tablestore.model.tunnel.TunnelInfo;
import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import com.alicloud.openservices.tablestore.tunnel.worker.TunnelWorker;
import com.alicloud.openservices.tablestore.tunnel.worker.TunnelWorkerConfig;
import com.manong.weapon.aliyun.common.RebuildListener;
import com.manong.weapon.aliyun.common.RebuildManager;
import com.manong.weapon.aliyun.common.Rebuildable;
import com.manong.weapon.aliyun.secret.DynamicSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OTS数据通道
 *
 * @author frankcl
 * @date 2022-08-03 19:11:02
 */
public class OTSTunnelWorker implements Rebuildable {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelWorker.class);

    private OTSTunnelConfig config;
    private OTSTunnelMonitor monitor;
    private TunnelClient tunnelClient;
    private TunnelWorker worker;
    private IChannelProcessor channelProcessor;
    private List<RebuildListener> rebuildListeners;

    public OTSTunnelWorker(OTSTunnelConfig config,
                           IChannelProcessor channelProcessor) {
        this.config = config;
        this.channelProcessor = channelProcessor;
        this.rebuildListeners = new ArrayList<>();
    }

    /**
     * 构建OTS通道
     *
     * @return 构建成功返回true，否则返回false
     */
    private boolean build() {
        tunnelClient = new TunnelClient(config.endpoint, config.aliyunSecret.accessKey,
                config.aliyunSecret.secretKey, config.instance);
        DescribeTunnelRequest request = new DescribeTunnelRequest(config.table, config.tunnel);
        try {
            DescribeTunnelResponse response = tunnelClient.describeTunnel(request);
            TunnelInfo tunnelInfo = response.getTunnelInfo();
            TunnelWorkerConfig workerConfig = new TunnelWorkerConfig(channelProcessor);
            workerConfig.setMaxRetryIntervalInMillis(config.maxRetryIntervalMs);
            workerConfig.setHeartbeatIntervalInSec(config.heartBeatIntervalSec);
            if (config.maxChannelParallel > 0) workerConfig.setMaxChannelParallel(config.maxChannelParallel);
            int threadNum = config.consumeThreadNum;
            workerConfig.setReadRecordsExecutor(createThreadPoolExecutor("tunnel_reader", threadNum));
            workerConfig.setProcessRecordsExecutor(createThreadPoolExecutor("tunnel_processor", threadNum));
            worker = new TunnelWorker(tunnelInfo.getTunnelId(), tunnelClient, workerConfig);
            worker.connectAndWorking();
            monitor = new OTSTunnelMonitor(config, tunnelClient);
            monitor.start();
            logger.info("build OTSTunnel worker success");
            return true;
        } catch (Exception e) {
            logger.error("build OTSTunnel worker failed");
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void rebuild() {
        logger.info("OTSTunnel worker is rebuilding ...");
        if (DynamicSecret.accessKey.equals(config.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(config.aliyunSecret.secretKey)) {
            logger.warn("secret is not changed, ignore OTSTunnel worker rebuilding");
            return;
        }
        config.aliyunSecret.accessKey = DynamicSecret.accessKey;
        config.aliyunSecret.secretKey = DynamicSecret.secretKey;
        OTSTunnelMonitor prevMonitor = monitor;
        TunnelWorker prevWorker = worker;
        TunnelClient prevClient = tunnelClient;
        if (prevMonitor != null) prevMonitor.stop();
        if (prevWorker != null) prevWorker.shutdown();
        if (prevClient != null) prevClient.shutdown();
        for (RebuildListener rebuildListener : rebuildListeners) {
            rebuildListener.notifyRebuildEvent(this);
        }
        if (!build()) throw new RuntimeException("rebuild OTSTunnel worker failed");
        logger.info("OTSTunnel worker rebuild success");
    }

    /**
     * 启动OTS通道
     *
     * @return 启动成功返回true，否则返回false
     */
    public boolean start() {
        logger.info("OTSTunnel worker is starting ...");
        if (config == null) {
            logger.error("OTSTunnel worker config is null");
            return false;
        }
        if (!config.check()) return false;
        if (channelProcessor == null) {
            logger.error("channel processor is null");
            return false;
        }
        if (!build()) return false;
        RebuildManager.register(this);
        logger.info("OTSTunnel worker has been started");
        return true;
    }

    /**
     * 停止OTS通道
     */
    public void stop() {
        logger.info("OTSTunnel worker is stopping ...");
        RebuildManager.unregister(this);
        if (monitor != null) monitor.stop();
        if (worker != null) worker.shutdown();
        if (tunnelClient != null) tunnelClient.shutdown();
        logger.info("OTSTunnel worker has been stopped");
    }

    /**
     * 添加重建监听器
     *
     * @param listener 重建监听器
     */
    public void addRebuildListener(RebuildListener listener) {
        if (listener == null) return;
        rebuildListeners.add(listener);
    }

    /**
     * 创建线程池执行器
     *
     * @param name 线程池名称
     * @param threadNum 线程数
     * @return 线程池执行器实例
     */
    private ThreadPoolExecutor createThreadPoolExecutor(String name, int threadNum) {
        logger.info("create thread pool executor[{}:{}]", name, threadNum);
        return new ThreadPoolExecutor(threadNum, threadNum, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue(16), new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger();
            public Thread newThread(Runnable task) {
                String threadName = String.format("%s-%d", name, this.counter.getAndIncrement());
                logger.info("create channel receiver thread[{}] success", threadName);
                return new Thread(task, threadName);
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
