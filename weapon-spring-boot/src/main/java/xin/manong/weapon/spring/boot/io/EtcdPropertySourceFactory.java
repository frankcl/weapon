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

import java.io.IOException;
import java.util.HashMap;
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
    private static final String[] ETCD_CONFIG = {
            "weapon.common.etcd.client",
            "weapon.common.etcd.client.many.default" };

    private static EtcdClient etcdClient;

    @Override
    @NotNull
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        String sourceFile = resource.getResource().getFilename();
        if (StringUtils.isEmpty(sourceFile)) sourceFile = YAML_FILE;
        buildEtcdClient(sourceFile);
        return new EtcdPropertySource(name, etcdClient);
    }

    /**
     * 构建etcd客户端
     *
     * @param sourceFile spring配置文件名
     * @throws IOException I/O异常
     */
    private static void buildEtcdClient(String sourceFile) throws IOException {
        if (etcdClient != null) return;
        Map<String, Object> configMap = new HashMap<>();
        Resource resource = new ClassPathResource(sourceFile);
        if (!resource.exists()) {
            logger.warn("source file is not found for EtcdPropertySourceFactory: {}", sourceFile);
            return;
        }
        Yaml yaml = new Yaml();
        Map<String, Object> yamlConfigMap = yaml.load(resource.getInputStream());
        Map<String, Object> config = getEtcdConfig(yamlConfigMap);
        if (config != null) configMap.putAll(config);
        EtcdConfig etcdConfig = JSON.parseObject(JSON.toJSONString(configMap), EtcdConfig.class);
        etcdClient = new EtcdClient(etcdConfig);
    }

    /**
     * 获取etcd配置
     *
     * @param yamlConfigMap yaml文件配置
     * @return 成功返回etcd配置，否则返回null
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> getEtcdConfig(Map<String, Object> yamlConfigMap) {
        JSONObject json = JSON.parseObject(JSON.toJSONString(yamlConfigMap));
        for (String key : ETCD_CONFIG) {
            Map<String, Object> config = (Map<String, Object>) JSONUtil.get(json, key);
            if (config != null && !config.isEmpty()) return config;
        }
        return null;
    }
}
