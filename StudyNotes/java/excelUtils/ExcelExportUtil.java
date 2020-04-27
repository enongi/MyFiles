package com.excelUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.inspur.crds.platform.common.entity.CommonSysDict;
import com.inspur.crds.platform.common.mapper.CommonSysDictMapper;
import com.inspur.crds.platform.querystatistics.entity.TableColumn;
import com.inspur.crds.platform.querystatistics.entity.TableOption;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.zip.ZipOutputStream;

public class ExcelExportUtil<T> {
    private CommonSysDictMapper commonSysDictMapper;

    private List<String> tranKey;
    private Map<String, String> tranDic;
    private Boolean isSelection = false;

    public void analyseOption(TableOption tableOption, List<String> extras, List<String> hidden) {
        List<TableColumn> datas = tableOption.getColumn();
        List<String> key = new ArrayList<>();
        List<String> title = new ArrayList<>();
//        List<TableColumn> children = new ArrayList<>();

        for (TableColumn data : datas) {
            List<TableColumn> children = data.getChildren();
            if (!data.isHide() && extras.indexOf(data.getProp()) != -1 && hidden.indexOf(data.getProp()) != -1) {
                if (children == null) {
                    key.add(data.getProp());
                    title.add(data.getLabel());
                    if (data.getDicUrl() != null) {
                        tranKey.add(data.getProp());
                        tranDic.put(data.getProp(), data.getDicUrl().split("/")[data.getDicUrl().split("/").length - 1]);
                    }
                } else {
                    for (TableColumn child : children) {
                        if (child.getChildren() == null) {
                            key.add(child.getProp());
                            title.add(data.getLabel() + "/" + child.getLabel());
                            if (data.getDicUrl() != null) {
                                tranKey.add(child.getProp());
                                tranDic.put(child.getProp(), child.getDicUrl().split("/")[child.getDicUrl().split("/").length - 1]);
                            } else {
                                for (TableColumn column : child.getChildren()) {
                                    key.add(column.getProp());
                                    title.add(data.getLabel() + "/" + child.getLabel() + "/" + column.getLabel());
                                    if (column.getDicUrl() != null) {
                                        tranKey.add(column.getProp());
                                        tranDic.put(column.getProp(), column.getDicUrl().split("/")[column.getDicUrl().split("/").length - 1]);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    public List translateDic() {
        List<List> dicList = new ArrayList();
        for (String key : tranKey) {
            String s = tranDic.get(key);
            List<CommonSysDict> type = commonSysDictMapper.selectList(new QueryWrapper<CommonSysDict>()
                    .select("value,label")
                    .eq("type", tranDic.get(s)));
            dicList.add(type);
        }
        return dicList;
    }


    /**
     * 生成Excel所有数据都放在一个sheet页里
     * @param @param filePath  Excel文件路径
     * @param @param handers   Excel列标题(数组)
     * @return void
     * @throws
     * @Title: createExcelTemplate
     * @Description: 生成Excel
     */
    public void createExcelDisk(String filePath, String[] headers, String fileName,
                                Collection<T> dataSet) {

        SXSSFWorkbook wb = new SXSSFWorkbook(300);//创建工作薄

        //表头样式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        //字体样式
        Font fontStyle = wb.createFont();
        fontStyle.setFontName("微软雅黑");
        fontStyle.setFontHeightInPoints((short) 12);
        fontStyle.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(fontStyle);

        //新建sheet

        Sheet sheet = wb.createSheet(fileName);

        //生成sheet1内容
        Row rowFirst = sheet.createRow(0);//第一个sheet的第1行为标题
        //写标题
        for (int i = 0; i < headers.length; i++) {
            Cell cell = rowFirst.createCell(i); //获取第一行的每个单元格
            sheet.setColumnWidth(i, 4000); //设置每列的列宽
            cell.setCellStyle(style); //加样式
            cell.setCellValue(headers[i]); //往单元格里写数据
        }

        try {
            // 遍历集合数据，产生数据行
            Iterator<T> it = dataSet.iterator();
            int index = 0;
            while (it.hasNext()) {
                index++;
                Row row = sheet.createRow(index);
                T t = (T) it.next();
                // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short i = 0; i < headers.length; i++) {
                    Cell cell = row.createCell(i);
                    Field field = fields[i];
                    String fieldName = field.getName();
                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});
                    // 判断值的类型后进行强制类型转换
                    String textValue = null;
                    // 其它数据类型都当作字符串简单处理
                    if (value != null && value != "") {
                        textValue = value.toString();
                    }
                    if (textValue != null) {
                        XSSFRichTextString richString = new XSSFRichTextString(textValue);
                        cell.setCellValue(richString);
                    }
                }
            }
            File f = new File(filePath + fileName + ".xlsx"); //写文件
            //不存在则新增
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            out.flush();
            wb.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成文件csv
     * 所有数据放到同一个csv文件
     * @param filePath
     * @param headers
     * @param fileName
     * @param dataSet
     */
    public void createCsvDisk(String filePath, String[] headers, String fileName,
                              Collection<T> dataSet) {

        try {
            File f = new File(filePath + fileName + ".csv"); //写文件
            //不存在则新增
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f, true);
            OutputStreamWriter writer = new OutputStreamWriter(out, "GBK");
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < headers.length; i++) {
                if (i == headers.length - 1) {
//                            writer.write(textValue + "\n");
                    str.append(headers[i] + "\n");
                } else {
//                            writer.write(textValue + ",");
                    str.append(headers[i] + ",");
                }
            }
//            writer.write(str.toString()); //写数据
            // 遍历集合数据，产生数据行
            Iterator<T> it = dataSet.iterator();
            int index = 0;
            while (it.hasNext()) {
                index++;
                T t = (T) it.next();
                // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short i = 0; i < headers.length; i++) {
                    Field field = fields[i];
                    String fieldName = field.getName();
                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});
                    // 判断值的类型后进行强制类型转换
                    String textValue = null;
                    // 其它数据类型都当作字符串简单处理
                    if (value != null && value != "") {
                        if (fieldName == "schemeId") {
                            textValue = value.toString() + "\t";
                        } else {
                            textValue = value.toString();
                        }
                    }
                    if (textValue != null) {
                        if (i == headers.length - 1) {
//                            writer.write(textValue + "\n");
                            str.append(textValue + "\n");
                        } else {
//                            writer.write(textValue + ",");
                            str.append(textValue + ",");
                        }
                    } else {
                        if (i == headers.length - 1) {
//                            writer.write(textValue + "\n");
                            str.append("-" + "\n");
                        } else {
//                            writer.write(textValue + ",");
                            str.append("-" + ",");
                        }
                    }
                }
            }
//            System.out.println(str.toString());
            writer.write(str.toString());
            writer.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成文件csv
     * 实体类list数据分批次根据放入同一个压缩包下的不同文件中
     *
     * @param headers
     * @param dataSet
     */
    public void createCsv(BufferedWriter writer, String[] headers,
                          Collection<T> dataSet) {

        try {
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < headers.length; i++) {
                if (i == headers.length - 1) {
                    str.append(headers[i] + "\n");
                } else {
                    str.append(headers[i] + ",");
                }
            }
            // 遍历集合数据，产生数据行
            Iterator<T> it = dataSet.iterator();
            int index = 0;
            while (it.hasNext()) {
                index++;
                T t = (T) it.next();
                // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short i = 0; i < headers.length; i++) {
                    Field field = fields[i];
                    String fieldName = field.getName();
                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});
                    // 判断值的类型后进行强制类型转换
                    String textValue = null;
                    // 其它数据类型都当作字符串简单处理
                    if (value != null && value != "") {
                        if (fieldName == "schemeId") {
                            textValue = value.toString() + "\t";
                        } else {
                            textValue = value.toString();
                        }
                    }
                    if (textValue != null) {
                        if (i == headers.length - 1) {
                            str.append(textValue + "\n");
                        } else {
                            str.append(textValue + ",");
                        }
                    } else {
                        if (i == headers.length - 1) {
                            str.append("-" + "\n");
                        } else {
                            str.append("-" + ",");
                        }
                    }
                }
            }
//            System.out.println(str.toString());
            writer.write(str.toString());
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
 /**
     * 生成文件csv 多线程
     * 实体类list数据分批次根据放入同一个压缩包下的不同文件中
     *
     * @param headers
     * @param dataSet
     */
    public void createCsvThread(BufferedWriter writer, String[] headers,
                          Collection<T> dataSet) {

        try {
            StringBuffer str = new StringBuffer();
//            for (int i = 0; i < headers.length; i++) {
//                if (i == headers.length - 1) {
//                    str.append(headers[i] + "\n");
//                } else {
//                    str.append(headers[i] + ",");
//                }
//            }
            // 遍历集合数据，产生数据行
            Iterator<T> it = dataSet.iterator();
            int index = 0;
            while (it.hasNext()) {
                index++;
                T t = (T) it.next();
                // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short i = 0; i < headers.length; i++) {
                    Field field = fields[i];
                    String fieldName = field.getName();
                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});
                    // 判断值的类型后进行强制类型转换
                    String textValue = null;
                    // 其它数据类型都当作字符串简单处理
                    if (value != null && value != "") {
                        if (fieldName == "schemeId") {
                            textValue = value.toString() + "\t";
                        } else {
                            textValue = value.toString();
                        }
                    }
                    if (textValue != null) {
                        if (i == headers.length - 1) {
                            str.append(textValue + "\n");
                        } else {
                            str.append(textValue + ",");
                        }
                    } else {
                        if (i == headers.length - 1) {
                            str.append("-" + "\n");
                        } else {
                            str.append("-" + ",");
                        }
                    }
                }
            }
//            System.out.println(str.toString());
            writer.write(str.toString());
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成文件csv
     * Map集合数据分批次放入同一个压缩包下的不同文件中
     * @param headers
     * @param dataSet
     */
    public void createCsvMap(BufferedWriter writer, String[] headers,
                             List<Map<String, Object>> dataSet) {

        try {
            StringBuffer strHead = new StringBuffer();
            for (int i = 0; i < headers.length; i++) {
                if (i == headers.length - 1) {
                    strHead.append(headers[i] + "\n");
                } else {
                    strHead.append(headers[i] + ",");
                }
            }
            writer.write(strHead.toString());
            // 遍历集合数据，产生数据行
            int index = 0;
            StringBuffer strBody = new StringBuffer();
            for (int n = 0; n < dataSet.size(); n++) {
                Map<String, Object> stringObjectMap = dataSet.get(n);
                String schemeId = stringObjectMap.get("SCHEME_ID").toString() + "\t,";
                strBody.append(schemeId);
                for (short i = 1; i < headers.length; i++) {
                    // 判断值的类型后进行强制类型转换
                    Object textValue = stringObjectMap.get("V" + i);
                    // 其它数据类型都当作字符串简单处理
                    if (textValue != null) {
                        if (i == headers.length - 1) {
                            strBody.append(textValue.toString() + "\n");
                        } else {
                            strBody.append(textValue.toString() + ",");
                        }
                    } else {
                        if (i == headers.length - 1) {
                            strBody.append("-" + "\n");
                        } else {
                            strBody.append("-" + ",");
                        }
                    }
                }
                if (n % 10000 == 0) {
                    writer.write(strBody.toString());
                    writer.flush();
                    strBody.setLength(0);
                } else {
                    if (n == dataSet.size() - 1) {
                        writer.write(strBody.toString());
                        writer.flush();
                        strBody.setLength(0);
                    }
                }
            }
//            for (Map<String, Object> stringObjectMap : dataSet) {
//                StringBuffer strBody = new StringBuffer();
//                String schemeId = stringObjectMap.get("SCHEME_ID").toString() + "\t,";
//                strBody.append(schemeId);
//                for (short i = 1; i < headers.length; i++) {
//                    // 判断值的类型后进行强制类型转换
//                    Object textValue = stringObjectMap.get("V" + i);
//                    // 其它数据类型都当作字符串简单处理
//                    if (textValue != null) {
//                        if (i == headers.length - 1) {
//                            strBody.append(textValue.toString() + "\n");
//                        } else {
//                            strBody.append(textValue.toString() + ",");
//                        }
//                    } else {
//                        if (i == headers.length - 1) {
//                            strBody.append("-" + "\n");
//                        } else {
//                            strBody.append("-" + ",");
//                        }
//                    }
//                }
//                writer.write(strBody.toString());
//            }
//            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成excel文件，分批次放入不同sheet页
     * @param workbook
     * @param sheetName
     * @param headers
     * @param dataSet
     * @throws IOException
     */

    public void createExcel(SXSSFWorkbook workbook, String sheetName, String[] headers,
                            Collection<T> dataSet) throws IOException {

//        FileOutputStream os = new FileOutputStream("D://export.xlsx");

//        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
//        workbook.setCompressTempFiles(true);

        CellStyle style = workbook.createCellStyle();
        // 设置样式
        //表头样式
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        //字体样式
        Font fontStyle = workbook.createFont();
        fontStyle.setFontName("宋体");
        fontStyle.setFontHeightInPoints((short) 12);
        fontStyle.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(fontStyle);

        try {

            //新建sheet
            Sheet sheet = workbook.createSheet(sheetName);
            //生成sheet1内容
            Row rowFirst = sheet.createRow(0);//第一个sheet的第1行为标题
            //写标题
            for (int i = 0; i < headers.length; i++) {
                Cell cell = rowFirst.createCell(i); //获取第一行的每个单元格
                sheet.setColumnWidth(i, 4000); //设置每列的列宽
                cell.setCellStyle(style); //加样式
                cell.setCellValue(headers[i]); //往单元格里写数据
            }
            // 遍历集合数据，产生数据行
            Iterator<T> it = dataSet.iterator();
            int index = 0;
            while (it.hasNext()) {
                index++;
                Row row = sheet.createRow(index);
                T t = (T) it.next();
                // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short i = 0; i < headers.length; i++) {
                    Cell cell = row.createCell(i);
                    Field field = fields[i];
                    String fieldName = field.getName();
                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});
                    // 判断值的类型后进行强制类型转换
                    String textValue = null;
                    // 其它数据类型都当作字符串简单处理
                    if (value != null && value != "") {
                        textValue = value.toString();
                    }
                    if (textValue != null) {
                        XSSFRichTextString richString = new XSSFRichTextString(textValue);
                        cell.setCellValue(richString);
                    }
                }
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
