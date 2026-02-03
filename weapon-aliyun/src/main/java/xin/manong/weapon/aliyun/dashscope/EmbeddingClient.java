package xin.manong.weapon.aliyun.dashscope;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Embedding客户端
 *
 * @author frankcl
 * @date 2026-02-03 11:32:15
 */
public class EmbeddingClient {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingClient.class);

    private final EmbeddingClientConfig config;
    private TextEmbedding textEmbedding;

    public EmbeddingClient(EmbeddingClientConfig config) {
        this.config = config;
    }

    /**
     * 打开
     *
     * @return 成功返回true，否则返回false
     */
    public boolean open() {
        logger.info("Embedding client is opening ...");
        if (config == null) {
            logger.error("Embedding client config is null");
            throw new IllegalArgumentException("Embedding client config is null");
        }
        config.check();
        textEmbedding = new TextEmbedding(config.baseURL);
        logger.info("Embedding client open success");
        return true;
    }

    /**
     * 关闭
     */
    public void close() {
        logger.info("Embedding client is closing ...");
        if (textEmbedding != null) textEmbedding = null;
        logger.info("Embedding client close success");
    }

    /**
     * 计算文本稠密向量embedding
     *
     * @param request 计算请求
     * @return 成功返回向量数组，否则返回null
     */
    public Double[] textEmbedding(TextEmbeddingRequest request) {
        if (request == null) throw new IllegalArgumentException("Text embedding request is null");
        if (!request.check()) throw new IllegalArgumentException("Invalid text embedding request");
        if (StringUtils.isEmpty(DashScopeApiKey.apiKey)) throw new IllegalArgumentException("Api key is not config");
        TextEmbeddingParam.TextEmbeddingParamBuilder<?, ?> builder = TextEmbeddingParam.builder().
                text(request.text).dimension(request.dimension).model(request.model).apiKey(DashScopeApiKey.apiKey).
                textType(request.textType).outputType(TextEmbeddingParam.OutputType.DENSE);
        if (request.textType == TextEmbeddingParam.TextType.QUERY &&
                StringUtils.isNotEmpty(request.instruct)) {
            builder.instruct(request.instruct);
        }
        try {
            TextEmbeddingResult result = textEmbedding.call(builder.build());
            if (result.getStatusCode() != 200) {
                logger.error("Text embedding failed, code:{}, cause:{}", result.getCode(), result.getMessage());
                return null;
            }
            TextEmbeddingResultItem item = result.getOutput().getEmbeddings().get(0);
            return item.getEmbedding().toArray(new Double[0]);
        } catch (Exception e) {
            logger.error("Text embedding exception occurred");
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
