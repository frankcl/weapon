package xin.manong.weapon.base.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2023-02-28 11:33:59
 */
public class ThreadContextTest {

    @Test
    public void testThreadContextOperation() {
        Assert.assertNull(ThreadContext.getContext());
        Context context = new Context();
        ThreadContext.setContext(context);
        Assert.assertSame(ThreadContext.getContext(), context);
        ThreadContext.commit("k1", "v1");
        Assert.assertTrue(context.contains("k1"));
        Assert.assertEquals("v1", (String) context.get("k1"));
        ThreadContext.removeContext();
        Assert.assertNull(ThreadContext.getContext());
    }
}
