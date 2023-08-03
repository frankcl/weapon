package xin.manong.weapon.base.image;

import xin.manong.weapon.base.util.ImageUtil;

import java.awt.image.BufferedImage;

/**
 * 计算图片Hash
 *
 * @author frankcl
 * @date 2023-08-02 15:13:22
 */
public abstract class Hash {

    /**
     * 计算图片hash
     *
     * @param bytes 图片内容
     * @return 图片hash
     */
    public byte[] compute(byte[] bytes) {
        BufferedImage image = null;
        try {
            image = ImageUtil.read(bytes);
            if (image == null) throw new RuntimeException("read image failed");
            return compute(image);
        } finally {
            if (image != null) {
                image.flush();
                image.getGraphics().dispose();
            }
        }
    }

    /**
     * 计算图片hash
     *
     * @param image 图片对象
     * @return 图片hash
     */
    public abstract byte[] compute(BufferedImage image);
}
