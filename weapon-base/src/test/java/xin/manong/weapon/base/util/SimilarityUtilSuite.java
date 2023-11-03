package xin.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2022-07-20 11:00:05
 */
public class SimilarityUtilSuite {

    @Test
    public void testCoefficientLCS() {
        Assert.assertEquals(1.0d, SimilarityUtil.coefficientLCS("很多烈士都没有照片", "很多烈士都没有照片"), 0.1d);
        Assert.assertEquals(0.8484d, SimilarityUtil.coefficientLCS("一定要为他们创作独一无二的肖像作品", "定要为他们创作独一元二的背像作品"), 0.1d);
        Assert.assertEquals(0.9090d, SimilarityUtil.coefficientLCS("不一定能找到亲人的喜地", "不一定能找到亲人的墓地"), 0.1d);
        Assert.assertEquals(0d, SimilarityUtil.coefficientLCS("", "不一定能找到亲人的墓地"), 0.1d);
    }

    @Test
    public void testCoefficientJaccard() {
        Assert.assertEquals(1.0d, SimilarityUtil.coefficientJaccard("很多烈士都没有照片", "很多烈士都没有照片"), 0.1d);
        Assert.assertEquals(0.2222d, SimilarityUtil.coefficientJaccard("一定要为他们创作独一无二的肖像作品", "定要为他们创作独一元二的背像作品"), 0.1d);
        Assert.assertEquals(0.6d, SimilarityUtil.coefficientJaccard("不一定能找到亲人的喜地", "不一定能找到亲人的墓地"), 0.1d);
        Assert.assertEquals(0d, SimilarityUtil.coefficientJaccard("", "不一定能找到亲人的墓地"), 0.1d);
    }
}
