package cn.zm1001.util.code;

import cn.zm1001.util.code.exception.QrCodeException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dongd_Zhou
 * @desc 二维码
 */
@Slf4j
public class ZXingUtils {
    // 二维码图片默认宽度
    private static final int WIDTH = 200;
    // 二维码图片默认高度
    private static final int HEIGHT = 200;
    // 生成二维码默认图像类型
    private static final String FORMAT = "JPG";
    // LOGO默认边框颜色
    private static final Color LOGO_BORDER_COLOR = new Color(192, 192, 192);// http://xh.5156edu.com/page/z1015m9220j18754.html
    // LOGO默认边框宽度
    private static final int LOGO_BORDER = 2;
    // LOGO大小默认为照片的1/5
    private static final int LOGO_PART = 5;

    private ZXingUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 生成二维码
     *
     * @param os      {@link OutputStream}
     * @param content 需要转为二维码的内容
     */
    public static void qrCode(OutputStream os, String content) {
        qrCode(os, content, WIDTH, HEIGHT);
    }

    /**
     * 生成二维码
     *
     * @param os      {@link OutputStream}
     * @param content 需要转为二维码的内容
     * @param width   图片宽度
     * @param height  图片高度
     */
    public static void qrCode(OutputStream os, String content, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
            MatrixToImageWriter.writeToStream(bitMatrix, FORMAT, os);// 输出图像
            os.flush();
            os.close();
        } catch (Exception e) {
            log.error("#qrCode# #os# ## 生成二维码失败：{}", content, e);
            throw new QrCodeException(e);
        }
    }

    /**
     * 生成二维码
     *
     * @param file    写入文件
     * @param content 需要转为二维码的内容
     */
    public static void qrCode(Path file, String content) {
        qrCode(file, content, WIDTH, HEIGHT);
    }

    /**
     * 生成二维码
     *
     * @param file    写入文件
     * @param content 需要转为二维码的内容
     * @param width   图片宽度
     * @param height  图片高度
     */
    public static void qrCode(Path file, String content, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
            MatrixToImageWriter.writeToPath(bitMatrix, FORMAT, file);// 输出图像
        } catch (Exception e) {
            log.error("#qrCode# #file# ## 生成二维码失败：{}", content, e);
            throw new QrCodeException(e);
        }
    }

    /**
     * 生成二维码(含logo)
     *
     * @param os      {@link OutputStream}
     * @param logoUrl Logo图片地址
     * @param content 需要转为二维码的内容
     */
    public static void qrCode(OutputStream os, String logoUrl, String content) {
        qrCode(os, logoUrl, content, WIDTH, HEIGHT);
    }

    /**
     * 生成二维码(含logo)
     *
     * @param os      {@link OutputStream}
     * @param logoUrl Logo图片地址
     * @param content 需要转为二维码的内容
     * @param width   图片宽度
     * @param height  图片高度
     */
    public static void qrCode(OutputStream os, String logoUrl, String content, int width, int height) {
        final String tempFile = FileUtils.createTempJPG();
        Path path = Paths.get(tempFile);
        // 生成二维码
        qrCode(path, content, width, height);
        // 合成图片
        BufferedImage image = composite(path.toFile(), logoUrl);
        try {
            // 写入LOGO照片到二维码
            ImageIO.write(image, FORMAT, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            log.error("#qrCode# #os# ## 合成二维码失败：{}", content, e);
        } finally {
            final File file = new File(tempFile);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 生成二维码(含logo)
     *
     * @param file    写入文件
     * @param logoUrl Logo图片地址
     * @param content 需要转为二维码的内容
     */
    public static void qrCode(Path file, String logoUrl, String content) {
        qrCode(file, logoUrl, content, WIDTH, HEIGHT);
    }

    /**
     * 生成二维码(含logo)
     *
     * @param file    写入文件
     * @param logoUrl Logo图片地址
     * @param content 需要转为二维码的内容
     * @param width   图片宽度
     * @param height  图片高度
     */
    public static void qrCode(Path file, String logoUrl, String content, int width, int height) {
        // 写入生成二维码
        qrCode(file, content, width, height);
        // 合成图片
        BufferedImage image = composite(file.toFile(), logoUrl);
        try {
            // 写入LOGO照片到二维码
            ImageIO.write(image, FORMAT, file.toFile());
        } catch (Exception e) {
            log.error("#qrCode# #file# ## 合成二维码失败", e);
        }
    }

    /**
     * 合成图片
     *
     * @param qrCodeFile 二维码图片文件
     * @param logoUrl    Logo地址
     * @return 合成后的图片流
     */
    private static BufferedImage composite(File qrCodeFile, String logoUrl) {
        try {
            // 添加LOGO图片, 此处一定需要重新进行读取，而不能直接使用二维码的BufferedImage 对象
            BufferedImage image = ImageIO.read(qrCodeFile);
            BufferedImage logo = ImageIO.read(new URL(logoUrl));

            Graphics2D g = image.createGraphics();
            // 考虑到LOGO照片贴到二维码中，建议大小不要超过二维码的1/5;
            int logoWidth = image.getWidth() / LOGO_PART;
            int logoHeight = image.getHeight() / LOGO_PART;
            // LOGO起始位置，此目的是为LOGO居中显示
            int x = (image.getWidth() - logoWidth) / 2;
            int y = (image.getHeight() - logoHeight) / 2;
            // 绘制图
            g.drawImage(logo, x, y, logoWidth, logoHeight, null);

            // 给LOGO画边框
            // 构造一个具有指定线条宽度以及 cap 和 join 风格的默认值的实心 BasicStroke
            g.setStroke(new BasicStroke(LOGO_BORDER));
            g.setColor(LOGO_BORDER_COLOR);
            g.drawRect(x, y, logoWidth, logoHeight);

            g.dispose();
            return image;
        } catch (Exception e) {
            throw new QrCodeException(e);
        }
    }

}
