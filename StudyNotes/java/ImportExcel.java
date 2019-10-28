package com.inspur.crds.platform.util;

import com.inspur.crds.platform.financial.entity.FinancialBsInfo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Description: ImportExcel
 * @Author: lizz
 * @Date: 2019/6/5.
 */
public class ImportExcel {

    private static Calendar calendar = Calendar.getInstance();

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
    private static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy/MM/dd");

    //解析excel文件
    public static HashMap<String, ArrayList<String[]>> analysisFile(MultipartFile file) throws IOException {
        HashMap<String, ArrayList<String[]>> hashMap = new HashMap<>();
        //获取workbook对象
        Workbook workbook = null;
        String filename = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        //根据后缀名是否excel文件
        if (filename.endsWith("xls")) {
            //2003
            workbook = new HSSFWorkbook(inputStream);
        } else if (filename.endsWith("xlsx")) {
            //2007
            workbook = new XSSFWorkbook(inputStream);
        }

        //创建对象，把每一行作为一个String数组，所以数组存到集合中
        ArrayList<String[]> arrayList = new ArrayList<>();
        if (workbook != null) {
            //循环sheet,现在是单sheet
            for (int sheetNum = 0; sheetNum < 1; sheetNum++) {
                //获取第一个sheet
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    hashMap.put("文件sheet为空!", arrayList);
                    return hashMap;
                }
                //获取当前sheet开始行和结束行
                int firstRowNum = sheet.getFirstRowNum();
                int lastRowNum = sheet.getLastRowNum();
                //循环开始，除了前两行
                for (int rowNum = firstRowNum + 2; rowNum <= lastRowNum; rowNum++) {
                    //获取当前行
                    Row row = sheet.getRow(rowNum);
                    //获取当前行的开始列和结束列
                    Row r = sheet.getRow(2);
                    short firstCellNum = 0;
                    short lastCellNum = 5;

                    //获取总行数
//                    int lastCellNum2 = row.getPhysicalNumberOfCells();
                    String[] strings = new String[lastCellNum];
                    //循环当前行
                    for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        if (cell == null || "".equals(cell) || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
//                            hashMap.put("第" + (rowNum + 1) + "行,第" + (cellNum + 1) + "列为空", arrayList);
//                            return hashMap;
                            cell= row.createCell(cellNum);
                        }
                        String cellValue = "";
                        cellValue = getCellValue(cell) + "";
                        strings[cellNum] = cellValue;
                    }
                    arrayList.add(strings);

                }
            }
        }
        inputStream.close();
        hashMap.put("OK", arrayList);
        return hashMap;
    }


    //解析excel文件
    public static List<Map<String, List>> analysisReportFile(MultipartFile file, String companyCode) throws IOException {
//        HashMap<String, ArrayList<String[]>> hashMap = new HashMap<>();
        List<Map<String, List>> lists = new ArrayList<>();
        //获取workbook对象
        Workbook workbook = null;
        String filename = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        //根据后缀名是否excel文件
        if (filename.endsWith("xls")) {
            //2003
            workbook = new HSSFWorkbook(inputStream);
        } else if (filename.endsWith("xlsx")) {
            //2007
            workbook = new XSSFWorkbook(inputStream);
        }

        //创建对象，把每一行作为一个String数组，所以数组存到集合中
        ArrayList<String[]> arrayList = new ArrayList<>();
        if (workbook != null) {
            Map<String, List> map = new HashMap<>();
            //循环sheet
            for (int sheetNum = 0; sheetNum < 3; sheetNum++) {
                //获取第一个sheet
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheetNum == 0) {
                    List<FinancialBsInfo> bsInfoList = new ArrayList<>();
                    //获取当前sheet开始行和结束行
                    int firstRowNum = sheet.getFirstRowNum();
                    int lastRowNum = sheet.getLastRowNum();
                    Row r = sheet.getRow(2);
                    int startCell = r.getFirstCellNum();
                    int endCell = r.getLastCellNum();
                    for (int i = 2; i < endCell; i++) {
                        FinancialBsInfo financialBsInfo = new FinancialBsInfo();
                        for (int j = 2; j < lastRowNum; j++) {
                            financialBsInfo.setInfopubldate(LocalDateTime.now());
                            financialBsInfo.setCompanycode(companyCode);
                            Row row2 = sheet.getRow(2);
                            SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");
                            //2、调用formatter2.parse(),将"19570323"转化为date类型  输出为：Sat Mar 23 00:00:00 GMT+08:00 1957
                            Date  date = null;
                            try {
                                date = formatter2.parse(row2.getCell(i).getStringCellValue());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            financialBsInfo.setEnddate(date);
                            Row row3 = sheet.getRow(3);
                            if ("合并报表".equals(row3.getCell(i).getStringCellValue())) {
                                financialBsInfo.setIfmerged(1L);
                            }else {
                                financialBsInfo.setIfmerged(2L);
                            }
                            Row row4 = sheet.getRow(4);
                            if ("调整".equals(row4.getCell(i).getStringCellValue())) {
                                financialBsInfo.setIfmerged(1L);
                            }else {
                                financialBsInfo.setIfmerged(2L);
                            }
                            Row row = sheet.getRow(j + 3);
                            setValue(financialBsInfo, financialBsInfo.getClass(), row.getCell(0).getStringCellValue(), Long.class, row.getCell(i).getStringCellValue());
                        }

                        bsInfoList.add(financialBsInfo);
                    }
                    map.put("bs", bsInfoList);
                    lists.add(map);
                }

//                List<Map<String, String>> list = new ArrayList<>();
//                //循环开始，除了前两行
//                for (int rowNum = firstRowNum + 2; rowNum <= lastRowNum; rowNum++) {
//                    //获取当前行
//                    Row row = sheet.getRow(rowNum);
//                    //获取当前行的开始列和结束列
//                    short firstCellNum = row.getFirstCellNum();
//                    short lastCellNum = row.getLastCellNum();
//
//                    //获取总行数
//                    int lastCellNum2 = row.getPhysicalNumberOfCells();
//                    String[] strings = new String[lastCellNum2];
//                    //循环当前行
//                    for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
//                        Cell cell = row.getCell(cellNum);
//                        if (cell == null || "".equals(cell) || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
//                            hashMap.put("第" + (rowNum + 1) + "行,第" + (cellNum + 1) + "列为空", arrayList);
//                            return hashMap;
//                        }
//                        String cellValue = "";
//                        cellValue = getCellValue(cell) + "";
//                        strings[cellNum] = cellValue;
//                    }
//                    arrayList.add(strings);
//
//                }
            }
        }
        inputStream.close();
        return lists;
    }

    /**
     * 根据属性，拿到set方法，并把值set到对象中
     *
     * @param obj       对象
     * @param clazz     对象的class
     * @param fieldName 需要设置值得属性
     * @param typeClass
     * @param value
     */
    public static void setValue(Object obj, Class<?> clazz, String fieldName, Class<?> typeClass, Object value) {
        fieldName = removeLine(fieldName);
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            Method method = clazz.getDeclaredMethod(methodName, new Class[]{typeClass});
            method.invoke(obj, new Object[]{getClassTypeValue(typeClass, value)});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 通过class类型获取获取对应类型的值
     *
     * @param typeClass class类型
     * @param value     值
     * @return Object
     */
    private static Object getClassTypeValue(Class<?> typeClass, Object value) {
        if (typeClass == int.class || value instanceof Integer) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == short.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == byte.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == double.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == long.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == String.class) {
            if (null == value) {
                return "";
            }
            return value;
        } else if (typeClass == boolean.class) {
            if (null == value) {
                return true;
            }
            return value;
        } else if (typeClass == BigDecimal.class) {
            if (null == value) {
                return new BigDecimal(0);
            }
            return new BigDecimal(value + "");
        } else {
            return typeClass.cast(value);
        }
    }

    /**
     * 处理字符串  如：  abc_dex ---> abcDex
     *
     * @param str
     * @return
     */
    public static String removeLine(String str) {
        if (null != str && str.contains("_")) {
            int i = str.indexOf("_");
            char ch = str.charAt(i + 1);
            char newCh = (ch + "").substring(0, 1).toUpperCase().toCharArray()[0];
            String newStr = str.replace(str.charAt(i + 1), newCh);
            String newStr2 = newStr.replace("_", "");
            return newStr2;
        }
        return str;
    }

//    public Object getValue(Object dto, String name) throws Exception {
//        Method[] m = dto.getClass().getMethods();
//        for (int i = 0; i < m.length; i++) {
//            if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
//                return m[i].invoke(dto);
//            } else {
//                return null;
//            }
//        }
//    }


    //把每一个cell转换为string
    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        //把数字转换成string，防止12.0这种情况
        if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字0
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串1
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                //cellValue = String.valueOf(cell.getCellFormula());
                try {
                    //cellValue = String.valueOf(cell.getNumericCellValue());
                    cellValue = String.valueOf(cell.getStringCellValue());
                } catch (IllegalStateException e) {
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cellValue = String.valueOf(cell.getRichStringCellValue());
                }
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue.trim();
    }

    //判断row是否为空
    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    //检查文件类型
    public static Boolean checkFile(MultipartFile file) {
        //检查文件是否为空
        boolean empty = file.isEmpty();
        if (empty || file == null) {
            return false;
        }
        //检查文件是否是excel类型文件
        String filename = file.getOriginalFilename();
        if (!filename.endsWith("xls") && !filename.endsWith("xlsx")) {
            return false;
        }
        return true;
    }

    //转换excel导入之后时间变为数字,月时间
    public static String getCorrectMonth(int i) {
        calendar.set(1900, 0, 1);
        calendar.add(calendar.DATE, i);
        Date time = calendar.getTime();
        String s = simpleDateFormat.format(time);
        return s;
    }

    //转换excel导入之后时间变为数字,年月日时间
    public static String getCorrectDay(int i) {
        calendar.set(1900, 0, -1, 0, 0, 0);
        calendar.add(calendar.DATE, i);
        Date time = calendar.getTime();
        String s = simpleDateFormat3.format(time);
        return s;
    }

    //获取当前时间的字符串
    public static String getNowDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);
        return format;
    }


    //文件读取到指定的位置
    public String saveFile(MultipartFile file) throws IOException {
        MultipartFile update = file;
        //文件中参数名字
        String name = update.getName();
        //文件名字
        String originalFilename = update.getOriginalFilename();
        //是否为空
        boolean empty = update.isEmpty();
        //传输文件到指定路径中
        String path = "F://LDJS/boco/uploading/" + originalFilename;
        update.transferTo(new File(path));
        //文件类型
        String contentType = update.getContentType();
        InputStream inputStream = update.getInputStream();
        inputStream.close();
        //是否存在此路径
        boolean path1 = new File(path).exists();
        if (path1) {
            return "OK";
        } else {
            return "导入文件失败";
        }

    }

    //显示时间，把数字转换成时间类型的
    public static String getExcelDate(Cell cell) {
        Date dateCellValue = cell.getDateCellValue();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(dateCellValue);
        return format;
    }


    public static String getDetailDate(String date) {
        int dayNum = (int) Double.parseDouble(date);

        String s1 = "0." + date.split("\\.")[1];
        String hour = Double.parseDouble(s1) * 24 + "";
        int hourNum = Integer.parseInt(hour.split("\\.")[0]);

        String s2 = "0." + hour.split("\\.")[1];
        String minte = Double.parseDouble(s2) * 60 + "";
        int minteNum = Integer.parseInt(minte.split("\\.")[0]);

        String s3 = "0." + minte.split("\\.")[1];
        String second = Double.parseDouble(s3) * 60 + "";
        int secondNum = Integer.parseInt(second.split("\\.")[0]);
        calendar.set(1900, 0, -1, 0, 0, 0);
        calendar.add(calendar.DATE, dayNum);
        calendar.add(calendar.HOUR, hourNum);
        calendar.add(calendar.MINUTE, minteNum);
        calendar.add(calendar.SECOND, secondNum);
        Date time = calendar.getTime();
        String s = simpleDateFormat2.format(time);
        return s;
    }

    //检查是否是数字
    public static Boolean checkWhetherNumber(String str) {
        try {
            BigDecimal bigDecimal = new BigDecimal(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //检查是不是时间类型
    public static Boolean checkWhetherDate(String str) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            simpleDateFormat.setLenient(false);
            simpleDateFormat.parse(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //检查是不是时间类型
    public static Boolean checkWhetherDate2(String str) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            simpleDateFormat.setLenient(false);
            simpleDateFormat.parse(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //检查是不是月的时间类型
    public static Boolean checkWhetherMonth(String str) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
            simpleDateFormat.setLenient(false);
            simpleDateFormat.parse(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public static Workbook getWorkbook(MultipartFile file) {
        Workbook workbook = null;
        String filename = file.getOriginalFilename();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            //根据后缀名是否excel文件
            if (filename.endsWith("xls")) {
                //2003
                workbook = new HSSFWorkbook(inputStream);
            } else if (filename.endsWith("xlsx")) {
                //2007
                workbook = new XSSFWorkbook(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return workbook;
    }


}
