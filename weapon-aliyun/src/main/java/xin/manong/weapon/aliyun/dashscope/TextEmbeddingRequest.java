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
    public String apiKey;
    public Integer dimension = DEFAULT_DIMENSION;
    public TextEmbeddingParam.TextType textType = TextEmbeddingParam.TextType.DOCUMENT;

    private TextEmbeddingRequest() {}

    private TextEmbeddingRequest(TextEmbeddingRequest request) {
        text = request.text;
        instruct = request.instruct;
        model = request.model;
        apiKey = request.apiKey;
        dimension = request.dimension;
        textType = request.textType;
    }

    /**
     * 请求构建器
     */
    public static class Builder {

        private final TextEmbeddingRequest delegate;

        public Builder() {
            delegate = new TextEmbeddingRequest();
        }

        /**
         * 设置embedding文本
         *
         * @param text embedding文本
         * @return 构建器
         */
        public TextEmbeddingRequest.Builder text(String text) {
            delegate.text = text;
            return this;
        }

        /**
         * 设置模型
         *
         * @param model 模型
         * @return 构建器
         */
        public TextEmbeddingRequest.Builder model(String model) {
            delegate.model = model;
            return this;
        }

        /**
         * 设置指令
         *
         * @param instruct 指令
         * @return 构建器
         */
        public TextEmbeddingRequest.Builder instruct(String instruct) {
            delegate.instruct = instruct;
            return this;
        }

        /**
         * 设置维数
         *
         * @param dimension 维数
         * @return 构建器
         */
        public TextEmbeddingRequest.Builder dimension(Integer dimension) {
            delegate.dimension = dimension;
            return this;
        }

        /**
         * 设置API key
         *
         * @param apiKey API key
         * @return 构建器
         */
        public TextEmbeddingRequest.Builder apiKey(String apiKey) {
            delegate.apiKey = apiKey;
            return this;
        }

        /**
         * 设置embedding文本类型
         *
         * @param textType 文本类型
         * @return 构建器
         */
        public TextEmbeddingRequest.Builder textType(TextEmbeddingParam.TextType textType) {
            delegate.textType = textType;
            return this;
        }

        /**
         * 构建实例
         *
         * @return 实例
         */
        public TextEmbeddingRequest build() {
            return new TextEmbeddingRequest(delegate);
        }
    }

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
