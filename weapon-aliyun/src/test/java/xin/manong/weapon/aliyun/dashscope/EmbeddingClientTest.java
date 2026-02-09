package xin.manong.weapon.aliyun.dashscope;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author frankcl
 * @date 2026-02-03 14:26:46
 */
public class EmbeddingClientTest {

    private EmbeddingClient client;

    @Before
    public void setUp() throws Exception {
        EmbeddingClientConfig config = new EmbeddingClientConfig();
        config.baseURL = "https://dashscope.aliyuncs.com/api/v1";
        client = new EmbeddingClient(config);
        Assert.assertTrue(client.open());
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void testCall() {
        TextEmbeddingRequest request = new TextEmbeddingRequest.Builder().
                text("测试文本").model("text-embedding-v4").build();
        Double[] vector = client.textEmbedding(request);
        Assert.assertNotNull(vector);
        Assert.assertEquals(768, vector.length);
    }
}
