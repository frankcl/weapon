package xin.manong.weapon.base.util;

import xin.manong.weapon.base.image.ImageMeta;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2020-05-06 10:30:05
 */
public class ImageUtilSuite {

    private String jpegFile = this.getClass().getResource("/image/jpeg_image.jpg").getPath();
    private String cmykFile = this.getClass().getResource("/image/cmyk_image.jpeg").getPath();
    private String animatedFile = this.getClass().getResource("/image/animated_image.jpg").getPath();
    private String webpFile = this.getClass().getResource("/image/webp_image.webp").getPath();
    private String bmpFile = this.getClass().getResource("/image/bmp_image.bmp").getPath();
    private String pngFile = this.getClass().getResource("/image/png_image.png").getPath();

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
        Assert.assertTrue(ImageUtil.read(jpegBytes) != null);
        Assert.assertTrue(ImageUtil.read(cmykBytes) != null);
        Assert.assertTrue(ImageUtil.read(webpBytes) != null);
        Assert.assertTrue(ImageUtil.read(pngBytes) != null);
        Assert.assertTrue(ImageUtil.read(bmpBytes) != null);
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
            Assert.assertEquals(1, imageMeta.imageNum);
            Assert.assertEquals(165L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(252L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("JPEG", imageMeta.format);
        }
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(animatedBytes);
            Assert.assertEquals(12, imageMeta.imageNum);
            Assert.assertEquals(575L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(1035L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("GIF", imageMeta.format);
        }
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(cmykBytes);
            Assert.assertEquals(1, imageMeta.imageNum);
            Assert.assertEquals(283L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(390L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("JPEG", imageMeta.format);
        }
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(webpBytes);
            Assert.assertEquals(1, imageMeta.imageNum);
            Assert.assertEquals(300L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(400L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("WEBP", imageMeta.format);
        }
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(bmpBytes);
            Assert.assertEquals(1, imageMeta.imageNum);
            Assert.assertEquals(600L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(338L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("BMP", imageMeta.format);
        }
        {
            ImageMeta imageMeta = ImageUtil.getImageMeta(pngBytes);
            Assert.assertEquals(1, imageMeta.imageNum);
            Assert.assertEquals(128L, imageMeta.imageSizes[0].width);
            Assert.assertEquals(128L, imageMeta.imageSizes[0].height);
            Assert.assertEquals("PNG", imageMeta.format);
        }
    }

    @Test
    public void testReadError() {
        Assert.assertTrue(ImageUtil.read(new byte[0]) == null);
    }

    @Test
    public void testGetImageMetaError() {
        Assert.assertTrue(ImageUtil.getImageMeta(new byte[0]) == null);
    }
}
