package xin.manong.weapon.base.base.common;

/**
 * 图片信息
 *
 * @author frankcl
 * @date 2022-07-23 09:52:12
 */
public class ImageMeta {

    public int imageNum;
    public long[] width;
    public long[] height;
    public String format;

    public ImageMeta(int imageNum, String format) {
        this.imageNum = imageNum;
        this.format = format;
        width = new long[imageNum];
        height = new long[imageNum];
    }
}
