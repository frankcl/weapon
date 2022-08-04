package com.manong.weapon.base.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * 随机ID生成器
 *
 * @author frankcl
 * @date 2022-08-04 17:48:11
 */
public class RandomID {

    /**
     * 计算随机ID，考虑机器和时间
     *
     * @return 随机ID
     */
    public static String build() {
        Long currentTime = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String mix = String.format("%s_%s_%d", hostname, uuid, currentTime);
            return DigestUtils.md5Hex(mix);
        } catch (UnknownHostException e) {
            String mix = String.format("unknown_%s_%d", uuid, currentTime);
            return DigestUtils.md5Hex(mix);
        }
    }
}
