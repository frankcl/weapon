package com.manong.weapon.base.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 短key构建器
 *
 * @author frankcl
 * @date 2022-09-28 15:01:28
 */
public class ShortKeyBuilder {

    private final static Logger logger = LoggerFactory.getLogger(ShortKeyBuilder.class);

    /* 编码字符集合 */
    private final static char[] ENCODE_CHARS = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z'
    };
    private final static long ONE_BYTE_MASK = 0x3D;
    private final static long TWO_BYTE_MASK = 0x3FFFFFFF;

    /**
     * 针对输入文本构建简短key
     *
     * @param input 输入文本
     * @return 简短key
     */
    public static String build(String input) {
        int codes[] = new int[6];
        String md5 = DigestUtils.md5Hex(input == null ? "" : input);
        for (int i = 0; i < 4; i++) {
            String segment = md5.substring(i * 8, (i + 1) * 8);
            long value = TWO_BYTE_MASK & Long.valueOf(segment, 16);
            for (int j = 0; j < 6; j++) {
                int k = (int) (ONE_BYTE_MASK & value);
                codes[j] += ENCODE_CHARS[k];
                value = value >> 5;
            }
        }
        StringBuffer buffer = new StringBuffer();
        for (int code : codes) buffer.append(ENCODE_CHARS[code % ENCODE_CHARS.length]);
        return buffer.toString();
    }
}
