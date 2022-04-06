package cn.zm1001.util.common;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Desc 字符串工具类
 * @Author Dongd_Zhou
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /** 占位符 */
    private static final String PLACEHOLDER = "{}";
    /** 转义字符 */
    private static final char ESCAPE = '\\';
    /** 下划线 */
    private static final char UNDERLINE = '_';

    /**
     * 将驼峰式方式命名的字符串转换为下划线式（小写）
     * 例如：HelloWorld->hello_world
     *
     * @param str 下划线方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toUnderScore(String str) {
        if (isEmpty(str)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前一个字符是否大写
        boolean previousUpper = false;
        // 当前字符是否大写
        boolean currentUpper;
        // 下一字符是否大写
        boolean nextUpper = true;
        char currentChar;
        for (int i = 0, len = str.length(); i < len; i++) {
            currentChar = str.charAt(i);
            currentUpper = Character.isUpperCase(currentChar);
            if (i > 0) {
                previousUpper = Character.isUpperCase(str.charAt(i - 1));
            }
            if (i < len - 1) {
                nextUpper = Character.isUpperCase(str.charAt(i + 1));
            }
            if (previousUpper && currentUpper && !nextUpper) {
                sb.append(UNDERLINE);
            } else if ((i != 0 && !previousUpper) && currentUpper) {
                sb.append(UNDERLINE);
            }
            sb.append(Character.toLowerCase(currentChar));
        }
        return sb.toString();
    }

    /**
     * 将下划线方式命名的字符串转换为驼峰式
     * 例如：hello_world->HelloWorld
     *
     * @param str 下划线方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toCamel(String str) {
        // 快速检查
        if (isEmpty(str)) {
            return EMPTY;
        } else if (!str.contains("_")) {
            if (1 == str.length()) {
                return str;
            }
            // 不含下划线，仅将首字母大写
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        StringBuilder result = new StringBuilder();
        // 用下划线将原始字符串分割
        String[] camels = str.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            if (1 < camel.length()) {
                result.append(camel.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 格式化字符串，占位符({})按照顺序替换
     * 若想输出占位符({})，使用转义\\{}
     * 例：
     * StringUtils.format("It is {} and {}", "red", "blue") = It is red and blue
     * StringUtils.format("It is \\{} and {}", "red", "blue") = It is {} and red
     * StringUtils.format("It is \\\\{} and {}", "red", "blue") -> It is \red and blue
     *
     * @param pattern 字符串模板
     * @param params  参数列表
     * @return 结果
     */
    public static String format(final String pattern, final Object... params) {
        // 字符串模板为空，参数为空，或者不包占位符，原样返回
        if (isEmpty(pattern) || ArrayUtils.isEmpty(params) || !contains(pattern, PLACEHOLDER)) {
            return pattern;
        }
        final int length = pattern.length();
        StringBuilder sb = new StringBuilder(length + 50);

        int fromIndex = 0;// 从字符串模板索引formIndex开始查找占位符
        int index;// 占位符所在位置
        for (int i = 0, len = params.length; i < len; i++) {
            index = pattern.indexOf(PLACEHOLDER, fromIndex);
            if (index == -1) {
                // 模板剩余部分不包含占位符
                sb.append(pattern, fromIndex, length);
                return sb.toString();
            } else {
                if (index > 0 && pattern.charAt(index - 1) == ESCAPE) {
                    if (index > 1 && pattern.charAt(index - 2) == ESCAPE) {
                        // 转义符被转义，占位符依旧有效
                        sb.append(pattern, fromIndex, index - 1);
                        sb.append(utf8Str(params[i]));
                    } else {
                        i--;// 占位符被转义，参数索引回退，重新开始查找替换
                        sb.append(pattern, fromIndex, index - 1);
                        sb.append(PLACEHOLDER);
                    }
                } else {
                    // 正常占位符
                    sb.append(pattern, fromIndex, index);
                    sb.append(utf8Str(params[i]));
                }
                fromIndex = index + 2;
            }
        }
        // 参数比占位符少时，后续不做处理
        sb.append(pattern, fromIndex, length);
        return sb.toString();
    }

    /**
     * 将对象转为字符串
     * Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String utf8Str(Object obj) {
        return str(obj, StandardCharsets.UTF_8);
    }

    /**
     * 将对象转为字符串
     * Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 对象数组会调用Arrays.toString方法
     *
     * @param obj         对象
     * @param charsetName 字符集
     * @return 字符串
     */
    public static String str(Object obj, String charsetName) {
        return str(obj, Charset.forName(charsetName));
    }

    /**
     * 将对象转为字符串
     * Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 对象数组会调用Arrays.toString方法
     *
     * @param obj     对象
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return str((byte[]) obj, charset);
        } else if (obj instanceof Byte[]) {
            byte[] bytes = ArrayUtils.toPrimitive((Byte[]) obj);
            return str(bytes, charset);
        } else if (obj instanceof ByteBuffer) {
            return str((ByteBuffer) obj, charset);
        }
        return obj.toString();
    }

    /**
     * 将byte数组转为字符串
     *
     * @param bytes   byte数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(byte[] bytes, String charset) {
        return str(bytes, isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        if (null == charset) {
            return new String(data);
        }
        return new String(data, charset);
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, String charset) {
        if (data == null) {
            return null;
        }

        return str(data, Charset.forName(charset));
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, Charset charset) {
        if (null == charset) {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString();
    }
}
