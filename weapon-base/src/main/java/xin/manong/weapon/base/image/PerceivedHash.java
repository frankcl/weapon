package xin.manong.weapon.base.image;

import xin.manong.weapon.base.util.ImageUtil;

import java.awt.image.BufferedImage;

/**
 * 图片感知Hash
 *
 * @author frankcl
 * @date 2023-08-02 15:18:07
 */
public class PerceivedHash extends Hash {

    private static final int SIZE = 32;
    private static final int SAMPLE_SIZE = 8;

    private static final double[] coefficientsDCT = initCoefficientsDCT();

    /**
     * 初始化DCT系数
     *
     * @return DCT系数
     */
    private static double[] initCoefficientsDCT() {
        double[] coefficients = new double[SIZE];
        for (int i = 1; i < SIZE; i++) coefficients[i] = 1;
        coefficients[0] = 1 / Math.sqrt(2.0);
        return coefficients;
    }

    /**
     * 计算坐标(x, y)的余弦变换
     *
     * @param metrics 输入矩阵
     * @param x 坐标x
     * @param y 坐标y
     * @return 余弦变换结果
     */
    private double computeDCT(double[][] metrics, int x, int y) {
        double sum = 0.0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sum += Math.cos(((2 * i + 1) / (2.0 * SIZE)) * x * Math.PI) *
                        Math.cos(((2 * j + 1) / (2.0 * SIZE)) * y * Math.PI) * (metrics[i][j]);
            }
        }
        return sum * ((coefficientsDCT[x] * coefficientsDCT[y]) / 4.0);
    }

    /**
     * 进行离散余弦变换，达到去燥效果
     *
     * @param input 待变换矩阵
     * @return 变换后矩阵
     */
    private double[][] applyDCT(double[][] input) {
        double[][] output = new double[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                output[i][j] = computeDCT(input, i, j);
            }
        }
        return output;
    }

    @Override
    public byte[] compute(BufferedImage image) {
        BufferedImage processedImage = ImageUtil.resize(image, SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        ImageUtil.gray(processedImage);
        double[][] matrix = new double[SIZE][SIZE];
        for (int x = 0; x < processedImage.getWidth(); x++) {
            for (int y = 0; y < processedImage.getHeight(); y++) {
                matrix[x][y] = processedImage.getRGB(x, y) & 0xff;
            }
        }
        double[][] matrixDCT = applyDCT(matrix);
        /*
         * 保留左上角8*8矩阵，这部分表示图片的低频部分
         * 计算8*8矩阵平均值（排除矩阵第一个元素）
         */
        double total = 0;
        for (int x = 0; x < SAMPLE_SIZE; x++) {
            for (int y = 0; y < SAMPLE_SIZE; y++) {
                total += matrixDCT[x][y];
            }
        }
        total -= matrixDCT[0][0];
        double mean = total / (double) (SAMPLE_SIZE * SAMPLE_SIZE - 1);

        /*
         * 8*8矩阵元素与平均值比较，大于平均值相应位设置1，否则设置0
         * 最终获得64位hash数组
         */
        byte[] hash = new byte[SAMPLE_SIZE];
        for (int x = 0; x < SAMPLE_SIZE; x++) {
            hash[x] = 0x00;
            for (int y = 0; y < SAMPLE_SIZE; y++) {
                if (x == 0 && y == 0) continue;
                if (matrixDCT[x][y] <= mean) continue;
                hash[x] = (byte) ((hash[x] | (1 << (SAMPLE_SIZE - y - 1))) & 0xff);
            }
        }
        return hash;
    }
}
