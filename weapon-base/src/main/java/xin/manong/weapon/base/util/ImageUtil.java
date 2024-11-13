package xin.manong.weapon.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.image.ImageMeta;
import xin.manong.weapon.base.image.ImageSize;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

/**
 * 图片工具
 *
 * @author frankcl
 * @date 2020-05-06 10:17:55
 */
public class ImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    private static final String READER_SUFFIX_GIF = "GIF";

    private static final ColorConvertOp colorConverter = new ColorConvertOp(
            ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

    /*
     * 不使用ImageIO默认缓存
     */
    static { ImageIO.setUseCache(false); }

    /**
     * 判断是否为动画图片
     * 判断标准：1. 图片格式为GIF 2. 包含多张图片
     *
     * @param byteArray 图片内容
     * @return 如果为动画图片返回true，否则返回false
     */
    public static boolean isAnimatedImage(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 3) return false;
        if (!(byteArray[0] == 'G' && byteArray[1] == 'I' && byteArray[2] == 'F')) return false;
        ImageInputStream imageInputStream;
        ImageReader reader = ImageIO.getImageReadersBySuffix(READER_SUFFIX_GIF).next();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        try {
            imageInputStream = ImageIO.createImageInputStream(byteArrayInputStream);
            reader.setInput(imageInputStream);
            return reader.getNumImages(true) > 1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            closeImageReader(reader);
        }
    }

    /**
     * 获取图片信息
     *
     * @param byteArray 图片数据
     * @return 如果成功返回图片信息，否则返回null
     */
    public static ImageMeta getImageMeta(byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) return null;
        ImageReader reader = getImageReader(byteArray, false);
        if (reader == null) return null;
        try {
            int n = reader.getNumImages(true);
            ImageSize[] imageSizes = new ImageSize[n];
            for (int i = 0; i < n; i++) {
                imageSizes[i] = new ImageSize(reader.getWidth(i), reader.getHeight(i));
            }
            return new ImageMeta(imageSizes, reader.getFormatName().toUpperCase());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            closeImageReader(reader);
        }
    }

    /**
     * 灰度图片
     *
     * @param image 图片信息
     */
    public static void gray(BufferedImage image) {
        colorConverter.filter(image, image);
    }

    /**
     * 缩放图片
     *
     * @param image 图片信息
     * @param width 缩放后宽度
     * @param height 缩放后高度
     * @param imageType 缩放后图片类型
     * @return 缩放后图片信息
     */
    public static BufferedImage resize(BufferedImage image, int width,
                                       int height, int imageType) {
        BufferedImage resizeImage = new BufferedImage(width, height, imageType);
        Graphics2D graphics = resizeImage.createGraphics();
        try {
            graphics.drawImage(image.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH), 0, 0, null);
            return resizeImage;
        } finally {
            graphics.dispose();
        }
    }

    /**
     * 读取图片
     *
     * @param byteArray 图片内容
     * @return 如果成功返回BufferedImage实例，否则返回null
     */
    public static BufferedImage read(byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) return null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        try {
            return ImageIO.read(inputStream);
        } catch (IIOException e) {
            ImageReader reader = getImageReader(byteArray, true);
            if (reader == null) {
                logger.error("unsupported image format for reading, cause[{}]", e.getMessage());
                return null;
            }
            try {
                Raster raster = reader.readRaster(0, null);
                BufferedImage image = new BufferedImage(raster.getWidth(), raster.getHeight(),
                        BufferedImage.TYPE_4BYTE_ABGR);
                image.getRaster().setRect(raster);
                return image;
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return null;
            } finally {
                closeImageReader(reader);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 根据图片内容获取ImageReader
     *
     * @param byteArray 图片内容
     * @param raster ImageReader是否需要支持raster
     * @return 如果存在符合图片格式的ImageReader则返回，否则返回null
     */
    private static ImageReader getImageReader(byte[] byteArray, boolean raster) {
        ImageReader reader = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(byteArrayInputStream);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
            while (iterator.hasNext()) {
                reader = iterator.next();
                if (!raster || reader.canReadRaster()) break;
            }
            if (reader == null) {
                logger.error("image reader is not found");
                return null;
            }
            reader.setInput(imageInputStream);
            return reader;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            closeImageReader(reader);
            return null;
        }
    }

    /**
     * 关闭ImageReader
     *
     * @param reader ImageReader
     */
    private static void closeImageReader(ImageReader reader) {
        if (reader == null) return;
        reader.dispose();
        Object input = reader.getInput();
        try {
            if (input instanceof InputStream) ((InputStream) input).close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
