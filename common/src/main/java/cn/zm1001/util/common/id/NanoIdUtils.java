package cn.zm1001.util.common.id;

import org.apache.commons.lang3.Validate;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Desc NanoId工具类
 * NanoId相比UUID要更轻量级
 * 开源实现 https://github.com/aventrix/jnanoid
 * @Author Dongd_Zhou
 */
public class NanoIdUtils {
    public static final char[] DEFAULT_ALPHABET = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    /** 默认返回字符串长度 */
    public static final int DEFAULT_SIZE = 21;

    /**
     * 静态工厂生成url友好，伪随机生成的NanoId字符串
     * 默认使用安全随机数
     * 默认生成的NanoId字符串长度21
     */
    public static String randomNanoId() {
        return secureNanoId();
    }

    private static class Holder {
        static final SecureRandom secureRandom = new SecureRandom();
    }

    /**
     * 获取随机数生成器对象
     * ThreadLocalRandom是JDK 7之后提供并发产生随机数，能够解决多个线程发生的竞争争夺。
     *
     * @return {@link ThreadLocalRandom}
     */
    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * 使用加密的本地线程伪随机数生成器生成该 NanoId。
     *
     * @return 随机生成的 {@code NanoId}
     */
    public static String fastNanoId() {
        return randomNanoId(getRandom(), DEFAULT_ALPHABET, DEFAULT_SIZE);
    }

    /**
     * 使用加密的强伪随机数生成器生成该 NanoId。
     *
     * @return 随机生成的 {@code NanoId}
     */
    public static String secureNanoId() {
        return randomNanoId(Holder.secureRandom, DEFAULT_ALPHABET, DEFAULT_SIZE);
    }

    /**
     * 生成NanoId字符串
     *
     * @param random   随机数生成器
     * @param alphabet NanoId字符串包含的字符
     * @param size     NanoId字符串长度
     * @return 随机生成NanoId字符串
     */
    public static String randomNanoId(final Random random, final char[] alphabet, final int size) {
        Validate.notNull(random, "random cannot be null.");
        Validate.notNull(alphabet, "alphabet cannot be null.");
        Validate.isTrue(alphabet.length > 0 && alphabet.length < 256, "alphabet must contain between 1 and 255 symbols.");
        Validate.isTrue(size > 0, "size must be greater than zero.");

        final int mask = (2 << (int) Math.floor(Math.log(alphabet.length - 1) / Math.log(2))) - 1;
        final int step = (int) Math.ceil(1.6 * mask * size / alphabet.length);

        final StringBuilder idBuilder = new StringBuilder();

        while (true) {
            final byte[] bytes = new byte[step];
            random.nextBytes(bytes);
            for (int i = 0; i < step; i++) {
                final int alphabetIndex = bytes[i] & mask;
                if (alphabetIndex < alphabet.length) {
                    idBuilder.append(alphabet[alphabetIndex]);
                    if (idBuilder.length() == size) {
                        return idBuilder.toString();
                    }
                }
            }
        }
    }
}
