package xin.manong.weapon.base.markdown;

import org.junit.Test;

import java.util.List;

/**
 * @author frankcl
 * @date 2025-12-19 16:56:32
 */
public class MarkdownToolTest {

    @Test
    public void testSplit() throws Exception {
        String source = "/Users/frankcl/Downloads/00038739dacd88fa2fae94e37fa900f8.md";
        long startTime = System.currentTimeMillis();
        MarkdownOptions options = new MarkdownOptions.Builder().chunkSize(3000).build();
        List<MarkdownChunk> chunks = MarkdownTool.chunking(source, options);
        System.out.printf("Chunk num:%d, process time: %dms\n", chunks.size(), System.currentTimeMillis() - startTime);
        chunks.forEach(chunk -> {
            System.out.println("No." + chunk.getNumber() + " " + chunk.getHeading() + "(" + chunk.getChars() + ")");
            System.out.println(chunk.getChunkContent());
        });
        chunks.forEach(chunk -> {
            List<String> images = chunk.getImages();
            if (!images.isEmpty()) {
                System.out.println(chunk.getNumber() + " " + chunk.getHeading());
                System.out.println(images);
            }
        });
        chunks.forEach(chunk -> {
            List<String> htmLs = chunk.getHTMLs();
            if (!htmLs.isEmpty()) {
                System.out.println(chunk.getNumber() + " " + chunk.getHeading());
                System.out.println(htmLs);
            }
        });
    }
}
