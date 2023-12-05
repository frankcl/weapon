package xin.manong.weapon.base.image;

import org.junit.Assert;
import org.junit.Test;
import xin.manong.weapon.base.util.ByteArrayUtil;
import xin.manong.weapon.base.util.FileUtil;

/**
 * @author frankcl
 * @date 2023-08-02 17:23:07
 */
public class PerceivedHashSuite {

    private String hashImageFile1 = this.getClass().getResource("/image/hash_image_1.jpg").getPath();
    private String hashImageFile2 = this.getClass().getResource("/image/hash_image_2.jpg").getPath();
    private String hashImageFile3 = this.getClass().getResource("/image/hash_image_3.jpeg").getPath();

    @Test
    public void testComputeHash() {
        byte[] imageBytes1 = FileUtil.read(hashImageFile1);
        byte[] imageBytes2 = FileUtil.read(hashImageFile2);
        byte[] imageBytes3 = FileUtil.read(hashImageFile3);
        Assert.assertTrue(imageBytes1 != null && imageBytes1.length > 0);
        Assert.assertTrue(imageBytes2 != null && imageBytes2.length > 0);
        Assert.assertTrue(imageBytes3 != null && imageBytes3.length > 0);
        Hash hash = new PerceivedHash();
        byte[] hash1 = hash.compute(imageBytes1);
        byte[] hash2 = hash.compute(imageBytes2);
        byte[] hash3 = hash.compute(imageBytes3);
        Assert.assertEquals("0011110100111101110000101100001011000001000111110001111100111100",
                ByteArrayUtil.byteArrayToBinary(hash1));
        Assert.assertEquals("0011110000111101110000101100001111000001000111110001111100011100",
                ByteArrayUtil.byteArrayToBinary(hash2));
        Assert.assertEquals("0010000011101101001101110111110101111100101110111100100101011111",
                ByteArrayUtil.byteArrayToBinary(hash3));
        Assert.assertEquals(3, ByteArrayUtil.distance(hash1, hash2));
    }
}
