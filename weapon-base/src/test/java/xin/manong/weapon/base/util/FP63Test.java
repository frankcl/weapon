package xin.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2019-05-27 20:13
 */
public class FP63Test {

    @Test
    public void testRoutine() {
        String string = "frank";
        Assert.assertEquals(-925790194224426764L, FP63.newFP63(string));
        Assert.assertEquals(-925790194224426764L,
                FP63.newFP63(string.getBytes(), 0, string.getBytes().length));
    }

}
