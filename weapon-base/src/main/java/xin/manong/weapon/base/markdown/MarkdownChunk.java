package xin.manong.weapon.base.markdown;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Markdown块
 *
 * @author frankcl
 * @date 2025-12-17 15:02:53
 */
@Data
public class MarkdownChunk {

    private int level;
    private int charNum;
    private int seqNo;
    private String heading;
    private Deque<MarkdownSlice> slices;

    public MarkdownChunk(MarkdownHeading markdownHeading) {
        this(markdownHeading == null ? 0 : markdownHeading.getLevel(),
                markdownHeading == null ? null : markdownHeading.getText());
    }

    public MarkdownChunk(int level, String heading) {
        this.level = level;
        this.heading = heading;
        this.charNum = 0;
        this.slices = new LinkedList<>();
    }

    public MarkdownChunk(int level, String heading, MarkdownSlice slice) {
        this(level, heading);
        this.slices = new LinkedList<>();
        this.slices.add(slice);
        this.charNum = this.slices.stream().mapToInt(MarkdownSlice::getCharNum).sum();
    }

    /**
     * 判断块是否为空
     *
     * @return 为空返回true，否则返回false
     */
    @JSONField(serialize = false)
    public boolean isEmpty() {
        return slices == null || slices.isEmpty();
    }

    /**
     * 获取第一个分片
     *
     * @return 第一个分片
     */
    @JSONField(serialize = false)
    public MarkdownSlice getFirstSlice() {
        return slices == null || slices.isEmpty() ? null : slices.getFirst();
    }

    /**
     * 获取最后一个分片
     *
     * @return 最后一个分片
     */
    @JSONField(serialize = false)
    public MarkdownSlice getLastSlice() {
        return slices == null || slices.isEmpty() ? null : slices.getLast();
    }

    /**
     * 获取块文本
     * 标题+内容
     *
     * @return 块文本
     */
    @JSONField(serialize = false)
    public String getChunkText() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty(heading)) builder.append("# ").append(heading).append("\n");
        builder.append(getChunkContent());
        return builder.toString();
    }

    /**
     * 获取块内容
     *
     * @return 块内容
     */
    @JSONField(serialize = false)
    public String getChunkContent() {
        StringBuilder builder = new StringBuilder();
        for (MarkdownSlice slice : slices) {
            if (!builder.isEmpty()) builder.append("\n");
            builder.append(slice.getText());
        }
        return builder.toString();
    }

    /**
     * 获取图片列表
     *
     * @return 图片列表
     */
    @JSONField(serialize = false)
    public List<String> getImages() {
        List<String> images = new ArrayList<>();
        for (MarkdownSlice slice : slices) {
            if (slice.getType() != MarkdownSliceType.IMAGE) continue;
            String url = slice.getUrl();
            if (StringUtils.isNotEmpty(url)) images.add(url);
        }
        return images;
    }

    /**
     * 获取HTML列表
     *
     * @return HTML列表
     */
    @JSONField(serialize = false)
    public List<String> getHTMLs() {
        List<String> htmlList = new ArrayList<>();
        for (MarkdownSlice slice : slices) {
            if (slice.getType() != MarkdownSliceType.HTML) continue;
            String text = slice.getText();
            if (StringUtils.isNotEmpty(text)) htmlList.add(text);
        }
        return htmlList;
    }

    /**
     * 添加片段
     *
     * @param slice 片段
     */
    public void addSlice(MarkdownSlice slice) {
        if (slice == null) return;
        if (slices == null) slices = new LinkedList<>();
        slices.add(slice);
        charNum += slice.getCharNum();
    }

    /**
     * 添加片段列表
     *
     * @param slices 片段列表
     */
    public void addSlices(Deque<MarkdownSlice> slices) {
        if (slices == null) return;
        for (MarkdownSlice slice : slices) addSlice(slice);
    }

    /**
     * 移除最后一个片段
     *
     * @return 最后一个片段，如果为空返回null
     */
    @JSONField(serialize = false)
    public MarkdownSlice removeLastSlice() {
        if (slices == null || slices.isEmpty()) return null;
        MarkdownSlice lastSlice = slices.removeLast();
        charNum -= lastSlice.getCharNum();
        return lastSlice;
    }

    /**
     * 移除第一个片段
     *
     * @return 第一个片段，如果为空返回null
     */
    @JSONField(serialize = false)
    public MarkdownSlice removeFirstSlice() {
        if (slices == null || slices.isEmpty()) return null;
        MarkdownSlice firstSlice = slices.removeFirst();
        charNum -= firstSlice.getCharNum();
        return firstSlice;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty(heading)) {
            builder.append("#").append(heading).append(String.format("[LEVEL:%d,CHARS:%d]\n", level, charNum));
        }
        for (MarkdownSlice slice : slices) {
            if (!builder.isEmpty()) builder.append("\n");
            builder.append(slice.toString());
        }
        return builder.toString();
    }
}
