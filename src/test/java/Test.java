import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by jiabing.zhu on 2016/10/20.
 */
public class Test {
    public static void zoomImageScale(File imageFile, String newPath, int newWidth) throws IOException {
        if(!imageFile.canRead())
            return;
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        if (null == bufferedImage)
            return;

        int originalWidth = bufferedImage.getWidth();
        int originalHeight = bufferedImage.getHeight();
        double scale = (double)originalWidth / (double)newWidth;    // 缩放的比例

        int newHeight =  (int)(originalHeight / scale);

        zoomImageUtils(imageFile, newPath, bufferedImage, newWidth, newHeight);
    }

    private static void zoomImageUtils(File imageFile, String newPath, BufferedImage bufferedImage, int width, int height)
            throws IOException{

        String suffix = StringUtils.substringAfterLast(imageFile.getName(), ".");

        // 处理 png 背景变黑的问题
        if(suffix != null && (suffix.trim().toLowerCase().endsWith("png") || suffix.trim().toLowerCase().endsWith("gif"))){
            BufferedImage to= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = to.createGraphics();
            to = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            g2d.dispose();

            g2d = to.createGraphics();
            Image from = bufferedImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
            g2d.drawImage(from, 0, 0, null);
            g2d.dispose();

            ImageIO.write(to, suffix, new File(newPath));
        }else{
            // 高质量压缩，其实对清晰度而言没有太多的帮助
//            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//            tag.getGraphics().drawImage(bufferedImage, 0, 0, width, height, null);
//
//            FileOutputStream out = new FileOutputStream(newPath);    // 将图片写入 newPath
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
//            jep.setQuality(1f, true);    //压缩质量, 1 是最高值
//            encoder.encode(tag, jep);
//            out.close();

            BufferedImage newImage = new BufferedImage(width, height, bufferedImage.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(bufferedImage, 0, 0, width, height, null);
            g.dispose();
            ImageIO.write(newImage, suffix, new File(newPath));
        }
    }

    /**
     * 按尺寸缩放图片
     *
     * @param imageFile
     * @param newPath
     * @param
     * @throws IOException
     */
    public static void zoomImage(File imageFile, String newPath, int width, int height) throws IOException {
        if (imageFile != null && !imageFile.canRead())
            return;
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        if (null == bufferedImage)
            return;

        zoomImageUtils(imageFile, newPath, bufferedImage, width, height);
    }

    public static void main(String[] args) throws IOException{
        String path = "D:/ICSData/H2&ICS_Data/image/boardroom_1.png";
        String newPath = "D:/ICSData/H2&ICS_Data/image/test.png";
        File imageFile = new File(path);
        zoomImageScale(imageFile,newPath,55);
    }
}
