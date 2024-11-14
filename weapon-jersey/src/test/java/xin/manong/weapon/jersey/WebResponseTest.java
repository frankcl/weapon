package xin.manong.weapon.jersey;

import jakarta.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2023-03-06 16:58:52
 */
public class WebResponseTest {

    @Test
    public void testBuildOK() {
        WebResponse<String> response = WebResponse.buildOK("OK");
        Assert.assertTrue(response.status);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.code.intValue());
        Assert.assertEquals("OK", response.data);
        Assert.assertNull(response.message);
    }

    @Test
    public void testBuildError() {
        WebResponse<?> response = WebResponse.buildError(Response.Status.NOT_FOUND.getStatusCode(), "NOT FOUND");
        Assert.assertFalse(response.status);
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.code.intValue());
        Assert.assertEquals("NOT FOUND", response.message);
        Assert.assertNull(response.data);
    }
}
