package cn.zm1001.util.common.id;

import cn.zm1001.util.common.exception.UUIDException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Desc UUID, 提供通用唯一识别码（universally unique identifier）
 * 与 java.util.UUID 大体相同，增加了 ThreadLocalRandom
 * @Author Dongd_Zhou
 */
public class UUID implements java.io.Serializable, Comparable<UUID> {
    /**
     * Explicit serialVersionUID for interoperability.
     */
    private static final long serialVersionUID = 8812638568281825467L;

    /**
     * The most significant 64 bits of this UUID.
     * UUID的最高64有效位
     *
     * @serial
     */
    private final long mostSigBits;

    /**
     * The least significant 64 bits of this UUID.
     * UUID的最低64有效位
     *
     * @serial
     */
    private final long leastSigBits;

    /**
     * The random number generator used by this class to create random
     * based UUIDs. In a holder class to defer initialization until needed.
     * SecureRandom 的单例
     */
    private static class Holder {
        // java.util.UUID中实现
        // static final SecureRandom numberGenerator = new SecureRandom();
        static final SecureRandom numberGenerator = getSecureRandom();
    }

    /**
     * 获取{@link SecureRandom}，类提供加密的强随机数生成器 (RNG)
     *
     * @return {@link SecureRandom}
     */
    public static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new UUIDException(e);
        }
    }

    /**
     * 获取随机数生成器对象<br>
     * ThreadLocalRandom是JDK 7之后提供并发产生随机数，能够解决多个线程发生的竞争争夺。
     *
     * @return {@link ThreadLocalRandom}
     */
    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    // Constructors and Factories

    /**
     * Private constructor which uses a byte array to construct the new UUID.
     * 私有构造
     */
    private UUID(byte[] data) {
        long msb = 0;
        long lsb = 0;
        assert data.length == 16 : "data must be 16 bytes in length";
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (data[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);
        this.mostSigBits = msb;
        this.leastSigBits = lsb;
    }

    /**
     * Constructs a new {@code UUID} using the specified data.  {@code
     * mostSigBits} is used for the most significant 64 bits of the {@code
     * UUID} and {@code leastSigBits} becomes the least significant 64 bits of
     * the {@code UUID}.
     * 使用指定的数据构造新的 UUID
     *
     * @param mostSigBits  The most significant bits of the {@code UUID}
     * @param leastSigBits The least significant bits of the {@code UUID}
     */
    public UUID(long mostSigBits, long leastSigBits) {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
    }

    /**
     * 获取类型 4（伪随机生成的）UUID 的静态工厂。 使用加密的本地线程伪随机数生成器生成该 UUID。
     *
     * @return 随机生成的 {@code UUID}
     */
    public static UUID fastUUID() {
        return randomUUID(false);
    }

    /**
     * 获取类型 4（伪随机生成的）UUID 的静态工厂。 使用加密的强伪随机数生成器生成该 UUID。
     *
     * @return 随机生成的 {@code UUID}
     */
    public static UUID randomUUID() {
        return randomUUID(true);
    }

    /**
     * Static factory to retrieve a type 4 (pseudo randomly generated) UUID.
     * 获取类型 4（伪随机生成的）UUID 的静态工厂。
     * <p>
     * The {@code UUID} is generated using a cryptographically strong pseudo
     * random number generator.
     * 使用加密的强伪随机数生成器生成该 UUID。
     *
     * @param isSecure 是否使用{@link SecureRandom}。如果是可以获得更安全的随机码，否则可以得到更好的性能
     * @return A randomly generated {@code UUID}
     */
    public static UUID randomUUID(boolean isSecure) {
        // 此处区别于java.util.UUID
        final Random ng = isSecure ? Holder.numberGenerator : getRandom();

        byte[] randomBytes = new byte[16];
        ng.nextBytes(randomBytes);
        randomBytes[6] &= 0x0f;  /* clear version        */
        randomBytes[6] |= 0x40;  /* set to version 4     */
        randomBytes[8] &= 0x3f;  /* clear variant        */
        randomBytes[8] |= 0x80;  /* set to IETF variant  */
        return new UUID(randomBytes);
    }

    /**
     * Static factory to retrieve a type 3 (name based) {@code UUID} based on
     * the specified byte array.
     * 根据指定的字节数组获取类型 3（基于名称的）UUID 的静态工厂。
     *
     * @param name A byte array to be used to construct a {@code UUID}
     * @return A {@code UUID} generated from the specified array
     */
    public static UUID nameUUIDFromBytes(byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("MD5 not supported", nsae);
        }
        byte[] md5Bytes = md.digest(name);
        md5Bytes[6] &= 0x0f;  /* clear version        */
        md5Bytes[6] |= 0x30;  /* set to version 3     */
        md5Bytes[8] &= 0x3f;  /* clear variant        */
        md5Bytes[8] |= 0x80;  /* set to IETF variant  */
        return new UUID(md5Bytes);
    }

    /**
     * Creates a {@code UUID} from the string standard representation as
     * described in the {@link #toString} method.
     * 根据 {@link #toString()} 方法中描述的字符串标准表示形式创建{@code UUID}
     *
     * @param name A string that specifies a {@code UUID}
     * @return A {@code UUID} with the specified value
     * @throws IllegalArgumentException If name does not conform to the string representation as
     *                                  described in {@link #toString}
     */
    public static UUID fromString(String name) {
        String[] components = name.split("-");
        if (components.length != 5)
            throw new IllegalArgumentException("Invalid UUID string: " + name);
        for (int i = 0; i < 5; i++)
            components[i] = "0x" + components[i];

        long mostSigBits = Long.decode(components[0]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[1]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[2]);

        long leastSigBits = Long.decode(components[3]);
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(components[4]);

        return new UUID(mostSigBits, leastSigBits);
    }

    // Field Accessor Methods

    /**
     * Returns the least significant 64 bits of this UUID's 128 bit value.
     * 返回此 UUID 的 128 位值中的最低有效 64 位
     *
     * @return The least significant 64 bits of this UUID's 128 bit value
     */
    public long getLeastSignificantBits() {
        return leastSigBits;
    }

    /**
     * Returns the most significant 64 bits of this UUID's 128 bit value.
     * 返回此 UUID 的 128 位值中的最高有效 64 位
     *
     * @return The most significant 64 bits of this UUID's 128 bit value
     */
    public long getMostSignificantBits() {
        return mostSigBits;
    }

    /**
     * The version number associated with this {@code UUID}.  The version
     * number describes how this {@code UUID} was generated.
     * 与此 {@code UUID} 相关联的版本号. 版本号描述此 {@code UUID} 是如何生成的
     * The version number has the following meaning:
     * <ul>
     * <li>1    Time-based UUID(基于时间的 UUID)
     * <li>2    DCE security UUID(DCE 安全 UUID)
     * <li>3    Name-based UUID(基于名称的 UUID)
     * <li>4    Randomly generated UUID(随机生成的 UUID)
     * </ul>
     *
     * @return The version number of this {@code UUID}
     */
    public int version() {
        // Version is bits masked by 0x000000000000F000 in MS long
        return (int) ((mostSigBits >> 12) & 0x0f);
    }

    /**
     * The variant number associated with this {@code UUID}.  The variant
     * number describes the layout of the {@code UUID}.
     * 与此 {@code UUID} 相关联的变体号。变体号描述 {@code UUID} 的布局
     * The variant number has the following meaning:
     * <ul>
     * <li>0    Reserved for NCS backward compatibility(为 NCS 向后兼容保留)
     * <li>2    <a href="http://www.ietf.org/rfc/rfc4122.txt">IETF&nbsp;RFC&nbsp;4122</a>
     * (Leach-Salz), used by this class(<a href="http://www.ietf.org/rfc/rfc4122.txt">IETF&nbsp;RFC&nbsp;4122</a>(Leach-Salz), 用于此类)
     * <li>6    Reserved, Microsoft Corporation backward compatibility(保留，微软向后兼容)
     * <li>7    Reserved for future definition(保留供以后定义使用)
     * </ul>
     *
     * @return The variant number of this {@code UUID}
     */
    public int variant() {
        // This field is composed of a varying number of bits.
        // 0    -    -    Reserved for NCS backward compatibility
        // 1    0    -    The IETF aka Leach-Salz variant (used by this class)
        // 1    1    0    Reserved, Microsoft backward compatibility
        // 1    1    1    Reserved for future definition.
        return (int) ((leastSigBits >>> (64 - (leastSigBits >>> 62)))
                & (leastSigBits >> 63));
    }

    /**
     * The timestamp value associated with this UUID.
     * 与此 UUID 相关联的时间戳值
     * <p> The 60 bit timestamp value is constructed from the time_low,
     * time_mid, and time_hi fields of this {@code UUID}.  The resulting
     * timestamp is measured in 100-nanosecond units since midnight,
     * October 15, 1582 UTC.
     * 60 位的时间戳值根据此 {@code UUID} 的 time_low、time_mid 和 time_hi 字段构造。
     * 所得到的时间戳以 100 毫微秒为单位，从 UTC（通用协调时间） 1582 年 10 月 15 日零时开始。
     *
     * <p> The timestamp value is only meaningful in a time-based UUID, which
     * has version type 1.  If this {@code UUID} is not a time-based UUID then
     * this method throws UnsupportedOperationException.
     * 时间戳值仅在在基于时间的 UUID（其 version 类型为 1）中才有意义。
     * 如果此 {@code UUID} 不是基于时间的 UUID，则此方法抛出 UnsupportedOperationException。
     *
     * @return The timestamp of this {@code UUID}.
     * @throws UnsupportedOperationException If this UUID is not a version 1 UUID
     */
    public long timestamp() {
        if (version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }

        return (mostSigBits & 0x0FFFL) << 48
                | ((mostSigBits >> 16) & 0x0FFFFL) << 32
                | mostSigBits >>> 32;
    }

    /**
     * The clock sequence value associated with this UUID.
     * 与此 UUID 相关联的时钟序列值
     * <p> The 14 bit clock sequence value is constructed from the clock
     * sequence field of this UUID.  The clock sequence field is used to
     * guarantee temporal uniqueness in a time-based UUID.
     * 14 位的时钟序列值根据此 UUID 的 clock_seq 字段构造。clock_seq 字段用于保证在基于时间的 UUID 中的时间唯一性
     *
     * <p> The {@code clockSequence} value is only meaningful in a time-based
     * UUID, which has version type 1.  If this UUID is not a time-based UUID
     * then this method throws UnsupportedOperationException.
     * {@code clockSequence} 值仅在基于时间的 UUID（其 version 类型为 1）中才有意义。
     * 如果此 UUID 不是基于时间的 UUID，则此方法抛出UnsupportedOperationException
     *
     * @return The clock sequence of this {@code UUID}
     * @throws UnsupportedOperationException If this UUID is not a version 1 UUID
     */
    public int clockSequence() {
        if (version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }

        return (int) ((leastSigBits & 0x3FFF000000000000L) >>> 48);
    }

    /**
     * The node value associated with this UUID.
     * 与此 UUID 相关的节点值
     *
     * <p> The 48 bit node value is constructed from the node field of this
     * UUID.  This field is intended to hold the IEEE 802 address of the machine
     * that generated this UUID to guarantee spatial uniqueness.
     * 48 位的节点值根据此 UUID 的 node 字段构造。此字段旨在用于保存机器的 IEEE 802 地址，该地址用于生成此 UUID 以保证空间唯一性。
     *
     * <p> The node value is only meaningful in a time-based UUID, which has
     * version type 1.  If this UUID is not a time-based UUID then this method
     * throws UnsupportedOperationException.
     * 节点值仅在基于时间的 UUID（其 version 类型为 1）中才有意义。
     * 如果此 UUID 不是基于时间的 UUID，则此方法抛出 UnsupportedOperationException
     *
     * @return The node value of this {@code UUID}
     * @throws UnsupportedOperationException If this UUID is not a version 1 UUID
     */
    public long node() {
        if (version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }

        return leastSigBits & 0x0000FFFFFFFFFFFFL;
    }

    // Object Inherited Methods

    /**
     * Returns a {@code String} object representing this {@code UUID}.
     * 返回此{@code UUID} 的字符串表现形式
     *
     * <p> The UUID string representation is as described by this BNF:
     * UUID 的字符串表示形式由此 BNF 描述
     * <blockquote><pre>
     * {@code
     * UUID                   = <time_low> "-" <time_mid> "-"
     *                          <time_high_and_version> "-"
     *                          <variant_and_sequence> "-"
     *                          <node>
     * time_low               = 4*<hexOctet>
     * time_mid               = 2*<hexOctet>
     * time_high_and_version  = 2*<hexOctet>
     * variant_and_sequence   = 2*<hexOctet>
     * node                   = 6*<hexOctet>
     * hexOctet               = <hexDigit><hexDigit>
     * hexDigit               =
     *       "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
     *       | "a" | "b" | "c" | "d" | "e" | "f"
     *       | "A" | "B" | "C" | "D" | "E" | "F"
     * }</pre></blockquote>
     *
     * @param isSimple 是否简单模式，简单模式为不带'-'的UUID字符串
     * @return A string representation of this {@code UUID}
     */
    public String toString(boolean isSimple) {
        final StringBuilder builder = new StringBuilder(isSimple ? 32 : 36);
        // time_low
        builder.append(digits(mostSigBits >> 32, 8));
        if (!isSimple) {
            builder.append('-');
        }
        // time_mid
        builder.append(digits(mostSigBits >> 16, 4));
        if (!isSimple) {
            builder.append('-');
        }
        // time_high_and_version
        builder.append(digits(mostSigBits, 4));
        if (!isSimple) {
            builder.append('-');
        }
        // variant_and_sequence
        builder.append(digits(leastSigBits >> 48, 4));
        if (!isSimple) {
            builder.append('-');
        }
        // node
        builder.append(digits(leastSigBits, 12));

        return builder.toString();
    }

    /**
     * Returns val represented by the specified number of hex digits.
     * 返回指定数字对应的hex值
     */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

    /**
     * Returns a hash code for this {@code UUID}.
     *
     * @return A hash code value for this {@code UUID}
     */
    public int hashCode() {
        long hilo = mostSigBits ^ leastSigBits;
        return ((int) (hilo >> 32)) ^ (int) hilo;
    }

    /**
     * Compares this object to the specified object.  The result is {@code
     * true} if and only if the argument is not {@code null}, is a {@code UUID}
     * object, has the same variant, and contains the same value, bit for bit,
     * as this {@code UUID}.
     *
     * @param obj The object to be compared
     * @return {@code true} if the objects are the same; {@code false}
     * otherwise
     */
    public boolean equals(Object obj) {
        if ((null == obj) || (obj.getClass() != UUID.class))
            return false;
        UUID id = (UUID) obj;
        return (mostSigBits == id.mostSigBits && leastSigBits == id.leastSigBits);
    }

    // Comparison Operations

    /**
     * Compares this UUID with the specified UUID.
     *
     * <p> The first of two UUIDs is greater than the second if the most
     * significant field in which the UUIDs differ is greater for the first
     * UUID.
     * 如果两个 UUID 不同，且第一个 UUID 的最高有效字段大于第二个 UUID 的对应字段，则第一个 UUID 大于第二个 UUID
     *
     * @param val {@code UUID} to which this {@code UUID} is to be compared
     * @return -1, 0 or 1 as this {@code UUID} is less than, equal to, or
     * greater than {@code val}
     */
    public int compareTo(UUID val) {
        // The ordering is intentionally set up so that the UUIDs
        // can simply be numerically compared as two numbers
        return (this.mostSigBits < val.mostSigBits ? -1 :
                (this.mostSigBits > val.mostSigBits ? 1 :
                        (this.leastSigBits < val.leastSigBits ? -1 :
                                (this.leastSigBits > val.leastSigBits ? 1 :
                                        0))));
    }
}
