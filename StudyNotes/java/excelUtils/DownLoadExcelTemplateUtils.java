package com.inspur.crds.platform.util;

import com.inspur.crds.platform.financial.entity.FinancialreportSubject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @Description: DownLoadExcelTemplateUtils
 * @Author: lizz
 * @Date: 2019/6/6.
 */
public class DownLoadExcelTemplateUtils {

    public static void downloadExcel(String dataPath, String fileName, HttpServletResponse response) {
        //清空response
        response.reset();
        //方法一：直接下载路径下的文件模板
        try {
            //获取要下载的模板名称
            //设置要下载的文件的名称
            response.setHeader("Content-disposition", "attachment;fileName=" + fileName);
            //通知客服文件的MIME类型
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            //获取文件的路径
//            String filePath = "D:/template/" + fileName;
            String filePath = dataPath + fileName;
            FileInputStream input = new FileInputStream(new File(filePath));
            OutputStream out = response.getOutputStream();
            byte[] b = new byte[2048];
            int len;
            while ((len = input.read(b)) != -1) {
                out.write(b, 0, len);
            }
            //修正 Excel在“xxx.xlsx”中发现不可读取的内容。是否恢复此工作薄的内容？如果信任此工作簿的来源，请点击"是"
            response.setHeader("Content-Length", String.valueOf(input.getChannel().size()));
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void downloadEditExcel(String dataPath, String fileName, List<FinancialreportSubject> arrayList, HttpServletResponse response) {
        //清空response
        response.reset();
        //方法一：直接下载路径下的文件模板
        try {
            //获取要下载的模板名称
            //设置要下载的文件的名称
            response.setHeader("Content-disposition", "attachment;fileName=" + fileName);
            //通知客服文件的MIME类型
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            //获取文件的路径
//            String filePath = "D:/template/" + fileName;
            String filePath = dataPath + fileName;
            FileInputStream input = new FileInputStream(new File(filePath));
            //获取workbook对象
            Workbook workbook = new XSSFWorkbook(input);
            int rowNum = 10;
            int rowNum2 = 7;
            int rowNum3 = 7;
            int level = 0;
            List<FinancialreportSubject> bsList = new ArrayList<>();
            List<FinancialreportSubject> plList = new ArrayList<>();
            List<FinancialreportSubject> cfList = new ArrayList<>();
            List<FinancialreportSubject> noteList = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                if ("bs".equals(arrayList.get(i).getFinType())) {
                    bsList.add(arrayList.get(i));
                } else if ("pl".equals(arrayList.get(i).getFinType())) {
                    plList.add(arrayList.get(i));
                } else if ("cf".equals(arrayList.get(i).getFinType())) {
                    cfList.add(arrayList.get(i));
                } else {
                    noteList.add(arrayList.get(i));
                }
            }
            for (int sheetNum = 0; sheetNum < 4; sheetNum++) {
                if (sheetNum == 0) {
                    findChildren(bsList, sheetNum, level, rowNum, workbook);
                } else if (sheetNum == 1) {
                    findChildren(plList, sheetNum, level, rowNum, workbook);
                } else if (sheetNum == 2) {
                    findChildren(cfList, sheetNum, level, rowNum, workbook);
                } else {
                    findChildren(noteList, sheetNum, level, rowNum, workbook);
                }
            }
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            input.close();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


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


    public static void downloadReportDocx(String fileName, HttpServletResponse response, String basePath) {
//        //获取文件的路径
//        String filePath = basePath + fileName;
//
//        byte[] buffer = new byte[1024];
//        FileInputStream fis = null; //文件输入流
//        BufferedInputStream bis = null;
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        try {
//            fis = new FileInputStream(new File(filePath));
//            bis = new BufferedInputStream(fis);
//            int i = bis.read(buffer);
//            while (i != -1) {
//                outputStream.write(buffer);
//                i = bis.read(buffer);
//            }
//            byte[] data = outputStream.toByteArray();
//            response.reset();
//            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", Base64.getEncoder().encodeToString(fileName.getBytes("UTF-8"))));
//            response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));
//            response.setContentType("application/octet-stream; charset=UTF-8");
//            IoUtil.write(response.getOutputStream(), Boolean.TRUE, data);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            bis.close();
//            fis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //清空response
        response.reset();
        //方法一：直接下载路径下的文件模板
        try {
            //获取要下载的模板名称
            //设置要下载的文件的名称
            response.setHeader("Content-disposition", String.format("attachment; filename=%s", Base64.getEncoder().encodeToString(fileName.getBytes("UTF-8"))));
            //通知客服文件的MIME类型
            response.setContentType("application/octet-stream;charset=UTF-8");
            //获取文件的路径
            String filePath = basePath + fileName;
            FileInputStream input = new FileInputStream(new File(filePath));
            OutputStream out = response.getOutputStream();
            byte[] b = new byte[2048];
            int len;
            while ((len = input.read(b)) != -1) {
                out.write(b, 0, len);
            }
            //修正 Excel在“xxx.xlsx”中发现不可读取的内容。是否恢复此工作薄的内容？如果信任此工作簿的来源，请点击"是"
            response.setHeader("Content-Length", String.valueOf(input.getChannel().size()));
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 递归查编辑excel文件
     *
     * @param treeNodes
     * @return
     */
    public static int findChildren(List<FinancialreportSubject> treeNodes, int sheetNum, int level, int rowNum, Workbook workbook) {
        for (FinancialreportSubject it : treeNodes) {
            CellStyle cellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            Sheet sheet = workbook.getSheetAt(sheetNum);
            Row row = sheet.createRow(rowNum);
            Cell cell0 = row.createCell(0);
            Cell cell1 = row.createCell(1);
            cellStyle.setIndention((short) level);
            cell1.setCellStyle(cellStyle);
            cell0.setCellValue(it.getEnname());
            cell1.setCellValue(it.getCnname());
            rowNum++;
            if (it.getChildren().size() > 0) {
                font.setBoldweight((short) 700);
                cellStyle.setFont(font);
                cell1.setCellStyle(cellStyle);
                rowNum = findChildren(it.getChildren(), sheetNum, level + 1, rowNum, workbook);
            }

        }
        return rowNum;
    }
}
