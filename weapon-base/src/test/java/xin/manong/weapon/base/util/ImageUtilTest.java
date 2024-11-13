package xin.manong.weapon.base.util;

import xin.manong.weapon.base.image.ImageMeta;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * @author frankcl
 * @date 2020-05-06 10:30:05
 */
public class ImageUtilTest {

    private final String jpegFile = Objects.requireNonNull(this.getClass().
            getResource("/image/jpeg_image.jpg")).getPath();
    private final String cmykFile = Objects.requireNonNull(this.getClass().
            getResource("/image/cmyk_image.jpeg")).getPath();
    private final String animatedFile = Objects.requireNonNull(this.getClass().
            getResource("/image/animated_image.jpg")).getPath();
    private final String webpFile = Objects.requireNonNull(this.getClass().
            getResource("/image/webp_image.webp")).getPath();
    private final String bmpFile = Objects.requireNonNull(this.getClass().
            getResource("/image/bmp_image.bmp")).getPath();
    private final String pngFile = Objects.requireNonNull(this.getClass().
            getResource("/image/png_image.png")).getPath();

    @Test
    public void testIsAnimatedImage() {
        byte[] jpegBytes = FileUtil.read(jpegFile);
        byte[] cmykBytes = FileUtil.read(cmykFile);
        byte[] animatedBytes = FileUtil.read(animatedFile);
        Assert.assertFalse(ImageUtil.isAnimatedImage(jpegBytes));
        Assert.assertFalse(ImageUtil.isAnimatedImage(cmykBytes));
        Assert.assertTrue(ImageUtil.isAnimatedImage(animatedBytes));
        Assert.assertFalse(ImageUtil.isAnimatedImage(null));
    }

    @Test
    public void testRead() throws Exception {
        byte[] jpegBytes = FileUtil.read(jpegFile);
        byte[] cmykBytes = FileUtil.read(cmykFile);
        byte[] webpBytes = FileUtil.read(webpFile);
        byte[] bmpBytes = FileUtil.read(bmpFile);
        byte[] pngBytes = FileUtil.read(pngFile);
        Assert.assertTrue(jpegBytes != null && jpegBytes.length > 0);
        Assert.assertTrue(cmykBytes != null && cmykBytes.length > 0);
        Assert.assertTrue(webpBytes != null && webpBytes.length > 0);
        Assert.assertTrue(bmpBytes != null && bmpBytes.length > 0);
        Assert.assertTrue(pngBytes != null && pngBytes.length > 0);
        Assert.assertNotNull(ImageUtil.read(jpegBytes));
        Assert.assertNotNull(ImageUtil.read(cmykBytes));
        Assert.assertNotNull(ImageUtil.read(webpBytes));
        Assert.assertNotNull(ImageUtil.read(pngBytes));
        Assert.assertNotNull(ImageUtil.read(bmpBytes));
    }

    @Test
    public void testGetImageMeta() throws Exception {
        byte[] jpegBytes = FileUtil.read(jpegFile);
        byte[] animatedBytes = FileUtil.read(animatedFile);
        byte[] cmykBytes = FileUtil.read(cmykFile);
        byte[] webpBytes = FileUtil.read(webpFile);
        byte[] bmpBytes = FileUtil.read(bmpFile);
        byte[] pngBytes = FileUtil.read(pngFile);
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(jpegBytes);
            Assert.assertNotNull(imageMeta);
            Assert.assertEquals(1, imageMeta.imageNum);
            Assert.assertEquals(165L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(252L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("JPEG", imageMeta.format);
        }
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(animatedBytes);
            Assert.assertNotNull(imageMeta);
            Assert.assertEquals(12, imageMeta.imageNum);
            Assert.assertEquals(575L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(1035L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("GIF", imageMeta.format);
        }
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(cmykBytes);
            Assert.assertNotNull(imageMeta);
            Assert.assertEquals(1, imageMeta.imageNum);
            Assert.assertEquals(283L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(390L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("JPEG", imageMeta.format);
        }
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(webpBytes);
            Assert.assertNotNull(imageMeta);
            Assert.assertEquals(1, imageMeta.imageNum);
            Assert.assertEquals(300L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(400L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("WEBP", imageMeta.format);
        }
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(bmpBytes);
            Assert.assertNotNull(imageMeta);
            Assert.assertEquals(1, imageMeta.imageNum);
            Assert.assertEquals(600L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(338L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("BMP", imageMeta.format);
        }
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(pngBytes);
            Assert.assertNotNull(imageMeta);
            Assert.assertEquals(1, imageMeta.imageNum);
            Assert.assertEquals(128L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(128L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("PNG", imageMeta.format);
        }
    }

    @Test
    public void testReadError() {
        Assert.assertNull(ImageUtil.read(new byte[0]));
    }

    @Test
    public void testGetImageMetaError() {
        Assert.assertNull(ImageUtil.getImageMeta(new byte[0]));
    }
}
