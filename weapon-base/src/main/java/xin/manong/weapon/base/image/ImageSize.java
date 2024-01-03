package xin.manong.weapon.base.image;

/**
 * 图片尺寸
 *
 * @author frankcl
 * @date 2024-01-03 14:32:51
 */
public class ImageSize {

    /**
     * 图片宽度
     */
    public long width;
    /**
     * 图片高度
     */
    public long height;

    public ImageSize(ImageSize imageSize) {
        this(imageSize.width, imageSize.height);
    }


    public ImageSize(long width, long height) {
        this.width = width;
        this.height = height;
    }
}
