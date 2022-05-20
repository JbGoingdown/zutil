package cn.zm1001.util.code;

import cn.zm1001.util.code.exception.BarCodeException;
import lombok.extern.slf4j.Slf4j;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

/**
 * @author Dongd_Zhou
 * @desc 条码工具类
 */
@Slf4j
public class BarCodeUtils {
    private static final String FORMAT = "image/jpeg";
    private static final int dpi = 100;// 精细度

    private BarCodeUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 生成条码
     *
     * @param os      {@link OutputStream}
     * @param content 条码内容
     */
    public static void code128(OutputStream os, String content) {
        try {
            final Code128Bean bean = new Code128Bean();
            // module宽度
            final double moduleWidth = UnitConv.in2mm(3.0f / dpi);
            // 配置对象
            bean.setModuleWidth(moduleWidth);
            bean.doQuietZone(false);
            bean.setFontSize(0);
            // 输出到流
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(os, FORMAT, dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            // 生成条形码
            bean.generateBarcode(canvas, content);
            // 结束绘制
            canvas.finish();
        } catch (Exception e) {
            log.error("## ## ## 生成条码失败：{}", content, e);
            throw new BarCodeException(e);
        }
    }

}
