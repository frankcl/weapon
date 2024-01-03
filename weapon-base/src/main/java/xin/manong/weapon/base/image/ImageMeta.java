package xin.manong.weapon.base.image;

/**
 * 图片信息
 *
 * @author frankcl
 * @date 2022-07-23 09:52:12
 */
public class ImageMeta {

    /**
     * 图片数量
     * 动图字图片数量大于等于1
     **/
    public int imageNum;
    /**
     * 图片格式
     **/
    public String format;
    /**
     * 图片大小
     */
    public ImageSize[] imageSizes;

    public ImageMeta(ImageSize[] imageSizes, String format) {
        this.imageNum = imageSizes.length;
        this.format = format;
        this.imageSizes = new ImageSize[imageNum];
        for (int i = 0; i < imageNum; i++) this.imageSizes[i] = new ImageSize(imageSizes[i]);
    }
}
