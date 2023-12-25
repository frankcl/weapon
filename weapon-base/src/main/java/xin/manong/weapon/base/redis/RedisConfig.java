package xin.manong.weapon.base.redis;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.redisson.client.codec.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.util.ReflectArgs;
import xin.manong.weapon.base.util.ReflectUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * redis基础配置信息
 *
 * @author frankcl
 * @date 2022-12-20 17:21:38
 */
@Data
public class RedisConfig {

    private final static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    private static final String REDIS_PROTOCOL = "redis://";
    private static final String REDIS_SECURITY_PROTOCOL = "rediss://";

    public Integer connectionPoolSize;
    public Integer timeout;
    public String password;
    public String codecClassName;
    public RedisMode mode;
    public Codec codec;

    /**
     * 检测有效性
     *
     * @return 有效返回true，否则返回false;
     */
    public boolean check() {
        if (connectionPoolSize != null && connectionPoolSize <= 0) connectionPoolSize = null;
        if (!StringUtils.isEmpty(codecClassName)) {
            try {
                codec = (Codec) ReflectUtil.newInstance(codecClassName, new ReflectArgs());
            } catch (Exception e) {
                logger.error("redisson codec[{}] is not found", codecClassName);
                logger.error(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    /**
     * 补充redis地址协议
     *
     * @param address 地址
     * @return 补充完整地址
     */
    protected String fillAddress(String address) {
        if (StringUtils.isEmpty(address)) return address;
        if (address.startsWith(REDIS_PROTOCOL) || address.startsWith(REDIS_SECURITY_PROTOCOL)) return address;
        return String.format("%s%s", REDIS_PROTOCOL, address);
    }

    /**
     * 补充redis地址协议
     *
     * @param addresses 地址列表
     * @return 补充完整地址列表
     */
    protected List<String> fillAddress(List<String> addresses) {
        if (addresses == null) return null;
        List<String> addressWithRedis = new ArrayList<>();
        Iterator<String> iterator = addresses.iterator();
        while (iterator.hasNext()) {
            String address = fillAddress(iterator.next());
            if (!StringUtils.isEmpty(address)) addressWithRedis.add(address);
        }
        return addressWithRedis;
    }
}
