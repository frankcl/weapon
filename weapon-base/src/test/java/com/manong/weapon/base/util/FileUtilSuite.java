package com.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @create 2019-05-28 13:58
 */
public class FileUtilSuite {

    @Test
    public void testReadNotFoundFile() {
        byte[] content = FileUtil.read("/unknown/unknown.dat");
        Assert.assertTrue(content == null);
    }

    @Test
    public void testReadNormalFile() {
        String path = FileUtilSuite.class.getResource(String.format("%s.class",
                FileUtilSuite.class.getSimpleName())).getFile();
        byte[] content = FileUtil.read(path);
        Assert.assertTrue(content != null && content.length > 0);
    }

}
