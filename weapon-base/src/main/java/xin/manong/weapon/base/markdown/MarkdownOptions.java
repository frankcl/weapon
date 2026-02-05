package xin.manong.weapon.base.markdown;

/**
 * Markdown选项
 *
 * @author frankcl
 * @date 2026-02-05 16:08:58
 */
public class MarkdownOptions {

    /**
     * 分块大小
     */
    public Integer chunkSize;
    /**
     * 最小段落大小
     */
    public Integer minParagraphLength;

    MarkdownOptions() {}
    MarkdownOptions(MarkdownOptions options) {
        chunkSize = options.chunkSize;
        minParagraphLength = options.minParagraphLength;
    }

    /**
     * 构建器
     */
    public static class Builder {

        private final MarkdownOptions options;

        public Builder() {
            options = new MarkdownOptions();
        }

        /**
         * 设置分块大小
         *
         * @param chunkSize 分块大小
         * @return 构建器
         */
        public Builder chunkSize(int chunkSize) {
            options.chunkSize = chunkSize;
            return this;
        }

        /**
         * 设置最小段落大小
         *
         * @param minParagraphLength 最小段落大小
         * @return 构建器
         */
        public Builder minParagraphLength(int minParagraphLength) {
            options.minParagraphLength = minParagraphLength;
            return this;
        }

        /**
         * 构建实例
         *
         * @return 实例
         */
        public MarkdownOptions build() {
            return new MarkdownOptions(options);
        }
    }
}
