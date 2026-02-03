package xin.manong.weapon.aliyun.dashscope;

import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文本Embedding请求
 *
 * @author frankcl
 * @date 2026-02-03 11:47:03
 */
public class TextEmbeddingRequest {

    private static final Logger logger = LoggerFactory.getLogger(TextEmbeddingRequest.class);

    private static final int DEFAULT_DIMENSION = 768;

    public String text;
    public String instruct;
    public String model;
    public Integer dimension = DEFAULT_DIMENSION;
    public TextEmbeddingParam.TextType textType = TextEmbeddingParam.TextType.DOCUMENT;

    /**
     * 检测有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(text)) {
            logger.error("Embedding text is empty");
            return false;
        }
        if (StringUtils.isEmpty(model)) {
            logger.error("Model is empty");
            return false;
        }
        if (dimension == null || dimension <= 0) dimension = DEFAULT_DIMENSION;
        if (textType == null) textType = TextEmbeddingParam.TextType.DOCUMENT;
        return true;
    }
}
