package xin.manong.weapon.base.util;

import com.aspose.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author frankcl
 * @date 2025-10-31 13:34:09
 */
public class PDFUtil {

    private static final Logger logger = LoggerFactory.getLogger(PDFUtil.class);

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
        try (Document document = new Document(sourcePath)) {
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
            document.save(destPath);
            return true;
        } catch (Exception e) {
            logger.error("Remove water mark error", e);
            return false;
        }
    }

    /**
     * 移除图片背景水印
     *
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @return 成功返回true，否则返回false
     */
    public static boolean removeWaterMark(String sourcePath, String destPath) {
        try (Document document = new Document(sourcePath)) {
            for (Page page : document.getPages()) {
                ArtifactCollection artifacts = page.getArtifacts();
                for (int i = artifacts.size(); i > 0; i--) {
                    Artifact artifact = artifacts.get_Item(i);
                    Artifact.ArtifactSubtype subtype = artifact.getSubtype();
                    if (subtype == Artifact.ArtifactSubtype.Watermark ||
                            subtype == Artifact.ArtifactSubtype.Background) {
                        artifacts.delete(i);
                    }
                }
            }
            document.save(destPath);
            return true;
        } catch (Exception e) {
            logger.error("Remove water mark error", e);
            return false;
        }
    }
}
