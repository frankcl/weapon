package com.manong.weapon.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 读写文件工具类
 *
 * @author frankcl
 * @create 2019-05-28 10:34
 */
public class FileUtil {

    private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private final static int BUFFER_SIZE = 4096;

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 成功返回true，否则返回false
     */
    public static boolean delete(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    /**
     * 读取文件字符内容，并按照charset进行编码
     *
     * @param filePath 文件路径
     * @param charset 字符编码
     * @return 读取成功返回文件字符内容，否则返回null
     */
    public static String read(String filePath, Charset charset) {
        byte[] byteContent = read(filePath);
        if (byteContent == null) return null;
        return byteContent.length == 0 ? "" : new String(byteContent, charset);
    }

    /**
     * 读取文件字节内容
     *
     * @param filePath 文件路径
     * @return 读取成功返回文件内容字节数组，失败返回null
     */
    public static byte[] read(String filePath) {
        byte[] readBuf = new byte[BUFFER_SIZE];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            int size;
            FileInputStream inputStream = new FileInputStream(filePath);
            while ((size = inputStream.read(readBuf, 0, BUFFER_SIZE)) != -1) {
                output.write(readBuf, 0, size);
            }
            return output.toByteArray();
        } catch (Exception e) {
            logger.error("read failed for file[{}]", filePath);
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    logger.error("close output stream failed");
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
