package xin.manong.weapon.spring.boot.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.yaml.snakeyaml.Yaml;
import xin.manong.weapon.base.etcd.EtcdClient;
import xin.manong.weapon.base.etcd.EtcdConfig;
import xin.manong.weapon.base.util.JSONUtil;
import xin.manong.weapon.spring.boot.configuration.EtcdClientMapConfig;

import java.io.IOException;
import java.util.Map;

/**
 * etcd property source工厂
 * 生产EtcdPropertySource
 *
 * @author frankcl
 * @date 2024-11-15 19:52:00
 */
public class EtcdPropertySourceFactory implements PropertySourceFactory {

    private static final Logger logger = LoggerFactory.getLogger(EtcdPropertySourceFactory.class);

    private static final String YAML_FILE = "application.yml";
    private static final String ETCD_CONFIG = "weapon.common.etcd.client";

    @Override
    @NotNull
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        String sourceFile = resource.getResource().getFilename();
        if (StringUtils.isEmpty(sourceFile)) sourceFile = YAML_FILE;
        EtcdClient etcdClient = buildEtcdClient(name, sourceFile);
        return new EtcdPropertySource(name, etcdClient);
    }

    /**
     * 构建etcd客户端
     *
     * @param name etcd客户端配置名称
     * @param sourceFile spring配置文件名
     * @return etcd客户端
     * @throws IOException I/O异常
     */
    @SuppressWarnings("unchecked")
    private EtcdClient buildEtcdClient(String name, String sourceFile) throws IOException {
        Resource resource = new ClassPathResource(sourceFile);
        if (!resource.exists()) {
            logger.warn("source file is not found for EtcdPropertySourceFactory: {}", sourceFile);
            throw new IOException(String.format("source file is not found for EtcdPropertySourceFactory: %s", sourceFile));
        }
        Yaml yaml = new Yaml();
        Map<String, Object> yamlConfigMap = yaml.load(resource.getInputStream());
        JSONObject json = JSON.parseObject(JSON.toJSONString(yamlConfigMap));
        Map<String, Object> configMap = (Map<String, Object>) JSONUtil.get(json, ETCD_CONFIG);
        if (configMap == null) {
            logger.warn("etcd client config is not found for key[{}]", ETCD_CONFIG);
            throw new IOException(String.format("etcd client config is not found for key[%s]", ETCD_CONFIG));
        }
        EtcdClientMapConfig clientMapConfig = JSON.parseObject(JSON.toJSONString(configMap), EtcdClientMapConfig.class);
        EtcdConfig etcdConfig = getEtcdConfig(clientMapConfig, name);
        return new EtcdClient(etcdConfig);
    }

    /**
     * 获取etcd客户端配置
     * 1. 如果存在多客户端配置，根据客户端名称获取
     * 2. 如果不存在多客户端配置，获取默认配置
     *
     * @param clientMapConfig 多客户端配置
     * @param name 配置名称
     * @return 客户端配置
     */
    private EtcdConfig getEtcdConfig(EtcdClientMapConfig clientMapConfig, String name) {
        if (StringUtils.isEmpty(name)) name = "default";
        if (clientMapConfig.many == null || clientMapConfig.many.isEmpty()) return clientMapConfig;
        return clientMapConfig.many.get(name);
    }
}
