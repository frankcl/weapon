package xin.manong.weapon.base.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * @author frankcl
 * @date 2019-05-28 13:58
 */
public class FileUtilTest {

    @Test
    public void testReadNotFoundFile() {
        byte[] content = FileUtil.read("/unknown/unknown.dat");
        Assert.assertNull(content);
    }

    @Test
    public void testReadNormalFile() {
        String path = Objects.requireNonNull(FileUtilTest.class.getResource(String.format("%s.class",
                FileUtilTest.class.getSimpleName()))).getFile();
        byte[] content = FileUtil.read(path);
        Assert.assertTrue(content != null && content.length > 0);
    }

    @Test
    public void testGetFileSuffix() {
        Assert.assertEquals("", FileUtil.getFileSuffix(null));
        Assert.assertEquals("", FileUtil.getFileSuffix(""));
        Assert.assertEquals("jar", FileUtil.getFileSuffix("test.jar"));
        Assert.assertEquals("cpp", FileUtil.getFileSuffix("test.cpp"));
    }

}
