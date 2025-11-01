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
     * 移除PDF水印
     *
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @param waterMark 水印文本
     * @return 成功返回true，否则返回false
     */
    public static boolean removeWaterMark(String sourcePath, String destPath,
                                          String waterMark) {
        try (Document document = new Document(sourcePath)) {
            TextFragmentAbsorber absorber = new TextFragmentAbsorber();
            document.getPages().accept(absorber);
            TextFragmentCollection textFragments = absorber.getTextFragments();
            for (TextFragment textFragment : textFragments) {
                if (textFragment.getText().contains(waterMark)) {
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
}
