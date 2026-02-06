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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown工具
 *
 * @author frankcl
 * @date 2025-12-17 15:31:58
 */
public class MarkdownTool {

    private static final Logger logger = LoggerFactory.getLogger(MarkdownTool.class);

    private static final int DEFAULT_MIN_CHUNK_SIZE = 500;
    private static final Pattern SENTENCE_END_PATTERN = Pattern.compile("[。？！?!]|\\.\\s|\r?\n");
    private static final Set<MarkdownSliceType> chunkSliceTypes = Set.of(
            MarkdownSliceType.PARAGRAPH, MarkdownSliceType.HTML, MarkdownSliceType.IMAGE, MarkdownSliceType.LIST);

    private static int minChunkSize = DEFAULT_MIN_CHUNK_SIZE;


    /**
     * 设置最小块大小
     *
     * @param minChunkSize 最小块大小
     */
    public static void setMinChunkSize(int minChunkSize) {
        if (minChunkSize < 100) minChunkSize = 100;
        MarkdownTool.minChunkSize = minChunkSize;
    }

    /**
     * 切分markdown文档
     *
     * @param source 数据路径，支持本地和HTTP
     * @param options 选项
     * @return 分块列表
     */
    public static List<MarkdownChunk> chunking(String source, MarkdownOptions options) {
        MarkdownOptions markdownOptions = new MarkdownOptions(options);
        markdownOptions.chunkSize = Math.max(markdownOptions.chunkSize, minChunkSize);
        Document document = parseSource(source);
        if (document == null) throw new IllegalStateException("解析Markdown失败");
        Node node = document.getFirstChild();
        if (node == null) return new ArrayList<>();
        return chunking(node, markdownOptions);
    }

    /**
     * 根据文本构建分块
     *
     * @param textBuilder 文本构建器
     * @param heading 标题
     * @param chunks 分块列表
     * @param options 选项
     */
    private static void buildTextChunk(StringBuilder textBuilder, MarkdownHeading heading,
                                       List<MarkdownChunk> chunks, MarkdownOptions options) {
        if (textBuilder.isEmpty()) return;
        List<MarkdownChunk> newChunks = buildChunk(MarkdownSliceType.PARAGRAPH,
                textBuilder.toString(), heading, null, options);
        pushChunks(newChunks, chunks, options);
        textBuilder.setLength(0);
    }

    /**
     * 构建分块
     *
     * @param type 分片类型
     * @param text 文本
     * @param options 选项
     * @param url URL
     * @param heading 标题
     * @return 分块列表
     */
    private static List<MarkdownChunk> buildChunk(MarkdownSliceType type, String text,
                                                  MarkdownHeading heading, String url,
                                                  MarkdownOptions options) {
        if (type == MarkdownSliceType.PARAGRAPH) {
            if (options.minParagraphLength != null && text.length() <= options.minParagraphLength) {
                logger.debug("Filter short paragraph:{}", text);
                return List.of();
            }
            if (text.length() > options.chunkSize) return chunkingLongText(text, options.chunkSize, heading);
        }
        MarkdownSlice slice = new MarkdownSlice(type, text);
        if (StringUtils.isNotEmpty(url)) slice.setUrl(url);
        return List.of(new MarkdownChunk(heading.getLevel(), heading.getText(), slice));
    }

    /**
     * 长文本分块
     *
     * @param text 长文本
     * @param chunkSize 分块大小
     * @param heading 标题
     * @return 分块列表
     */
    private static List<MarkdownChunk> chunkingLongText(String text, int chunkSize,
                                                        MarkdownHeading heading) {
        int start = 0;
        StringBuilder builder = new StringBuilder();
        Matcher matcher = SENTENCE_END_PATTERN.matcher(text);
        List<MarkdownChunk> chunks = new ArrayList<>();
        while (matcher.find()) {
            int end = matcher.end(), size = builder.length();
            if (size > 0 && size + end - start > chunkSize) {
                MarkdownSlice slice = new MarkdownSlice(MarkdownSliceType.PARAGRAPH, builder.toString());
                chunks.add(new MarkdownChunk(heading.getLevel(), heading.getText(), slice));
                builder.setLength(0);
            }
            builder.append(text, start, end);
            start = end;
        }
        int size = builder.length();
        if (size > 0 && size + text.length() - start > chunkSize) {
            MarkdownSlice slice = new MarkdownSlice(MarkdownSliceType.PARAGRAPH, builder.toString());
            chunks.add(new MarkdownChunk(heading.getLevel(), heading.getText(), slice));
            builder.setLength(0);
        }
        if (start < text.length()) builder.append(text, start, text.length());
        if (!builder.isEmpty()) {
            MarkdownSlice slice = new MarkdownSlice(MarkdownSliceType.PARAGRAPH, builder.toString());
            chunks.add(new MarkdownChunk(heading.getLevel(), heading.getText(), slice));
        }
        return chunks;
    }

    /**
     * 根据节点后去片段类型
     *
     * @param node markdown节点
     * @return 片段类型
     */
    private static MarkdownSliceType sliceType(Node node) {
        if (node instanceof Paragraph) return MarkdownSliceType.PARAGRAPH;
        if (node instanceof Image) return MarkdownSliceType.IMAGE;
        if (node instanceof ListBlock) return MarkdownSliceType.LIST;
        if (node instanceof HtmlBlockBase) return MarkdownSliceType.HTML;
        return MarkdownSliceType.UNKNOWN;
    }

    /**
     * 段落分块
     *
     * @param node 段落节点
     * @param textBuilder 文本构建器
     * @param heading 标题
     * @param chunks 分块列表
     * @param options 选项
     */
    private static void chunkingParagraph(Node node, StringBuilder textBuilder,
                                          MarkdownHeading heading, List<MarkdownChunk> chunks,
                                          MarkdownOptions options) {
        if (options.minParagraphLength != null && node.getChars().length() <= options.minParagraphLength) return;
        Node child = node.getFirstChild();
        while (child != null) {
            child = processNode(child, textBuilder, heading, chunks, options);
            if (child != null) child = child.getNext();
        }
        if (!textBuilder.isEmpty()) textBuilder.append("\n");
    }

    /**
     * 填充图片描述文本
     *
     * @param node 图片节点
     * @param nodeText 节点文本构建器
     * @return 存在返回文本节点，否则返回null
     */
    private static Node fillImageText(Node node, StringBuilder nodeText) {
        Node textNode = null;
        if (!(node instanceof Image)) return textNode;
        while (true) {
            node = node.getNext();
            if (!(node instanceof HardLineBreak) && !(node instanceof Emphasis)) return textNode;
            String text = node.getChars().toString();
            node = node.getNext();
            if (!(node instanceof Text)) return textNode;
            nodeText.append(text).append(node.getChars());
            textNode = node;
        }
    }

    /**
     * 处理节点
     *
     * @param node 节点
     * @param textBuilder 文本构建器
     * @param heading 标题
     * @param chunks 分块列表
     * @param options 选项
     * @return 当前处理节点
     */
    private static Node processNode(Node node, StringBuilder textBuilder,
                                    MarkdownHeading heading, List<MarkdownChunk> chunks,
                                    MarkdownOptions options) {
        Node processingNode = node;
        MarkdownSliceType sliceType = sliceType(node);
        if (node instanceof Heading) {
            buildTextChunk(textBuilder, heading, chunks, options);
            heading.add((Heading) node);
        } else if (chunkSliceTypes.contains(sliceType)) {
            buildTextChunk(textBuilder, heading, chunks, options);
            String url = node instanceof Image ? ((Image) node).getUrl().toString() : null;
            StringBuilder nodeText = new StringBuilder();
            nodeText.append(node.getChars());
            Node next = fillImageText(node, nodeText);
            if (next != null) processingNode = next;
            List<MarkdownChunk> newChunks = buildChunk(sliceType, nodeText.toString(), heading, url, options);
            pushChunks(newChunks, chunks, options);
        } else {
            textBuilder.append(node.getChars());
        }
        return processingNode;
    }

    /**
     * Markdown分块
     * @param node Markdown首节点
     * @param options 选项
     * @return 分块列表
     */
    private static List<MarkdownChunk> chunking(Node node, MarkdownOptions options) {
        List<MarkdownChunk> chunks = new ArrayList<>();
        StringBuilder textBuilder = new StringBuilder();
        MarkdownHeading heading = new MarkdownHeading();
        Deque<Node> queue = new LinkedList<>();
        queue.addLast(node);
        while (!queue.isEmpty()) {
            node = queue.removeFirst();
            if (node == null) continue;
            if (node instanceof Paragraph) chunkingParagraph(node, textBuilder, heading, chunks, options);
            else node = processNode(node, textBuilder, heading, chunks, options);
            if (node != null && node.getNext() != null) queue.addLast(node.getNext());
        }
        if (!textBuilder.isEmpty()) {
            List<MarkdownChunk> newChunks = buildChunk(MarkdownSliceType.PARAGRAPH,
                    textBuilder.toString(), heading, null, options);
            pushChunks(newChunks, chunks, options);
        }
        return chunks;
    }

    /**
     * 添加新分块
     *
     * @param newChunks 新分块
     * @param chunks 分块列表
     * @param options 选项
     */
    private static void pushChunks(List<MarkdownChunk> newChunks,
                                   List<MarkdownChunk> chunks, MarkdownOptions options) {
        if (newChunks == null || newChunks.isEmpty()) return;
        for (MarkdownChunk newChunk : newChunks) pushChunk(newChunk, chunks, options);
    }

    /**
     * 添加新分块
     * 如果前一分块与新分块可以合并则触发合并
     *
     * @param newChunk 新分块
     * @param chunks 分块列表
     * @param options 选项
     */
    private static void pushChunk(MarkdownChunk newChunk, List<MarkdownChunk> chunks,
                                  MarkdownOptions options) {
        if (chunks.isEmpty()) {
            newChunk.setNumber(1);
            chunks.add(newChunk);
            return;
        }
        MarkdownChunk prevChunk = chunks.get(chunks.size() - 1);
        if (shouldMergeChunk(prevChunk, newChunk, options.chunkSize)) {
            if (newChunk.isEmpty()) return;
            if (prevChunk.isEmpty()) {
                prevChunk.addSlices(newChunk.getSlices());
                return;
            }
            MarkdownSlice prevSlice = prevChunk.removeLastSlice();
            MarkdownSlice newSlice = newChunk.removeFirstSlice();
            if (shouldMergeSlice(prevSlice, newSlice, options.chunkSize)) {
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
        newChunk.setNumber(chunks.size() + 1);
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
        if (prevSlice.getChars() + newSlice.getChars() > chunkSize) return false;
        if (prevSlice.getType() == MarkdownSliceType.IMAGE) return false;
        if (prevSlice.getType() == MarkdownSliceType.HTML) return false;
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
        if (prevChunk.getChars() + newChunk.getChars() > chunkSize) return false;
        if (prevChunk.getLevel() != newChunk.getLevel()) return false;
        return Objects.equals(prevChunk.getHeading(), newChunk.getHeading());
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
