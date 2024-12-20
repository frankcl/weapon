package xin.manong.weapon.spring.boot.bean.wrap;

import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import xin.manong.weapon.aliyun.ots.OTSTunnel;
import xin.manong.weapon.aliyun.ots.OTSTunnelConfig;
import xin.manong.weapon.aliyun.ots.OTSTunnelWorkerConfig;

import java.util.List;

/**
 * OTS Tunnel封装
 * 在属性设置完成之后保证channelProcessor注入
 *
 * @author frankcl
 * @date 2023-02-20 21:47:31
 */
public class OTSTunnelBean extends OTSTunnel implements InitializingBean, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelBean.class);

    private ApplicationContext applicationContext;

    public OTSTunnelBean(OTSTunnelConfig config) {
        super(config);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<OTSTunnelWorkerConfig> workerConfigs = config.workerConfigs;
        for (OTSTunnelWorkerConfig workerConfig : workerConfigs) {
            if (StringUtils.isEmpty(workerConfig.processor)) {
                logger.warn("processor config is not found for tunnel[{}]", workerConfig.tunnel);
                continue;
            }
            Object bean = applicationContext.getBean(workerConfig.processor);
            if (!(bean instanceof IChannelProcessor)) {
                logger.error("unexpected bean[{}], not IChannelProcessor", bean.getClass().getName());
                throw new Exception(String.format("unexpected bean[%s]", bean.getClass().getName()));
            }
            workerConfig.channelProcessor = (IChannelProcessor) bean;
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
