package xin.manong.weapon.base.image;

import org.junit.Assert;
import org.junit.Test;
import xin.manong.weapon.base.util.ByteArrayUtil;
import xin.manong.weapon.base.util.FileUtil;

/**
 * @author frankcl
 * @date 2023-08-02 17:35:05
 */
public class MeanHashSuite {

    private String hashImageFile1 = this.getClass().getResource("/image/hash_image_1.jpg").getPath();
    private String hashImageFile2 = this.getClass().getResource("/image/hash_image_2.jpg").getPath();

    @Test
    public void testComputeHash() {
        byte[] imageBytes1 = FileUtil.read(hashImageFile1);
        byte[] imageBytes2 = FileUtil.read(hashImageFile2);
        Assert.assertTrue(imageBytes1 != null && imageBytes1.length > 0);
        Assert.assertTrue(imageBytes2 != null && imageBytes2.length > 0);
        Hash hash = new MeanHash();
        byte[] hash1 = hash.compute(imageBytes1);
        byte[] hash2 = hash.compute(imageBytes2);
        Assert.assertEquals("000000000000000000000000001111111000000000011111000001111000000011111111000001111111000011111111110001111111111111111111000000001111000011111111111111111111111111111111111111111111111111111111",
                ByteArrayUtil.byteArrayToBinString(hash1));
        Assert.assertEquals("000000000000000000000000000001111000000000011111000001111000000011111111000001111111111011111111110001111111111111111111000000001111111011111111111111111111111111111111111111111111111111111111",
                ByteArrayUtil.byteArrayToBinString(hash2));
        Assert.assertEquals(9, ByteArrayUtil.distance(hash1, hash2));
    }
}
