package cn.zm1001.util.common.sql;

import cn.zm1001.util.common.StringUtils;
import cn.zm1001.util.common.exception.SQLException;

/**
 * @Desc SQL注入检查
 * @Author Dongd_Zhou
 */
public class SqlUtil {

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * 检查字符，防止注入绕过
     */
    public static String checkSQL(String value) {
        if (StringUtils.isNotEmpty(value) && !isValidSql(value)) {
            throw new SQLException("参数不符合规范，不能进行查询");
        }
        return value;
    }

    /**
     * SQL是否符合规范
     */
    public static boolean isValidSql(String value) {
        return value.matches(SQL_PATTERN);
    }
}
