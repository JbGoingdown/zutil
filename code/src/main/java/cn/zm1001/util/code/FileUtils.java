package cn.zm1001.util.code;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * @author Dongd_Zhou
 * @desc 文件工具类
 */
@Slf4j
public class FileUtils {
    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 生成临时文件
     *
     * @param fileName 文件名
     * @return 文件地址
     */
    public static String createTempFile(String fileName) {
        try {
            File file = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
            file.createNewFile();
            // 返回临时文件的路径
            return file.getCanonicalPath();
        } catch (IOException e) {
            log.error("#createTempFile# ## ## 生成临时文件异常：{}", fileName, e);
        }
        return null;
    }

    /**
     * 生成临时图片文件
     *
     * @return JPG文件地址
     */
    public static String createTempJPG() {
        return createTempFile(System.currentTimeMillis() + ".jpg");
    }
}
