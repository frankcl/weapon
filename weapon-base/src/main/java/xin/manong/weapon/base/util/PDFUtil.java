package xin.manong.weapon.base.util;

import com.aspose.pdf.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * PDF工具
 *
 * @author frankcl
 * @date 2025-10-31 13:34:09
 */
public class PDFUtil {

    private static final Logger logger = LoggerFactory.getLogger(PDFUtil.class);

    private static final List<String> WATER_MARK_KEYWORDS = List.of(
            "confidential", "draft", "sample", "watermark", "水印");

    /**
     * 移除PDF文本水印
     *
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @param waterMarkText 水印文本
     * @param exactlyMatch 精确匹配水印文本 true精确匹配水印 false包含水印
     * @return 成功返回true，否则返回false
     */
    public static boolean removeTextWaterMark(String sourcePath, String destPath,
                                              String waterMarkText, boolean exactlyMatch) {
        return removeTextWaterMark(new Document(sourcePath), destPath, waterMarkText, exactlyMatch);
    }

    /**
     * 移除PDF文本水印
     *
     * @param inputStream 源文件输入流
     * @param destPath 目标文件路径
     * @param waterMarkText 水印文本
     * @param exactlyMatch 精确匹配水印文本 true精确匹配水印 false包含水印
     * @return 成功返回true，否则返回false
     */
    public static boolean removeTextWaterMark(InputStream inputStream, String destPath,
                                              String waterMarkText, boolean exactlyMatch) {
        return removeTextWaterMark(new Document(inputStream), destPath, waterMarkText, exactlyMatch);
    }

    /**
     * 移除PDF图片水印
     *
     * @param sourcePath 源文件路径
     * @param minSize 水印最小长宽
     * @param destPath 目标文件路径
     * @return 成功返回true，否则返回false
     */
    public static boolean removeImageWaterMark(String sourcePath,
                                               Integer minSize,
                                               String destPath) {
        return removeImageWaterMark(new Document(sourcePath), minSize, destPath);
    }

    /**
     * 移除PDF图片水印
     *
     * @param inputStream 源文件输入流
     * @param minSize 水印最小长宽
     * @param destPath 目标文件路径
     * @return 成功返回true，否则返回false
     */
    public static boolean removeImageWaterMark(InputStream inputStream,
                                               Integer minSize,
                                               String destPath) {
        return removeImageWaterMark(new Document(inputStream), minSize, destPath);
    }

    /**
     * 移除注解水印
     *
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @return 成功返回true，否则返回false
     */
    public static boolean removeAnnotationWaterMark(String sourcePath, String destPath) {
        return removeAnnotationWaterMark(new Document(sourcePath), destPath);
    }

    /**
     * 移除注解水印
     *
     * @param inputStream 源文件输入流
     * @param destPath 目标文件路径
     * @return 成功返回true，否则返回false
     */
    public static boolean removeAnnotationWaterMark(InputStream inputStream, String destPath) {
        return removeAnnotationWaterMark(new Document(inputStream), destPath);
    }

    /**
     * 移除图片背景水印
     *
     * @param sourcePath 源文件路径
     * @param minSize 水印最小长宽
     * @param destPath 目标文件路径
     * @return 成功返回true，否则返回false
     */
    public static boolean removeWaterMark(String sourcePath,
                                          Integer minSize,
                                          String destPath) {
        return removeWaterMark(new Document(sourcePath), minSize, destPath);
    }

    /**
     * 移除图片背景水印
     *
     * @param inputStream 源文件输入流
     * @param minSize 水印最小长宽
     * @param destPath 目标文件路径
     * @return 成功返回true，否则返回false
     */
    public static boolean removeWaterMark(InputStream inputStream,
                                          Integer minSize,
                                          String destPath) {
        return removeWaterMark(new Document(inputStream), minSize, destPath);
    }

    /**
     * 移除图片背景水印
     *
     * @param document PDF文档
     * @param minSize 水印最小长宽
     * @param destPath 目标文件路径
     * @return 成功返回true，否则返回false
     */
    private static boolean removeWaterMark(Document document,
                                           Integer minSize,
                                           String destPath) {
        try (document) {
            removeAnnotationWaterMark(document);
            removeImageWaterMark(document, minSize);
            document.save(destPath);
            return true;
        } catch (Exception e) {
            logger.error("Remove water mark error", e);
            return false;
        }
    }

    /**
     * 移除注解水印
     *
     * @param document PDF文档
     * @param destPath 目标文件路径
     * @return 成功返回true，否则返回false
     */
    private static boolean removeAnnotationWaterMark(Document document, String destPath) {
        try (document) {
            removeAnnotationWaterMark(document);
            document.save(destPath);
            return true;
        } catch (Exception e) {
            logger.error("Remove annotation water mark error", e);
            return false;
        }
    }

    /**
     * 移除注解水印
     *
     * @param document PDF文档
     */
    private static void removeAnnotationWaterMark(Document document) {
        for (Page page : document.getPages()) {
            AnnotationCollection annotations = page.getAnnotations();
            for (int i = annotations.size(); i >= 1; i--) {
                Annotation annotation = annotations.get_Item(i);
                if (annotation.getAnnotationType() == AnnotationType.Watermark) {
                    annotations.delete(i);
                    continue;
                }
                String contents = annotation.getContents().toLowerCase();
                if (StringUtils.isNotEmpty(contents)) {
                    if (WATER_MARK_KEYWORDS.stream().anyMatch(contents::contains)) {
                        annotations.delete(i);
                    };
                }
            }
        }
    }

    /**
     * 移除图片水印
     *
     * @param document PDF文档
     * @param minSize 水印最小长宽
     * @param destPath 目标文件路径
     * @return 成功返回true，否则返回false
     */
    private static boolean removeImageWaterMark(Document document, Integer minSize, String destPath) {
        try (document) {
            removeImageWaterMark(document, minSize);
            document.save(destPath);
            return true;
        } catch (Exception e) {
            logger.error("Remove image watermark error", e);
            return false;
        }
    }

    /**
     * 移除图片水印
     *
     * @param document PDF文档
     * @param minSize 水印最小长宽
     */
    private static void removeImageWaterMark(Document document, Integer minSize) {
        if (minSize == null || minSize <= 0) minSize = 200;
        for (Page page : document.getPages()) {
            ImagePlacementAbsorber absorber = new ImagePlacementAbsorber();
            page.accept(absorber);
            for (ImagePlacement placement : absorber.getImagePlacements()) {
                Rectangle rect = placement.getRectangle();
                if (rect.getWidth() > minSize && rect.getHeight() > minSize) placement.hide();
            }
        }
    }

    /**
     * 移除文本水印
     *
     * @param document PDF文档
     * @param destPath 目标文件路径
     * @param waterMarkText 水印文本
     * @param exactlyMatch 精确匹配水印文本 true精确匹配水印 false包含水印
     * @return 成功返回true，否则返回false
     */
    private static boolean removeTextWaterMark(Document document, String destPath,
                                               String waterMarkText, boolean exactlyMatch) {
        try (document) {
            removeTextWaterMark(document, waterMarkText, exactlyMatch);
            document.save(destPath);
            return true;
        } catch (Exception e) {
            logger.error("Remove water mark error", e);
            return false;
        }
    }

    /**
     * 移除文本水印
     *
     * @param document PDF文档
     * @param waterMarkText 水印文本
     * @param exactlyMatch 精确匹配水印文本 true精确匹配水印 false包含水印
     */
    private static void removeTextWaterMark(Document document, String waterMarkText, boolean exactlyMatch) {
        TextFragmentAbsorber absorber = new TextFragmentAbsorber();
        document.getPages().accept(absorber);
        TextFragmentCollection textFragments = absorber.getTextFragments();
        for (TextFragment textFragment : textFragments) {
            if (exactlyMatch && textFragment.getText().contentEquals(waterMarkText)) {
                textFragment.setText("");
            } else if (!exactlyMatch && textFragment.getText().contains(waterMarkText)) {
                textFragment.setText("");
            }
        }
    }
}
