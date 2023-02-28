package xin.manong.weapon.base.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2023-02-28 11:33:59
 */
public class ThreadContextSuite {

    @Test
    public void testThreadContextOperation() {
        Assert.assertTrue(ThreadContext.getContext() == null);
        Context context = new Context();
        ThreadContext.setContext(context);
        Assert.assertTrue(ThreadContext.getContext() == context);
        ThreadContext.commit("k1", "v1");
        Assert.assertTrue(context.contains("k1"));
        Assert.assertEquals("v1", (String) context.get("k1"));
        ThreadContext.removeContext();
        Assert.assertTrue(ThreadContext.getContext() == null);
    }
}
