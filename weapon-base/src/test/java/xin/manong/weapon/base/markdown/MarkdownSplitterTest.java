package xin.manong.weapon.base.markdown;

import org.junit.Test;

import java.util.List;

/**
 * @author frankcl
 * @date 2025-12-19 16:56:32
 */
public class MarkdownSplitterTest {

    @Test
    public void testSplit() throws Exception {
        String source = "/Users/frankcl/Downloads/0000300384482732b88cc3071c065dae.md";
        List<MarkdownChunk> chunks = MarkdownSplitter.split(source, 3000);
        chunks.forEach(chunk -> System.out.println(chunk.buildText() + "\n"));
    }
}
