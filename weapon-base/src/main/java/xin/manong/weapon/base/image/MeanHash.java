package xin.manong.weapon.base.image;

import xin.manong.weapon.base.util.ImageUtil;

import java.awt.image.BufferedImage;

/**
 * 图片均值hash
 *
 * @author frankcl
 * @date 2023-08-02 17:18:05
 */
public class MeanHash extends Hash {

    private static final int SIZE = 8;

    @Override
    public byte[] compute(BufferedImage image) {
        if (image == null) throw new RuntimeException("input image is null");
        BufferedImage processedImage = ImageUtil.resize(image, SIZE, SIZE, BufferedImage.TYPE_3BYTE_BGR);
        ImageUtil.gray(processedImage);
        byte[] bytes = (byte[]) processedImage.getData().getDataElements(0, 0, SIZE, SIZE, null);
        long sum = 0;
        for (byte b : bytes) sum += (long) b & 0xff;
        int mean = Math.round((float) sum / bytes.length);
        /**
         * 计算均值hash（24字节，192位）
         */
        byte[] hash = new byte[bytes.length / 8];
        for (int i = 0; i < bytes.length; i++) {
            int m = i % 8, n = i / 8;
            if (m == 0) hash[n] = 0x00;
            if (((int) bytes[i] & 0xff) >= mean) hash[n] = (byte) ((hash[n] | (1 << m)) & 0xff);
        }
        return hash;
    }
}
