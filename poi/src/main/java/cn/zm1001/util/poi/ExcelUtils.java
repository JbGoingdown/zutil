package cn.zm1001.util.poi;

import cn.zm1001.util.common.DateUtils;
import cn.zm1001.util.common.ObjectUtils;
import cn.zm1001.util.common.ReflectUtils;
import cn.zm1001.util.common.StringUtils;
import cn.zm1001.util.poi.annotation.Excel;
import cn.zm1001.util.poi.annotation.Excels;
import cn.zm1001.util.poi.handler.ExcelHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desc Excel处理工具类
 * @Author Dongd_Zhou
 */
@Slf4j
public class ExcelUtils<T> {
    /** Excel Response Content-Type */
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    /** 数字格式 */
    private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("######0.00");
    /** Excel sheet最大行数，默认65536 */
    private static final int sheetSize = 65536;

    /** 类对象 */
    public Class<T> clazz;

    /** 导出类型（EXPORT:导出数据；IMPORT：导入模板） */
    private Excel.Type type;

    /** 工作薄对象 */
    private Workbook wb;

    /** 工作表对象 */
    private Sheet sheet;

    /** 工作表名称 */
    private String sheetName;

    /** 标题 */
    private String title;

    /** 样式列表 */
    private Map<String, CellStyle> styles;

    /** 导入导出数据列表 */
    private List<T> list;

    /** 注解列表 */
    private List<Object[]> fields;

    /** 当前行号 */
    private int rowNum;

    /** 最大高度 */
    private short maxHeight;

    /** 统计列表 */
    private final Map<Integer, Double> statistics = new HashMap<>();

    public ExcelUtils(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 对Excel表单默认第一行开始转换成对象(第0行为标题)
     *
     * @param is 输入流
     * @return 结果集
     */
    public List<T> importExcel(InputStream is) throws Exception {
        return importExcel(is, 0);
    }

    /**
     * 对Excel表单从第(titleNum + 1)行开始转换成List
     *
     * @param is       输入流
     * @param titleNum 标题占用的行数
     * @return 结果集
     */
    public List<T> importExcel(InputStream is, int titleNum) throws Exception {
        return importExcel(is, StringUtils.EMPTY, titleNum);
    }

    /**
     * 对Excel表单从第titleNum行开始转换成List
     *
     * @param is        输入流
     * @param sheetName 表格索引名
     * @param titleNum  标题占用的行数
     * @return 结果集
     */
    public List<T> importExcel(InputStream is, String sheetName, int titleNum) throws Exception {
        this.type = Excel.Type.IMPORT;
        this.wb = WorkbookFactory.create(is);
        // 若指定sheet名,则取指定sheet中的内容 否则默认指向第1个sheet
        Sheet sheet = StringUtils.isNotEmpty(sheetName) ? wb.getSheet(sheetName) : wb.getSheetAt(0);
        if (null == sheet) {
            throw new IOException("文件sheet不存在");
        }
        List<T> list = new ArrayList<>();
        // 获取最后一个非空行的行下标，比如总行数为n，则返回的为n-1
        int rows = sheet.getLastRowNum();
        if (rows <= 0) {
            // 表单为空
            return list;
        }

        // 存放Excel标题和列的序号.
        Map<String, Integer> cellMap = new HashMap<>();
        // 获取表头（行索引从0开始，第titleNum行为表头）
        Row heard = sheet.getRow(titleNum);
        for (int i = 0, cells = heard.getPhysicalNumberOfCells(); i < cells; i++) {
            Cell cell = heard.getCell(i);
            if (null != cell) {
                String title = getCellValue(heard, i).toString();
                if (StringUtils.isNotEmpty(title)) {
                    cellMap.put(title, i);
                }
            }
        }
        // 有数据时才处理 得到类的所有属性.
        // Object[0]=field(字段属性),Object[1]=Excel(注解)
        List<Object[]> fields = getFields();
        // 列序号对应的属性信息及注解信息
        Map<Integer, Object[]> fieldsMap = new HashMap<>();
        for (Object[] objects : fields) {
            Excel attr = (Excel) objects[1];
            // Excel中对应的列序号
            Integer column = cellMap.get(attr.name());
            if (null != column) {
                fieldsMap.put(column, objects);
            }
        }
        for (int i = titleNum + 1; i <= rows; i++) {
            // 从第2行开始取数据,默认第一行是表头.
            Row row = sheet.getRow(i);
            // 判断当前行是否是空行
            if (isRowEmpty(row)) {
                continue;
            }
            T entity = null;
            for (Map.Entry<Integer, Object[]> entry : fieldsMap.entrySet()) {
                Object val = getCellValue(row, entry.getKey());

                // 如果不存在实例则新建
                entity = (null == entity ? clazz.newInstance() : entity);
                // 从map中得到对应列的field
                Field field = (Field) entry.getValue()[0];
                Excel attr = (Excel) entry.getValue()[1];
                // 取得类型,并根据对象类型设置值
                Class<?> fieldType = field.getType();
                if (String.class == fieldType) {
                    String s = ObjectUtils.toStr(val);
                    if (StringUtils.endsWith(s, ".0")) {
                        val = StringUtils.substringBefore(s, ".0");
                    } else {
                        String dateFormat = field.getAnnotation(Excel.class).dateFormat();
                        if (StringUtils.isNotEmpty(dateFormat)) {
                            val = DateUtils.format((Date) val, dateFormat);
                        } else {
                            val = ObjectUtils.toStr(val);
                        }
                    }
                } else if ((Integer.TYPE == fieldType || Integer.class == fieldType) && StringUtils.isNumeric(ObjectUtils.toStr(val))) {
                    val = ObjectUtils.toInt(val);
                } else if (Long.TYPE == fieldType || Long.class == fieldType) {
                    val = ObjectUtils.toLong(val);
                } else if (Double.TYPE == fieldType || Double.class == fieldType) {
                    val = ObjectUtils.toDouble(val);
                } else if (Float.TYPE == fieldType || Float.class == fieldType) {
                    val = ObjectUtils.toFloat(val);
                } else if (BigDecimal.class == fieldType) {
                    val = ObjectUtils.toBigDecimal(val);
                } else if (Date.class == fieldType) {
                    if (val instanceof String) {
                        val = DateUtils.parse(val);
                    } else if (val instanceof Double) {
                        val = DateUtil.getJavaDate((Double) val);
                    }
                } else if (Boolean.TYPE == fieldType || Boolean.class == fieldType) {
                    val = ObjectUtils.toBool(val, false);
                }
                String propertyName = field.getName();
                if (StringUtils.isNotEmpty(attr.targetAttr())) {
                    propertyName = field.getName() + "." + attr.targetAttr();
                } else if (StringUtils.isNotEmpty(attr.readConverterExp())) {
                    val = reverseByExp(ObjectUtils.toStr(val), attr.readConverterExp(), attr.separator());
                } else if (!attr.handler().equals(ExcelHandlerAdapter.class)) {
                    val = dataFormatHandlerAdapter(val, attr);
                }
                ReflectUtils.invokeSetter(entity, propertyName, val);
            }
            list.add(entity);
        }
        return list;
    }

    /**
     * 获取单元格值
     *
     * @param row    获取的行
     * @param column 获取单元格列号
     * @return 单元格值
     */
    private Object getCellValue(Row row, int column) {
        Object val = StringUtils.EMPTY;
        if (row == null) {
            return val;
        }

        try {
            Cell cell = row.getCell(column);
            if (null != cell) {
                if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                    val = cell.getNumericCellValue();
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // POI Excel 日期格式转换
                        val = DateUtil.getJavaDate((Double) val);
                    } else {
                        if ((Double) val % 1 != 0) {
                            val = new BigDecimal(val.toString());
                        } else {
                            val = new DecimalFormat("0").format(val);
                        }
                    }
                } else if (cell.getCellType() == CellType.STRING) {
                    val = cell.getStringCellValue();
                } else if (cell.getCellType() == CellType.BOOLEAN) {
                    val = cell.getBooleanCellValue();
                } else if (cell.getCellType() == CellType.ERROR) {
                    val = cell.getErrorCellValue();
                }
            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }

    /**
     * 获取字段注解信息
     * Object[0]=field(字段属性),Object[1]=Excel(注解)
     */
    private List<Object[]> getFields() {
        List<Object[]> fields = new ArrayList<>();
        List<Field> tempFields = new ArrayList<>();
        tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        for (Field field : tempFields) {
            // 单注解
            if (field.isAnnotationPresent(Excel.class)) {
                Excel attr = field.getAnnotation(Excel.class);
                if (null != attr && (attr.type() == Excel.Type.ALL || attr.type() == type)) {
                    field.setAccessible(true);
                    fields.add(new Object[]{field, attr});
                }
            }

            // 多注解
            if (field.isAnnotationPresent(Excels.class)) {
                Excels attrs = field.getAnnotation(Excels.class);
                Excel[] excels = attrs.value();
                for (Excel attr : excels) {
                    if (null != attr && (attr.type() == Excel.Type.ALL || attr.type() == type)) {
                        field.setAccessible(true);
                        fields.add(new Object[]{field, attr});
                    }
                }
            }
        }
        return fields;
    }

    /**
     * 判断是否是空行
     *
     * @param row 判断的行
     * @return 是否空行
     */
    private boolean isRowEmpty(Row row) {
        if (null == row) {
            return true;
        }
        for (int index = row.getFirstCellNum(), last = row.getLastCellNum(); index < last; index++) {
            Cell cell = row.getCell(index);
            if (null != cell && CellType.BLANK != cell.getCellType()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 反向解析值 未知=0,男=1,女=2
     *
     * @param propertyValue 参数值
     * @param converterExp  翻译注解
     * @param separator     分隔符
     * @return 解析后值
     */
    private static String reverseByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StringUtils.containsAny(separator, propertyValue)) {
                for (String value : propertyValue.split(separator)) {
                    if (itemArray[1].equals(value)) {
                        propertyString.append(itemArray[0]).append(separator);
                        break;
                    }
                }
            } else {
                if (itemArray[1].equals(propertyValue)) {
                    return itemArray[0];
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 数据处理器
     *
     * @param value 数据值
     * @param excel 数据注解
     * @return 日期格式化
     */
    private String dataFormatHandlerAdapter(Object value, Excel excel) {
        try {
            Object instance = excel.handler().newInstance();
            Method formatMethod = excel.handler().getMethod("format", Object.class, String[].class);
            value = formatMethod.invoke(instance, value, excel.args());
        } catch (Exception e) {
            log.error("不能格式化数据 " + excel.handler(), e);
        }
        return ObjectUtils.toStr(value);
    }

    /**
     * 对list数据源将其里面的数据导出到Excel表单
     *
     * @param response  返回数据
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @throws IOException IO异常
     */
    public void exportExcel(HttpServletResponse response, List<T> list, String sheetName) throws IOException {
        exportExcel(response, list, sheetName, StringUtils.EMPTY);
    }

    /**
     * 对list数据源将其里面的数据导出到excel表单
     *
     * @param response  返回数据
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @param title     标题
     * @throws IOException IO异常
     */
    public void exportExcel(HttpServletResponse response, List<T> list, String sheetName, String title) throws IOException {
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        this.init(list, sheetName, title, Excel.Type.EXPORT);
        exportExcel(response.getOutputStream());
    }

    /**
     * 导出数据模板到Excel表单
     *
     * @param sheetName 工作表的名称
     * @return 结果
     */
    public void exportTemplateExcel(HttpServletResponse response, String sheetName) throws IOException {
        exportTemplateExcel(response, sheetName, StringUtils.EMPTY);
    }

    /**
     * 导出数据模板到Excel表单
     *
     * @param sheetName 工作表的名称
     * @param title     标题
     * @return 结果
     */
    public void exportTemplateExcel(HttpServletResponse response, String sheetName, String title) throws IOException {
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 导出数据需要的模板
        this.init(null, sheetName, title, Excel.Type.IMPORT);
        exportExcel(response.getOutputStream());
    }

    private void init(List<T> list, String sheetName, String title, Excel.Type type) {
        this.type = type;
        this.sheetName = sheetName;
        this.title = title;
        this.list = null == list ? new ArrayList<>() : list;
        createWorkbook();
        createTitle();
        createExcelField();
    }

    /**
     * 创建一个工作簿
     */
    private void createWorkbook() {
        this.wb = new SXSSFWorkbook(500);
        this.sheet = wb.createSheet();
        wb.setSheetName(0, sheetName);
        this.styles = createStyles(wb);
    }

    /**
     * 创建表格样式
     *
     * @param wb 工作薄对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        // 写入各条记录,每条记录对应excel表中的一行
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        style.setFont(titleFont);
        styles.put("title", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font totalFont = wb.createFont();
        totalFont.setFontName("Arial");
        totalFont.setFontHeightInPoints((short) 10);
        style.setFont(totalFont);
        styles.put("total", style);

        // 1：居左
        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.LEFT);
        styles.put("data1", style);

        // 2：居中
        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        styles.put("data2", style);

        // 3：居右
        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        styles.put("data3", style);

        return styles;
    }

    /**
     * 创建excel第一行标题(表头上方部分)
     */
    private void createTitle() {
        if (StringUtils.isNotEmpty(title)) {
            Row titleRow = sheet.createRow(rowNum == 0 ? rowNum++ : 0);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(styles.get("title"));
            titleCell.setCellValue(title);
            sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), titleRow.getRowNum(),
                    this.fields.size() - 1));
        }
    }

    /**
     * 得到所有定义字段
     */
    private void createExcelField() {
        this.fields = getFields();
        this.fields = this.fields.stream().sorted(Comparator.comparing(objects -> ((Excel) objects[1]).sort())).collect(Collectors.toList());
        this.maxHeight = getRowHeight();
    }

    /**
     * 根据注解获取最大行高
     */
    private short getRowHeight() {
        short maxHeight = 0;
        for (Object[] os : this.fields) {
            Excel excel = (Excel) os[1];
            maxHeight = maxHeight > excel.height() ? maxHeight : excel.height();
        }
        return (short) (maxHeight * 20);
    }

    /**
     * 写入数据到Excel表单，并写入到响应流
     */
    private void exportExcel(OutputStream out) {
        try {
            writeSheet();
            wb.write(out);
        } catch (Exception e) {
            log.error("导出Excel异常{}", e.getMessage());
        } finally {
            IOUtils.closeQuietly(wb);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 创建写入数据到Sheet
     */
    private void writeSheet() {
        // 取出一共有多少个sheet.
        int sheetNo = Math.max(1, (int) Math.ceil(list.size() * 1.0 / sheetSize));
        for (int index = 0; index < sheetNo; index++) {
            createSheet(sheetNo, index);
            // 产生一行
            Row row = sheet.createRow(rowNum);
            int column = 0;
            // 写入各个字段的列头名称
            for (Object[] os : fields) {
                Excel excel = (Excel) os[1];
                this.createTitle(excel, row, column++);
            }
            if (Excel.Type.EXPORT.equals(type)) {
                fillExcelData(index);
                addStatisticsRow();
            }
        }
    }

    /**
     * 创建工作表
     *
     * @param sheetNo sheet数量
     * @param index   序号
     */
    private void createSheet(int sheetNo, int index) {
        // 设置工作表的名称.
        if (sheetNo > 1 && index > 0) {
            this.sheet = wb.createSheet();
            this.createTitle();
            wb.setSheetName(index, sheetName + index);
        }
    }

    /**
     * 创建单元格
     */
    private void createTitle(Excel attr, Row row, int column) {
        // 创建列
        Cell cell = row.createCell(column);
        // 写入列信息
        cell.setCellValue(attr.name());
        setDataValidation(attr, column);
        cell.setCellStyle(styles.get("header"));
    }

    /**
     * 创建表格样式
     */
    private void setDataValidation(Excel attr, int column) {
        if (attr.name().contains("注：")) {
            sheet.setColumnWidth(column, 6000);
        } else {
            // 设置列宽
            sheet.setColumnWidth(column, (int) ((attr.width() + 0.72) * 256));
        }
        // 如果设置了提示信息则鼠标放上去提示.
        if (StringUtils.isNotEmpty(attr.prompt())) {
            // 这里默认设了2-101列提示.
            setXSSFPrompt(sheet, "", attr.prompt(), 1, 100, column, column);
        }
        // 如果设置了combo属性则本列只能选择不能输入
        if (attr.combo().length > 0) {
            // 这里默认设了2-101列只能选择不能输入.
            setXSSFValidation(sheet, attr.combo(), 1, 100, column, column);
        }
    }

    /**
     * 设置 POI XSSFSheet 单元格提示
     *
     * @param sheet         表单
     * @param promptTitle   提示标题
     * @param promptContent 提示内容
     * @param firstRow      开始行
     * @param endRow        结束行
     * @param firstCol      开始列
     * @param endCol        结束列
     */
    private void setXSSFPrompt(Sheet sheet, String promptTitle, String promptContent, int firstRow, int endRow,
                               int firstCol, int endCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createCustomConstraint("DD1");
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.createPromptBox(promptTitle, promptContent);
        dataValidation.setShowPromptBox(true);
        sheet.addValidationData(dataValidation);
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet    要设置的sheet.
     * @param textList 下拉框显示的内容
     * @param firstRow 开始行
     * @param endRow   结束行
     * @param firstCol 开始列
     * @param endCol   结束列
     * @return 设置好的sheet.
     */
    private void setXSSFValidation(Sheet sheet, String[] textList, int firstRow, int endRow, int firstCol, int endCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 加载下拉列表内容
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textList);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }

        sheet.addValidationData(dataValidation);
    }

    /**
     * 填充excel数据
     *
     * @param index 序号
     */
    private void fillExcelData(int index) {
        int startNo = index * sheetSize;
        int endNo = Math.min(startNo + sheetSize, list.size());
        for (int i = startNo; i < endNo; i++) {
            Row row = sheet.createRow(i + 1 + rowNum - startNo);
            // 得到导出对象.
            T vo = list.get(i);
            int column = 0;
            for (Object[] os : fields) {
                Field field = (Field) os[0];
                Excel excel = (Excel) os[1];
                this.addCell(excel, row, vo, field, column++);
            }
        }
    }

    /**
     * 添加单元格
     */
    private void addCell(Excel attr, Row row, T vo, Field field, int column) {
        Cell cell;
        try {
            // 设置行高
            row.setHeight(maxHeight);
            // 根据Excel中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
            if (attr.includeData()) {
                // 创建cell
                cell = row.createCell(column);
                int align = attr.align().value();
                cell.setCellStyle(styles.get("data" + (align >= 1 && align <= 3 ? align : "")));

                // 用于读取对象中的属性
                Object value = getTargetValue(vo, field, attr);
                String dateFormat = attr.dateFormat();
                String readConverterExp = attr.readConverterExp();
                String separator = attr.separator();
                if (StringUtils.isNotEmpty(dateFormat) && Objects.nonNull(value)) {
                    cell.setCellValue(DateUtils.format((Date) value, dateFormat));
                } else if (StringUtils.isNotEmpty(readConverterExp) && Objects.nonNull(value)) {
                    cell.setCellValue(convertByExp(ObjectUtils.toStr(value), readConverterExp, separator));
                } else if (value instanceof BigDecimal && -1 != attr.scale()) {
                    cell.setCellValue((((BigDecimal) value).setScale(attr.scale(), attr.roundingMode())).toString());
                } else if (!attr.handler().equals(ExcelHandlerAdapter.class)) {
                    cell.setCellValue(dataFormatHandlerAdapter(value, attr));
                } else {
                    // 设置列类型
                    setCellVo(value, attr, cell);
                }
                addStatisticsData(column, ObjectUtils.toStr(value), attr);
            }
        } catch (Exception e) {
            log.error("导出Excel失败", e);
        }
    }

    /**
     * 获取bean中的属性值
     *
     * @param vo    实体对象
     * @param field 字段
     * @param excel 注解
     * @return 最终的属性值
     * @throws Exception 异常
     */
    private Object getTargetValue(T vo, Field field, Excel excel) throws Exception {
        Object o = field.get(vo);
        if (StringUtils.isNotEmpty(excel.targetAttr())) {
            String target = excel.targetAttr();
            if (target.contains(".")) {
                String[] targets = target.split("[.]");
                for (String name : targets) {
                    o = getValue(o, name);
                }
            } else {
                o = getValue(o, target);
            }
        }
        return o;
    }

    /**
     * 以类的属性的get方法方法形式获取值
     */
    private Object getValue(Object o, String name) throws Exception {
        if (Objects.nonNull(o) && StringUtils.isNotEmpty(name)) {
            Class<?> clazz = o.getClass();
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            o = field.get(o);
        }
        return o;
    }

    /**
     * 解析导出值 0=未知,1=男,2=女
     *
     * @param propertyValue 参数值
     * @param converterExp  翻译注解
     * @param separator     分隔符
     * @return 解析后值
     */
    private static String convertByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StringUtils.containsAny(separator, propertyValue)) {
                for (String value : propertyValue.split(separator)) {
                    if (itemArray[0].equals(value)) {
                        propertyString.append(itemArray[1]).append(separator);
                        break;
                    }
                }
            } else {
                if (itemArray[0].equals(propertyValue)) {
                    return itemArray[1];
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 设置单元格信息
     *
     * @param value 单元格值
     * @param attr  注解相关
     * @param cell  单元格信息
     */
    private void setCellVo(Object value, Excel attr, Cell cell) {
        if (Excel.ColumnType.STRING == attr.cellType()) {
            cell.setCellValue(null == value ? attr.defaultValue() : value + attr.suffix());
        } else if (Excel.ColumnType.NUMERIC == attr.cellType()) {
            if (Objects.nonNull(value)) {
                cell.setCellValue(StringUtils.contains(ObjectUtils.toStr(value), ".") ? ObjectUtils.toDouble(value) : ObjectUtils.toInt(value));
            }
        }
    }

    /**
     * 合计统计信息
     */
    private void addStatisticsData(Integer index, String text, Excel entity) {
        if (null != entity && entity.isStatistics()) {
            Double temp = 0D;
            if (!statistics.containsKey(index)) {
                statistics.put(index, temp);
            }
            try {
                temp = Double.valueOf(text);
            } catch (NumberFormatException ignored) {
            }
            statistics.put(index, statistics.get(index) + temp);
        }
    }

    /**
     * 创建统计行
     */
    private void addStatisticsRow() {
        if (statistics.size() > 0) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            Set<Integer> keys = statistics.keySet();
            Cell cell = row.createCell(0);
            cell.setCellStyle(styles.get("total"));
            cell.setCellValue("合计");

            for (Integer key : keys) {
                cell = row.createCell(key);
                cell.setCellStyle(styles.get("total"));
                cell.setCellValue(DOUBLE_FORMAT.format(statistics.get(key)));
            }
            statistics.clear();
        }
    }

}
