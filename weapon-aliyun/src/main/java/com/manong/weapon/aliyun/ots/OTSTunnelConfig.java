package com.manong.weapon.aliyun.ots;

import com.manong.weapon.aliyun.secret.AliyunSecret;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * OTS通道配置
 *
 * @author frankcl
 * @date 2022-08-04 23:08:14
 */
public class OTSTunnelConfig {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelConfig.class);

    public String endpoint;
    public String instance;
    public AliyunSecret aliyunSecret;
    public List<OTSTunnelWorkerConfig> workerConfigs = new ArrayList<>();

    /**
     * 添加通道worker配置
     *
     * @param workerConfig 通道worker配置
     * @return  添加成功返回true，否则返回false
     */
    public boolean addTunnelWorkerConfig(OTSTunnelWorkerConfig workerConfig) {
        if (workerConfig == null || !workerConfig.check()) return false;
        if (workerConfigs == null) workerConfigs = new ArrayList<>();
        for (OTSTunnelWorkerConfig config : workerConfigs) {
            if (config.equals(workerConfig)) {
                logger.warn("worker config[{}/{}] has existed", workerConfig.table, workerConfig.tunnel);
                return false;
            }
        }
        workerConfigs.add(workerConfig);
        return true;
    }

    /**
     * 移除通道worker配置
     *
     * @param workerConfig 通道worker配置
     */
    public void removeTunnelWorkerConfig(OTSTunnelWorkerConfig workerConfig) {
        if (workerConfig == null) return;
        if (StringUtils.isEmpty(workerConfig.table)) {
            logger.warn("table is null, ignore remove request");
            return;
        }
        if (StringUtils.isEmpty(workerConfig.tunnel)) {
            logger.warn("tunnel is null, ignore remove request");
            return;
        }
        if (!workerConfigs.remove(workerConfig)) {
            logger.warn("tunnel worker[{}/{}] is not found", workerConfig.table, workerConfig.tunnel);
        }
    }

    /**
     * 检测OTS通道配置有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (aliyunSecret == null || !aliyunSecret.check()) return false;
        if (StringUtils.isEmpty(instance)) {
            logger.error("oss instance is empty");
            return false;
        }
        if (StringUtils.isEmpty(endpoint)) {
            logger.error("oss endpoint is empty");
            return false;
        }
        for (OTSTunnelWorkerConfig workerConfig : workerConfigs) {
            if (!workerConfig.check()) return false;
        }
        return true;
    }
}
