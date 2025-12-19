package xin.manong.weapon.base.markdown;

import lombok.Data;

/**
 * Markdown片段
 *
 * @author frankcl
 * @date 2025-12-17 15:02:15
 */
@Data
public class MarkdownSlice {

    private MarkdownSliceType type;
    private int charNum;
    private String text;
    private String url;

    public MarkdownSlice(MarkdownSliceType type, String text) {
        this.type = type;
        this.text = text;
        this.charNum = text == null ? 0 : text.length();
    }

    @Override
    public String toString() {
        return String.format("[%s:%d]%s", type.getName(), charNum, text);
    }
}
