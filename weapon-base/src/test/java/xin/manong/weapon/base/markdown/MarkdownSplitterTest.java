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
        String source = "/Users/frankcl/Downloads/00038739dacd88fa2fae94e37fa900f8.md";
        long startTime = System.currentTimeMillis();
        List<MarkdownChunk> chunks = MarkdownSplitter.split(source, 3000);
        System.out.printf("Chunk num:%d, process time: %dms\n", chunks.size(), System.currentTimeMillis() - startTime);
        chunks.forEach(chunk -> System.out.println(chunk.getSeqNo() + " " + chunk.getChunkText() + "\n"));
        chunks.forEach(chunk -> {
            List<String> images = chunk.getImages();
            if (!images.isEmpty()) {
                System.out.println(chunk.getSeqNo() + " " + chunk.getHeading());
                System.out.println(images);
            }
        });
        chunks.forEach(chunk -> {
            List<String> htmLs = chunk.getHTMLs();
            if (!htmLs.isEmpty()) {
                System.out.println(chunk.getSeqNo() + " " + chunk.getHeading());
                System.out.println(htmLs);
            }
        });
    }
}
