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
        byte[] matrix = (byte[]) processedImage.getData().getDataElements(0, 0, SIZE, SIZE, null);
        long sum = 0;
        for (byte b : matrix) sum += (long) b & 0xff;
        int mean = Math.round((float) sum / matrix.length);
        byte[] hash = new byte[matrix.length / 8];
        for (int i = 0; i < matrix.length; i++) {
            int remainder = i % 8, quotient = i / 8;
            if (remainder == 0) hash[quotient] = 0x00;
            if (((int) matrix[i] & 0xff) >= mean) hash[quotient] = (byte) ((hash[quotient] | (1 << remainder)) & 0xff);
        }
        return hash;
    }
}
