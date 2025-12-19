package xin.manong.weapon.base.markdown;

import lombok.Getter;

/**
 * Markdown片段类型
 *
 * @author frankcl
 * @date 2025-12-17 15:03:28
 */
@Getter
public enum MarkdownSliceType {

    PARAGRAPH(1, "PARAGRAPH"),
    TABLE(2, "TABLE"),
    LIST(3, "LIST"),
    IMAGE(4, "IMAGE"),
    HTML(5, "HTML"),
    UNKNOWN(6, "UNKNOWN");

    private final int code;
    private final String name;

    MarkdownSliceType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据片段编码获取片段类型
     *
     * @param code 片段编码
     * @return 片段类型
     */
    public static MarkdownSliceType valueOf(int code) {
        for (MarkdownSliceType type : MarkdownSliceType.values()) {
            if (type.code == code) return type;
        }
        return null;
    }
}
