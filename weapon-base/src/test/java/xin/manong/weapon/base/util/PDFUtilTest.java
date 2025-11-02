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
        String path = "/Users/frankcl/Desktop/GRF3042.pdf";
        String dest = "/Users/frankcl/Desktop/test.pdf";
        Assert.assertTrue(PDFUtil.removeTextWaterMark(path, dest, "芯查查", true));
    }
}
