package xin.manong.weapon.spring.boot.bean;

import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;
import xin.manong.weapon.aliyun.ots.OTSTunnel;
import xin.manong.weapon.aliyun.ots.OTSTunnelConfig;
import xin.manong.weapon.aliyun.ots.OTSTunnelWorkerConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;

import java.util.Map;

/**
 * 阿里云OTS数据通道bean定义注册
 *
 * @author frankcl
 * @date 2022-08-26 11:25:16
 */
@Component
public class OTSTunnelDefinitionRegistryPostProcessor extends AliyunBeanDefinitionRegistryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelDefinitionRegistryPostProcessor.class);

    private final static String BINDING_KEY = "weapon.aliyun.ots.tunnel";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        AliyunSecret secret = bindAliyunSecret();
        Map<String, OTSTunnelConfig> configMap;
        try {
            configMap = Binder.get(environment).bind(BINDING_KEY, Bindable.mapOf(
                    String.class, OTSTunnelConfig.class)).get();
        } catch (Exception e) {
            logger.warn("bind OTS tunnel config failed");
            logger.warn(e.getMessage(), e);
            return;
        }
        for (Map.Entry<String, OTSTunnelConfig> entry : configMap.entrySet()) {
            String name = String.format("%sOTSTunnel", entry.getKey());
            OTSTunnelConfig config = entry.getValue();
            boolean check = secret != null && secret.check();
            if (check) config.aliyunSecret = secret;
            if (!config.dynamic && !check) logger.error("aliyun secret is not config");
            fillChannelProcessors(config);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(OTSTunnel.class, () ->
                    new OTSTunnel(config));
            beanDefinition.setInitMethodName("start");
            beanDefinition.setEnforceInitMethod(true);
            beanDefinition.setLazyInit(false);
            beanDefinition.setDestroyMethodName("stop");
            beanDefinition.setEnforceDestroyMethod(true);
            beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            logger.info("register OTS tunnel bean definition success for name[{}]", name);
        }
    }

    /**
     * 从spring上下文填充数据处理器
     *
     * @param config OTS数据通道配置
     * @return 失败抛出异常
     */
    private void fillChannelProcessors(OTSTunnelConfig config) {
        if (config.workerConfigs == null || config.workerConfigs.isEmpty()) {
            logger.error("OTS tunnel worker config is empty");
            throw new RuntimeException("OTS tunnel worker config is empty");
        }
        for (OTSTunnelWorkerConfig workerConfig : config.workerConfigs) {
            if (StringUtils.isEmpty(workerConfig.processor)) {
                logger.error("processor is not config for OTS tunnel[{}/{}]", workerConfig.table, workerConfig.tunnel);
                throw new RuntimeException(String.format("processor is not config for OTS tunnel[%s/%s]",
                        workerConfig.table, workerConfig.tunnel));
            }
            IChannelProcessor processor = (IChannelProcessor) applicationContext.getBean(workerConfig.processor);
            if (processor == null) {
                logger.error("channel processor is not found for name[{}]", workerConfig.processor);
                throw new RuntimeException(String.format("channel processor is not found for name[%s]",
                        workerConfig.processor));
            }
            workerConfig.channelProcessor = processor;
        }
    }
}
