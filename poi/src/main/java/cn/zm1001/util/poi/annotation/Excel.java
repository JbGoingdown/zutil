package cn.zm1001.util.poi.annotation;

import cn.zm1001.util.poi.handler.ExcelHandlerAdapter;

import java.lang.annotation.*;
import java.math.BigDecimal;

/**
 * @Desc 自定义Excel注解（需要实体提供无参构造器）
 * @Author Dongd_Zhou
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Excel {
    /** 导出时在excel中排序 */
    int sort() default Integer.MAX_VALUE;

    /** 导入导出类型（0：导出导入；1：仅导出；2：仅导入） */
    Type type() default Type.ALL;

    /** 标题 */
    String name() default "";

    /** 是否包含数据（false:仅导出模板） */
    boolean includeData() default true;

    /** 导出类型（0数字 1字符串） */
    ColumnType cellType() default ColumnType.STRING;

    /** 另一个类中的属性名称,支持多级获取,以小数点隔开 */
    String targetAttr() default "";

    /** 当值为空时,字段的默认值 */
    String defaultValue() default "";

    /** 文字后缀,如% 100 变成 100% */
    String suffix() default "";

    /** 自定义数据处理器 */
    Class<?> handler() default ExcelHandlerAdapter.class;

    /** 自定义数据处理器参数 */
    String[] args() default {};

    /** 分隔符，读取字符串组内容 */
    String separator() default ",";

    /** 日期格式, 如: yyyy-MM-dd */
    String dateFormat() default "";

    /** 读取内容转表达式 (如: 0=未知,1=男,2=女) */
    String readConverterExp() default "";

    /** BigDecimal 精度 默认:-1(默认不开启BigDecimal格式化) */
    int scale() default -1;

    /** BigDecimal 舍入规则 默认:BigDecimal.ROUND_HALF_EVEN */
    int roundingMode() default BigDecimal.ROUND_HALF_EVEN;

    /** 设置只能选择不能输入的列内容 */
    String[] combo() default {};

    /** 提示信息 */
    String prompt() default "";

    /** 导出字段对齐方式（0：默认；1：靠左；2：居中；3：靠右） */
    Align align() default Align.AUTO;

    /** 导出时在excel中每个列的高度 单位为字符 */
    short height() default 14;

    /** 导出时在excel中每个列的宽 单位为字符 */
    short width() default 16;

    /** 是否统计数据,在最后追加一行统计数据总和 */
    boolean isStatistics() default false;

    enum Align {
        AUTO(0), LEFT(1), CENTER(2), RIGHT(3);
        private final int value;

        Align(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    enum Type {
        ALL(0), EXPORT(1), IMPORT(2);
        private final int value;

        Type(int value) {
            this.value = value;
        }

        int value() {
            return this.value;
        }
    }

    enum ColumnType {
        NUMERIC(0), STRING(1), IMAGE(2);
        private final int value;

        ColumnType(int value) {
            this.value = value;
        }

        int value() {
            return this.value;
        }
    }
}
