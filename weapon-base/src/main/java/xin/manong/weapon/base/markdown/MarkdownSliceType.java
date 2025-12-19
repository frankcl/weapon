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

    UNKNOWN(0, "UNKNOWN"),
    PARAGRAPH(1, "PARAGRAPH"),
    LIST(2, "LIST"),
    IMAGE(3, "IMAGE"),
    HTML(4, "HTML");

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
