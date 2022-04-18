package cn.zm1001.util.common.id;

/**
 * @Desc ID生成器工具类
 * @Author Dongd_Zhou
 */
public class IdUtils {
    private IdUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取随机UUID
     *
     * @return 随机36位UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 32位简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 36位随机UUID
     */
    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 32位简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() {
        return UUID.fastUUID().toString(true);
    }

    /**
     * 获取随机NanoId
     *
     * @return 随机21位NanoId
     */
    public static String randomNanoId() {
        return NanoIdUtils.randomNanoId();
    }

    /**
     * 获取随机NanoId，使用性能更好的ThreadLocalRandom生成NanoId
     *
     * @return 21位随机NanoId
     */
    public static String fastNanoId() {
        return NanoIdUtils.fastNanoId();
    }
}
