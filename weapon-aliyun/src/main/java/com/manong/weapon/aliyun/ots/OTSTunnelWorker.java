package com.manong.weapon.aliyun.ots;

import com.alicloud.openservices.tablestore.TunnelClient;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelRequest;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelResponse;
import com.alicloud.openservices.tablestore.model.tunnel.TunnelInfo;
import com.alicloud.openservices.tablestore.tunnel.worker.TunnelWorker;
import com.alicloud.openservices.tablestore.tunnel.worker.TunnelWorkerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class OTSTunnelWorker {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelWorker.class);

    private OTSTunnelWorkerConfig config;
    private TunnelWorkerConfig workerConfig;
    private TunnelClient tunnelClient;
    private TunnelWorker worker;

    public OTSTunnelWorker(OTSTunnelWorkerConfig config,
                           TunnelClient tunnelClient) {
        this.config = config;
        this.tunnelClient = tunnelClient;
        if (!check()) throw new RuntimeException("invalid OTS tunnel worker config");
    }

    /**
     * 检测配置信息
     *
     * @return 合法返回true，否则返回false
     */
    private boolean check() {
        if (config == null) {
            logger.error("OTS tunnel worker config is null");
            return false;
        }
        return config.check();
    }

    /**
     * 启动OTS通道worker
     *
     * @return 启动成功返回true，否则返回false
     */
    public boolean start() {
        logger.info("OTS tunnel worker[{}/{}] is starting ...", config.table, config.tunnel);
        DescribeTunnelRequest request = new DescribeTunnelRequest(config.table, config.tunnel);
        try {
            DescribeTunnelResponse response = tunnelClient.describeTunnel(request);
            TunnelInfo tunnelInfo = response.getTunnelInfo();
            workerConfig = new TunnelWorkerConfig(config.channelProcessor);
            workerConfig.setMaxRetryIntervalInMillis(config.maxRetryIntervalMs);
            workerConfig.setHeartbeatIntervalInSec(config.heartBeatIntervalSec);
            if (config.maxChannelParallel > 0) workerConfig.setMaxChannelParallel(config.maxChannelParallel);
            int threadNum = config.consumeThreadNum;
            workerConfig.setReadRecordsExecutor(createThreadPoolExecutor("tunnel_reader", threadNum));
            workerConfig.setProcessRecordsExecutor(createThreadPoolExecutor("tunnel_processor", threadNum));
            worker = new TunnelWorker(tunnelInfo.getTunnelId(), tunnelClient, workerConfig);
            worker.connectAndWorking();
        } catch (Exception e) {
            logger.error("start OTS tunnel worker[{}/{}] failed", config.table, config.tunnel);
            logger.error(e.getMessage(), e);
            return false;
        }
        logger.info("OTS tunnel worker[{}/{}] has been started", config.table, config.tunnel);
        return true;
    }

    /**
     * 停止OTS通道worker
     */
    public void stop() {
        logger.info("OTS tunnel worker[{}/{}] is stopping ...", config.table, config.tunnel);
        if (worker != null) worker.shutdown();
        if (workerConfig != null) workerConfig.shutdown();
        logger.info("OTS tunnel worker[{}/{}] has been stopped", config.table, config.tunnel);
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
