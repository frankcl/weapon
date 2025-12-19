package xin.manong.weapon.base.markdown;

import com.vladsch.flexmark.ast.Heading;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Markdown标题
 *
 * @author frankcl
 * @date 2025-12-17 15:57:11
 */
public class MarkdownHeading {

    private static final String HEADING_SEPARATOR = "^";
    @Getter
    private int level;
    @Getter
    private String text;
    private final Deque<Heading> headings;

    public MarkdownHeading() {
        headings = new LinkedList<>();
    }

    /**
     * 添加标题
     *
     * @param heading 标题
     */
    public void add(Heading heading) {
        if (heading == null || heading.getText().toString().isEmpty()) return;
        Heading last = headings.isEmpty() ? null : headings.peekLast();
        while (last != null && last.getLevel() >= heading.getLevel()) {
            headings.removeLast();
            last = headings.peekLast();
            int lastSlash = text.lastIndexOf(HEADING_SEPARATOR);
            text = text.substring(0, lastSlash == -1 ? 0 : lastSlash);
        }
        headings.addLast(heading);
        level = heading.getLevel();
        if (text == null) text = "";
        text += StringUtils.isEmpty(text) ? heading.getText() : HEADING_SEPARATOR + heading.getText();
    }
}
