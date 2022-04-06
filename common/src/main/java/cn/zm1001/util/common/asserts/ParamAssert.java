package cn.zm1001.util.common.asserts;

import cn.zm1001.util.common.StringUtils;
import cn.zm1001.util.common.exception.ParamAssertException;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @Desc 参数断言
 * @Author Dongd_Zhou
 */
public class ParamAssert {

    /**
     * 期望布尔值为真，为假时抛异常
     *
     * @param condition 待判断字布尔值
     * @param message   为假时，抛出的异常信息
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望布尔值为假，为真时抛异常
     *
     * @param condition 待判断字布尔值
     * @param message   为真时，抛出的异常信息
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望与指定值相等，不相等抛出异常
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param message  不相等时，抛出的异常信息
     */
    public static void isEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望与指定值相等，不相等抛出异常
     *
     * @param expected 期望值
     * @param actual   实际值
     */
    public static void isEquals(Object expected, Object actual) {
        isEquals(expected, actual, "actual value does not match expected value");
    }

    /**
     * 期望与指定值不相等，相等抛出异常
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param message  不匹配时，抛出的异常信息
     */
    public static void isNotEquals(Object expected, Object actual, String message) {
        if (Objects.equals(expected, actual)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望与指定值不相等，相等抛出异常
     *
     * @param expected 期望值
     * @param actual   实际值
     */
    public static void isNotEquals(Object expected, Object actual) {
        isNotEquals(expected, actual, "actual value does not match expected value");
    }

    /**
     * 期望对象为空，不为空抛异常
     *
     * @param condition 待判断对象
     * @param message   不为空时，抛出的异常信息
     */
    public static void isNull(Object condition, String message) {
        if (Objects.nonNull(condition)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望对象为空，不为空抛异常
     *
     * @param condition 待判断对象
     */
    public static void isNull(Object condition) {
        isNull(condition, condition + " expected is null");
    }

    /**
     * 期望对象不为空，为空抛异常
     *
     * @param condition 待判断对象
     * @param message   为空时，抛出的异常信息
     */
    public static void isNotNull(Object condition, String message) {
        if (Objects.isNull(condition)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望对象不为空，为空抛异常
     *
     * @param condition 待判断对象
     */
    public static void isNotNull(Object condition) {
        isNotNull(condition, condition + " expected is not null");
    }

    /**
     * 期望字符串为空，不为空抛异常
     *
     * @param text    待判断字符串
     * @param message 不为空时，抛出的异常信息
     */
    public static void isEmpty(String text, String message) {
        if (StringUtils.isNotEmpty(text)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望字符串为空，不为空抛异常
     *
     * @param text 待判断字符串
     */
    public static void isEmpty(String text) {
        isEmpty(text, text + " expected is empty");
    }

    /**
     * 期望字符串不为空，为空抛异常
     *
     * @param text    待判断字符串
     * @param message 为空时，抛出的异常信息
     */
    public static void isNotEmpty(String text, String message) {
        if (StringUtils.isEmpty(text)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望字符串不为空，为空抛异常
     *
     * @param text 待判断字符串
     */
    public static void isNotEmpty(String text) {
        isNotEmpty(text, text + " expected is not empty");
    }

    /**
     * 判断是否空数组
     *
     * @param array 待判断数组
     * @return 为空，返回true
     */
    private static boolean isEmptyArray(Object array) {
        if (null == array) {
            return true;
        }
        return array.getClass().isArray() && 0 == Array.getLength(array);
    }

    /**
     * 期望数组为空，不为空抛出异常
     * <p>非数组对象也会抛出异常</p>
     *
     * @param array   待判断数组
     * @param message 不为空时，抛出的异常信息
     */
    public static void isNoneArray(Object array, String message) {
        if (!isEmptyArray(array)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望数组为空，不为空抛出异常
     * <p>非数组对象也会抛出异常</p>
     *
     * @param array 待判断数组
     */
    public static void isNoneArray(Object array) {
        isNoneArray(array, array + " expected is none");
    }

    /**
     * 期望数组为不空，为空抛出异常
     * <p>非数组对象不会抛出异常</p>
     *
     * @param array   待判断数组
     * @param message 为空时，抛出的异常信息
     */
    public static void isNotNoneArray(Object array, String message) {
        if (isEmptyArray(array)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望数组为不空，为空抛出异常
     * <p>非数组对象不会抛出异常</p>
     *
     * @param array 待判断数组
     */
    public static void isNotNoneArray(Object array) {
        isNotNoneArray(array, array + " expected is not none");
    }

    /**
     * 判断是否空Map
     *
     * @param map 待判断数组
     * @return 为空，返回true
     */
    private static <K, V> boolean isEmptyMap(Map<K, V> map) {
        return null == map || map.isEmpty();
    }

    /**
     * 期望Map为空，不为空抛出异常
     *
     * @param map     待判断Map
     * @param message 不为空时，抛出的异常信息
     */
    public static <K, V> void isNoneMap(Map<K, V> map, String message) {
        if (!isEmptyMap(map)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望Map为空，不为空抛出异常
     *
     * @param map 待判断Map
     */
    public static <K, V> void isNoneMap(Map<K, V> map) {
        isNoneMap(map, map + " expected is none");
    }

    /**
     * 期望Map为不空，为空抛出异常
     *
     * @param map     待判断Map
     * @param message 为空时，抛出的异常信息
     */
    public static <K, V> void isNotNoneMap(Map<K, V> map, String message) {
        if (isEmptyMap(map)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望Map为不空，为空抛出异常
     *
     * @param map 待判断Map
     */
    public static <K, V> void isNotNoneMap(Map<K, V> map) {
        isNotNoneMap(map, map + " expected is not none");
    }

    private static <T> boolean isEmptySet(Set<T> set) {
        return null == set || 0 == set.size();
    }

    /**
     * 期望Set为空，不为空抛出异常
     *
     * @param set     待判断Set
     * @param message 不为空时，抛出的异常信息
     */
    public static <T> void isNoneSet(Set<T> set, String message) {
        if (!isEmptySet(set)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望Set为空，不为空抛出异常
     *
     * @param set 待判断Set
     */
    public static <T> void isNoneArray(Set<T> set) {
        isNoneSet(set, set + " expected is none");
    }

    /**
     * 期望Set为不空，为空抛出异常
     *
     * @param set     待判断数组
     * @param message 为空时，抛出的异常信息
     */
    public static <T> void isNotNoneSet(Set<T> set, String message) {
        if (isEmptySet(set)) {
            throw new ParamAssertException(message);
        }
    }

    /**
     * 期望Set为不空，为空抛出异常
     *
     * @param set 待判断Set
     */
    public static <T> void isNotNoneSet(Set<T> set) {
        isNotNoneSet(set, set + " expected is not none");
    }
}
