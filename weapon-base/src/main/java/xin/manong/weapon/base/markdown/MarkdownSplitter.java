package xin.manong.weapon.base.markdown;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown文档切分器
 *
 * @author frankcl
 * @date 2025-12-17 15:31:58
 */
public class MarkdownSplitter {

    private static final Logger logger = LoggerFactory.getLogger(MarkdownSplitter.class);

    private static final int DEFAULT_MIN_CHUNK_SIZE = 500;
    private static final Pattern SENTENCE_END_PATTERN = Pattern.compile("[。？！?!]|\\.\\s|\r?\n");

    public static int minChunkSize = DEFAULT_MIN_CHUNK_SIZE;

    /**
     * 切分markdown文档
     *
     * @param source 数据路径，支持本地和HTTP
     * @param chunkSize 块大小，最小500
     * @return 分块列表
     */
    public static List<MarkdownChunk> split(String source, int chunkSize) {
        if (minChunkSize <= 0) minChunkSize = DEFAULT_MIN_CHUNK_SIZE;
        chunkSize = Math.max(chunkSize, minChunkSize);
        Document document = parseSource(source);
        if (document == null) throw new IllegalStateException("解析Markdown失败");
        List<MarkdownChunk> chunks = new ArrayList<>();
        MarkdownHeading markdownHeading = new MarkdownHeading();
        Node node = document.getFirstChild();
        if (node == null) return chunks;
        split(node, markdownHeading, chunkSize, new StringBuilder(), chunks);
        return chunks;
    }

    /**
     * Markdown分片
     *
     * @param node 节点
     * @param markdownHeading heading
     * @param chunkSize 块大小
     * @param buffer 文本buffer
     * @param chunks 分片列表
     */
    private static void split(Node node, MarkdownHeading markdownHeading, int chunkSize,
                              StringBuilder buffer, List<MarkdownChunk> chunks) {
        if (node == null) return;
        if (node instanceof Heading) {
            markdownHeading.add((Heading) node);
            MarkdownChunk chunk = new MarkdownChunk(markdownHeading.getLevel(), markdownHeading.getText());
            pushChunk(chunk, chunks, chunkSize);
            split(node.getNext(), markdownHeading, chunkSize, new StringBuilder(), chunks);
        } else if (node instanceof Paragraph) {
            StringBuilder newBuffer = new StringBuilder();
            split(node.getFirstChild(), markdownHeading, chunkSize, newBuffer, chunks);
            List<MarkdownChunk> newChunks = buildChunk(MarkdownSliceType.PARAGRAPH,
                    newBuffer.toString(), null, chunkSize, markdownHeading);
            pushChunks(newChunks, chunks, chunkSize);
            split(node.getNext(), markdownHeading, chunkSize, new StringBuilder(), chunks);
        } else if (node instanceof ListBlock) {
            handleSpecialNode(node, buffer, chunkSize, chunks, MarkdownSliceType.LIST, markdownHeading);
        } else if (node instanceof HtmlBlockBase) {
            handleSpecialNode(node, buffer, chunkSize, chunks, MarkdownSliceType.HTML, markdownHeading);
        } else if (node instanceof Image) {
            handleSpecialNode(node, buffer, chunkSize, chunks, MarkdownSliceType.IMAGE, markdownHeading);
        } else {
            buffer.append(node.getChars());
            split(node.getNext(), markdownHeading, chunkSize, buffer, chunks);
        }
    }

    /**
     * 处理特殊节点
     *
     * @param node 节点
     * @param buffer 文本buffer
     * @param chunkSize 块大小
     * @param chunks 分块结果
     * @param type 节点类型
     * @param markdownHeading 标题
     */
    private static void handleSpecialNode(Node node, StringBuilder buffer, int chunkSize,
                                          List<MarkdownChunk> chunks, MarkdownSliceType type,
                                          MarkdownHeading markdownHeading) {
        if (buffer != null && !buffer.isEmpty()) {
            List<MarkdownChunk> newChunks = buildChunk(MarkdownSliceType.PARAGRAPH,
                    buffer.toString(), null, chunkSize, markdownHeading);
            pushChunks(newChunks, chunks, chunkSize);
        }
        String url = node instanceof Image ? ((Image) node).getUrl().toString() : null;
        List<MarkdownChunk> newChunks = buildChunk(type, node.getChars().toString(), url, chunkSize, markdownHeading);
        pushChunks(newChunks, chunks, chunkSize);
        StringBuilder newBuffer = new StringBuilder();
        split(node.getNext(), markdownHeading, chunkSize, newBuffer, chunks);
        if (!newBuffer.isEmpty()) {
            pushChunks(buildChunk(MarkdownSliceType.PARAGRAPH, newBuffer.toString(), null,
                    chunkSize, markdownHeading), chunks, chunkSize);
        }
    }

    /**
     * 添加新分块
     *
     * @param newChunks 新分块
     * @param chunks 分块列表
     * @param chunkSize 分块大小
     */
    private static void pushChunks(List<MarkdownChunk> newChunks, List<MarkdownChunk> chunks, int chunkSize) {
        if (newChunks == null) return;
        for (MarkdownChunk newChunk : newChunks) pushChunk(newChunk, chunks, chunkSize);
    }

    /**
     * 添加新分块
     * 如果前一分块与新分块可以合并则触发合并
     *
     * @param newChunk 新分块
     * @param chunks 分块列表
     * @param chunkSize 分块大小
     */
    private static void pushChunk(MarkdownChunk newChunk, List<MarkdownChunk> chunks, int chunkSize) {
        if (chunks.isEmpty()) {
            chunks.add(newChunk);
            return;
        }
        MarkdownChunk prevChunk = chunks.get(chunks.size() - 1);
        if (shouldMergeChunk(prevChunk, newChunk, chunkSize)) {
            if (newChunk.isEmpty()) return;
            if (prevChunk.isEmpty()) {
                prevChunk.addSlices(newChunk.getSlices());
                return;
            }
            MarkdownSlice prevSlice = prevChunk.removeLastSlice();
            MarkdownSlice newSlice = newChunk.removeFirstSlice();
            if (shouldMergeSlice(prevSlice, newSlice, chunkSize)) {
                String text = prevSlice.getText() + "\n" + newSlice.getText();
                MarkdownSlice mergedSlice = new MarkdownSlice(prevSlice.getType(), text);
                prevChunk.addSlice(mergedSlice);
            } else {
                prevChunk.addSlice(prevSlice);
                prevChunk.addSlice(newSlice);
            }
            prevChunk.addSlices(newChunk.getSlices());
            return;
        }
        if (prevChunk.isEmpty()) chunks.remove(chunks.size() - 1);
        chunks.add(newChunk);
    }

    /**
     * 分片是否可以合并
     *
     * @param prevSlice 上一个分片
     * @param newSlice 新分片
     * @param chunkSize 块大小
     * @return 可以合并返回true，否则返回false
     */
    private static boolean shouldMergeSlice(MarkdownSlice prevSlice, MarkdownSlice newSlice, int chunkSize) {
        if (prevSlice.getCharNum() + newSlice.getCharNum() > chunkSize) return false;
        return prevSlice.getType() == newSlice.getType();
    }

    /**
     * 块是否可以合并
     *
     * @param prevChunk 上一个分块
     * @param newChunk 新分块
     * @param chunkSize 块大小
     * @return 可以合并返回true，否则返回false
     */
    private static boolean shouldMergeChunk(MarkdownChunk prevChunk, MarkdownChunk newChunk, int chunkSize) {
        if (prevChunk.getCharNum() + newChunk.getCharNum() > chunkSize) return false;
        if (prevChunk.getLevel() != newChunk.getLevel()) return false;
        return Objects.equals(prevChunk.getHeading(), newChunk.getHeading());
    }

    /**
     * 处理句子
     * 如果当前文本加上句子不超过块大小，则将句子加入当前文本
     * 否则当前文本成为独立块，并重置当前文本，并将句子加入当前文本
     *
     * @param textBuilder 文本构建器
     * @param text 点前段文本
     * @param chunks 分块列表
     * @param markdownHeading 标题
     * @param startOffset 句子起始偏移
     * @param endOffset 句子结束偏移
     * @param chunkSize 块大小
     * @return 文本构建器
     */
    private static StringBuilder handleSentence(StringBuilder textBuilder, String text,
                                                List<MarkdownChunk> chunks, MarkdownHeading markdownHeading,
                                                int startOffset, int endOffset, int chunkSize) {
        if (textBuilder.length() + endOffset - startOffset > chunkSize) {
            MarkdownSlice slice = new MarkdownSlice(MarkdownSliceType.PARAGRAPH, textBuilder.toString());
            chunks.add(new MarkdownChunk(markdownHeading.getLevel(), markdownHeading.getText(), slice));
            textBuilder = new StringBuilder();
        }
        textBuilder.append(text, startOffset, endOffset);
        return textBuilder;
    }

    /**
     * 构建chunk
     *
     * @param type 分片类型
     * @param text 分片文本
     * @param url URL
     * @param chunkSize 块大小
     * @param markdownHeading heading
     * @return 块列表
     */
    private static List<MarkdownChunk> buildChunk(MarkdownSliceType type, String text, String url,
                                                  int chunkSize, MarkdownHeading markdownHeading) {
        List<MarkdownChunk> chunks = new ArrayList<>();
        if (type == MarkdownSliceType.PARAGRAPH && text.length() > chunkSize) {
            int start = 0;
            StringBuilder textBuilder = new StringBuilder();
            Matcher matcher = SENTENCE_END_PATTERN.matcher(text);
            while (matcher.find()) {
                textBuilder = handleSentence(textBuilder, text, chunks,
                        markdownHeading, start, matcher.end(), chunkSize);
                start = matcher.end();
            }
            if (start < text.length()) {
                textBuilder = handleSentence(textBuilder, text, chunks, markdownHeading,
                        start, text.length(), chunkSize);
            }
            if (!textBuilder.isEmpty()) {
                MarkdownSlice slice = new MarkdownSlice(MarkdownSliceType.PARAGRAPH, textBuilder.toString());
                chunks.add(new MarkdownChunk(markdownHeading.getLevel(), markdownHeading.getText(), slice));
            }
            return chunks;
        }
        MarkdownSlice slice = new MarkdownSlice(type, text);
        if (StringUtils.isNotEmpty(url)) slice.setUrl(url);
        chunks.add(new MarkdownChunk(markdownHeading.getLevel(), markdownHeading.getText(), slice));
        return chunks;
    }

    /**
     * 解析Markdown文档
     *
     * @param source 数据源地址
     * @return Markdown文档
     */
    private static Document parseSource(String source) {
        try (BufferedReader reader = getReader(source)) {
            Parser parser = Parser.builder().build();
            return parser.parseReader(reader);
        } catch (IOException e) {
            logger.error("Parse markdown failed from source:{}", source, e);
            return null;
        }
    }

    /**
     * 根据数据源获取数据reader
     *
     * @param source 数据源路径
     * @return 数据reader
     * @throws IOException I/O异常
     */
    private static BufferedReader getReader(String source) throws IOException {
        InputStream inputStream;
        if (source.startsWith("http://") || source.startsWith("https://")) {
            inputStream = new URL(source).openStream();
        } else {
            inputStream = new FileInputStream(source);
        }
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
