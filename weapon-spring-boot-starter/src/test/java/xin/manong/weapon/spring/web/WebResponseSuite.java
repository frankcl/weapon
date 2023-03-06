package xin.manong.weapon.spring.web;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;

/**
 * @author frankcl
 * @date 2023-03-06 16:58:52
 */
public class WebResponseSuite {

    @Test
    public void testBuildOK() {
        WebResponse<String> response = WebResponse.buildOK("OK");
        Assert.assertTrue(response.status);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.code.intValue());
        Assert.assertEquals("OK", response.data);
        Assert.assertTrue(response.message == null);
    }

    @Test
    public void testBuildError() {
        WebResponse response = WebResponse.buildError(Response.Status.NOT_FOUND.getStatusCode(), "NOT FOUND");
        Assert.assertFalse(response.status);
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.code.intValue());
        Assert.assertEquals("NOT FOUND", response.message);
        Assert.assertTrue(response.data == null);
    }
}
