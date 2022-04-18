package cn.zm1001.util.common;

import cn.zm1001.util.common.exception.DateException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Desc 时间工具类
 * @Author Dongd_Zhou
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static final String HH_MM = "HH:mm";
    public static final String HH_MM_COMPACT = "HHmm";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String HH_MM_SS_COMPACT = "HHmmss";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_COMPACT = "yyyyMM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_COMPACT = "yyyyMMdd";
    public static final String YYYY_MM_DD_PATH = "yyyy/MM/dd";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_COMPACT = "yyyyMMddHHmm";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_COMPACT = "yyyyMMddHHmmss";

    private static final String[] parsePatterns = {
            YYYY_MM_DD_HH_MM_SS_COMPACT, YYYY_MM_DD_HH_MM_COMPACT, YYYY_MM_DD_COMPACT, YYYY_MM_COMPACT,
            YYYY_MM_DD_HH_MM_SS, YYYY_MM_DD_HH_MM, YYYY_MM_DD, YYYY_MM,
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM/dd", "yyyy/MM",
            "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM.dd", "yyyy.MM"};

    private DateUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取当前Date型日期
     *
     * @return 当前日期 {@link Date}
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return 当前日期
     */
    public static String nowDate() {
        return nowFormat(YYYY_MM_DD);
    }

    /**
     * 获取当前日期, 默认格式为yyyyMMdd
     *
     * @return 当前日期
     */
    public static String nowDateCompact() {
        return nowFormat(YYYY_MM_DD_COMPACT);
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd HH:mm:ss
     *
     * @return 当前日期
     */
    public static String nowDateTime() {
        return nowFormat(YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取当前日期, 默认格式为yyyyMMddHHmmss
     *
     * @return 当前日期
     */
    public static String nowDateTimeCompact() {
        return nowFormat(YYYY_MM_DD_HH_MM_SS_COMPACT);
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     *
     * @return 当前日期
     */
    public static String datePath() {
        return nowFormat(YYYY_MM_DD_PATH);
    }

    /**
     * 获取当前日期
     *
     * @param format 指定日期格式
     * @return 当前日期
     */
    public static String nowFormat(final String format) {
        return format(new Date(), format);
    }

    /**
     * 格式化显示日期
     *
     * @param date   待转化日期
     * @param format 指定日期格式
     * @return 当前日期
     */
    public static String format(final Date date, final String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 日期字符串转为时间
     *
     * @param dateStr 待转化日期字符串
     * @param format  日期格式
     * @return 日期时间 {@link Date}
     */
    public static Date parse(final String dateStr, final String format) {
        try {
            return new SimpleDateFormat(format).parse(dateStr);
        } catch (ParseException e) {
            throw new DateException(e);
        }
    }

    /**
     * 日期型字符串转化为日期 格式
     *
     * @param obj 待转化日期对象，支持类型：
     *            "yyyyMMddHHmmss", "yyyyMMHHmm", "yyyyMMdd", "yyyyMM",
     *            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "yyyy-MM",
     *            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM/dd", "yyyy/MM",
     *            "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM.dd", "yyyy.MM"
     * @return 日期时间 {@link Date}
     */
    public static Date parse(final Object obj) {
        if (null == obj) {
            return null;
        }
        try {
            return parseDate(obj.toString(), parsePatterns);
        } catch (ParseException e) {
            throw new DateException(e);
        }
    }

    /**
     * 计算两个时间差
     *
     * @param fromDate 较小时间
     * @param toDate   较大时间
     * @return 间隔时间描述（{day}天{hour}小时{min}分钟）
     */
    public static String interval(final Date fromDate, final Date toDate) {
        // 获得两个时间的毫秒时间差异
        long diff = toDate.getTime() - fromDate.getTime();
        // 计算差多少天
        long day = diff / MILLIS_PER_DAY;
        // 计算差多少小时
        long hour = diff % MILLIS_PER_DAY / MILLIS_PER_HOUR;
        // 计算差多少分钟
        long min = diff % MILLIS_PER_DAY % MILLIS_PER_HOUR / MILLIS_PER_MINUTE;
        // 计算差多少秒
        // long sec = diff % MILLIS_PER_DAY % MILLIS_PER_HOUR % MILLIS_PER_MINUTE / MILLIS_PER_SECOND;

        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append("天");
        }
        if (hour > 0) {
            sb.append(day).append("小时");
        }
        if (min > 0) {
            sb.append(day).append("分钟");
        }
        return sb.toString();
    }
}
