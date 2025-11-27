package xin.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2025-11-02 14:53:23
 */
public class PDFUtilTest {

    @Test
    public void testRemoveTextWaterMark() {
        String path = "/Users/frankcl/Downloads/d560db0f3736566551e83ce4630d1e9d.pdf";
        String dest = "/Users/frankcl/Desktop/test.pdf";
        Assert.assertTrue(PDFUtil.removeTextWaterMark(path, dest, "芯查查", true));
    }

    @Test
    public void testRemoveImageWaterMark() {
        String path = "/Users/frankcl/Desktop/JW1550 Datasheet_R0.1_EN_20200616 _for 纳微半导体.pdf";
        String dest = "/Users/frankcl/Desktop/test.pdf";
        Assert.assertTrue(PDFUtil.removeImageWaterMark(path, 300, dest));
    }
}
