package xin.manong.weapon.base.redisson;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * redisson基础配置信息
 *
 * @author frankcl
 * @date 2022-12-20 17:21:38
 */
public class RedissonConfig {

    private static final String REDIS_PROTOCOL = "redis://";
    private static final String REDIS_SECURITY_PROTOCOL = "rediss://";

    public Integer connectionPoolSize;
    public String password;

    /**
     * 检测有效性
     *
     * @return 有效返回true，否则返回false;
     */
    public boolean check() {
        if (connectionPoolSize != null && connectionPoolSize <= 0) connectionPoolSize = null;
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
