package cn.billionsfinance.businessaccount.utils;

import cn.billionsfinance.businessaccount.core.bean.BeanExcelExport;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Excel 相关操作类(大数据量写入但受Excel数据行数限制)
 * 先写入Excel标题(writeExcelTitle)，再写入数据(writeExcelData)，最后释放资源(dispose)
 */
public class ExportExcel2007 {

    public static int THREAD_NUMBER = 50;
    // 声明一个容量为10的缓存队列
    BlockingQueue<ArrayList<BeanExcelExport>> queue = null;

    //总数
    private Long countAll = 0l;
    //剩余未处理
    private Long countOver = 0l;



    //默认列宽度
    public static int DEFAULT_COLUMN_SIZE = 15;

    public static Long SHEET_SIZE = 1000l;

    //刷新写入硬盘数据阀值
    private final int flushRows = 100;

    public static int SCHEMA = 1;

    public ExportExcel2007() {
        this.queue = new LinkedBlockingQueue<ArrayList<BeanExcelExport>>(THREAD_NUMBER);
    }


    //刷新写入硬盘数据阀值

    /**
     * 断言Excel文件写入之前的条件
     *
     * @param directory 目录
     * @param fileName  文件名
     * @return file
     * @throws IOException
     */
    private File assertFile(String directory, String fileName) throws IOException {
        File tmpFile = new File(directory + File.separator + fileName + ".xlsx");
        if (tmpFile.exists()) {
            if (tmpFile.isDirectory()) {
                throw new IOException("File '" + tmpFile + "' exists but is a directory");
            }
            if (!tmpFile.canWrite()) {
                throw new IOException("File '" + tmpFile + "' cannot be written to");
            }
        } else {
            File parent = tmpFile.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return tmpFile;
    }



    /**
     * Excel 导出，POI实现，先写入Excel标题，与writeExcelData配合使用
     * 先使用writeExcelTitle再使用writeExcelData
     *
     * @param directory   目录
     * @param fileName    文件名
     * @param columnNames 列名集合
     */
    public void writeExcelToFile(String directory, String fileName,Long count, String sheetName, List<String> columnNames,ResultSet rs) throws IOException {


        Long size = SHEET_SIZE;

        exportExcel(count,size,directory, fileName, sheetName,columnNames, rs);



    }





    /**
     * 导出字符串数据
     *
     * @param columnNames 表头
     */
    private void exportExcel(Long count,Long size,String directory, String fileName, String sheetName, List<String> columnNames,ResultSet rs)  {

        this.countAll = count;
        this.countOver = count;

        //根据多个sheet拆分
        if(SCHEMA == 1){
            List<String> sheets = new ArrayList<String>();

            if(count > size){
                Long temp1 = count / size;
                Long temp2 = count % size;
                if(temp2 > 0){
                    temp1 = temp1 + 1;
                }
                for(Long i = 1l; i<=temp1;i++){
                    if(i == temp1){
                        sheets.add(sheetName + ((i-1l)*size+1) + "-" + count);

                    }
                    else{
                        sheets.add(sheetName + ((i-1l)*size+1) + "-" + ((i)*size));
                    }
                }

            }
            else{
                sheets.add(sheetName);
            }



            SXSSFWorkbook tplWorkBook = new SXSSFWorkbook(flushRows);
            Map<String, CellStyle> cellStyleMap = styleMap(tplWorkBook);
            // 表头样式
            CellStyle headStyle = cellStyleMap.get("head");
            CellStyle headTitleStyle = cellStyleMap.get("headTitle");
            // 生成一个表格

            for(String str:sheets){

                Sheet  sheet = tplWorkBook.createSheet(str);
                // 设置表格默认列宽度
                sheet.setDefaultColumnWidth(DEFAULT_COLUMN_SIZE);
                // 合并单元格
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnNames.size() - 1));

                // 产生表格标题行
                Row rowMerged = sheet.createRow(0);
                Cell mergedCell = rowMerged.createCell(0);
                mergedCell.setCellStyle(headTitleStyle);
                mergedCell.setCellValue(new XSSFRichTextString(str));
                //写入成功一行数据递增行数

                // 产生表格表头列标题行
                Row row = sheet.createRow(1);
                for (int i = 0; i < columnNames.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(headStyle);
                    RichTextString text = new XSSFRichTextString(columnNames.get(i));
                    cell.setCellValue(text);
                }
                sheet.createFreezePane( 0, 2, 0, 2 );
            }


            ConsoleProgressBar CP3 = new ConsoleProgressBar(0, count, 50, '#','=');




            Long k = 1l;

            for(String str:sheets){
                Long thisSize = size;
                Sheet sheet = tplWorkBook.getSheet(str);

                int currentRowNum_1 = 2;

                Long i = 0l;
                try {
                    while (rs.next()) {

                        Row row = sheet.createRow(currentRowNum_1);
                        for(int j = 0;j<columnNames.size();j++){
                            Cell contentCell = row.createCell(j);
                            String name = columnNames.get(j);
                            String values = rs.getString(name);
                            contentCell.setCellValue(values);
                        }

                        CP3.show(k,"正在导出第"+k+"条数据...");
                        //写入成功一行数据递增行数
                        currentRowNum_1 = currentRowNum_1 + 1;
                        i++;
                        k++;

                        //每当行数达到设置的值就刷新数据到硬盘,以清理内存
                        if(i%flushRows==0){
                            try {
                                ((SXSSFSheet)sheet).flushRows();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        if(i.longValue() == thisSize.longValue()){
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }


            try {
                OutputStream ops = null;
                try {
                    File tmpFile = assertFile(directory, fileName);
                    ops = new FileOutputStream(tmpFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                tplWorkBook.write(ops);
                ops.flush();
                ops.close();
            } catch (IOException e) {
                try {
                    throw new Exception(e);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        //根据多个文件拆分
        if(SCHEMA == 2){
            List<String> fileNames = new ArrayList<String>();

            if(count > size){
                Long temp1 = count / size;
                Long temp2 = count % size;
                if(temp2 > 0){
                    temp1 = temp1 + 1;
                }
                for(Long i = 1l; i<=temp1;i++){
                    if(i == temp1){
                        fileNames.add(sheetName + ((i-1l)*size+1) + "-" + count);

                    }
                    else{
                        fileNames.add(sheetName + ((i-1l)*size+1) + "-" + ((i)*size));
                    }
                }

            }
            else{
                fileNames.add(sheetName);
            }

            Long k = 1l;

            ConsoleProgressBar CP3 = new ConsoleProgressBar(0, count, 50, '#','=');
            for(String fileNameOne:fileNames){

                SXSSFWorkbook tplWorkBook = new SXSSFWorkbook(flushRows);
                Map<String, CellStyle> cellStyleMap = styleMap(tplWorkBook);
                // 表头样式
                CellStyle headStyle = cellStyleMap.get("head");
                CellStyle headTitleStyle = cellStyleMap.get("headTitle");
                // 生成一个表格



                Sheet sheet = null;
                if (sheet == null) {
                    sheet = tplWorkBook.createSheet(fileNameOne);
                }
                // 设置表格默认列宽度
                sheet.setDefaultColumnWidth(DEFAULT_COLUMN_SIZE);
                // 合并单元格
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnNames.size() - 1));

                // 产生表格标题行
                Row rowMerged = sheet.createRow(0);
                Cell mergedCell = rowMerged.createCell(0);
                mergedCell.setCellStyle(headTitleStyle);
                mergedCell.setCellValue(new XSSFRichTextString(fileNameOne));
                //写入成功一行数据递增行数

                // 产生表格表头列标题行
                Row row = sheet.createRow(1);
                for (int i = 0; i < columnNames.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(headStyle);
                    RichTextString text = new XSSFRichTextString(columnNames.get(i));
                    cell.setCellValue(text);
                }
                sheet.createFreezePane( 0, 2, 0, 2 );


                Long thisSize = size;


                int currentRowNum_1 = 2;

                Long i = 0l;
                try {
                    while (rs.next()) {

                        Row row1 = sheet.createRow(currentRowNum_1);
                        for(int j = 0;j<columnNames.size();j++){
                            Cell contentCell = row1.createCell(j);
                            String name = columnNames.get(j);
                            String values = rs.getString(name);
                            contentCell.setCellValue(values);
                        }

                        CP3.show(k,"正在导出第"+k+"条数据...");
                        //写入成功一行数据递增行数
                        currentRowNum_1 = currentRowNum_1 + 1;
                        i++;
                        k++;

                        //每当行数达到设置的值就刷新数据到硬盘,以清理内存
                        if(i%flushRows==0){
                            try {
                                ((SXSSFSheet)sheet).flushRows();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        if(i.longValue() == thisSize.longValue()){
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }




                try {
                    OutputStream ops = null;
                    try {
                        File tmpFile = assertFile(directory, fileNameOne);
                        ops = new FileOutputStream(tmpFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    tplWorkBook.write(ops);
                    ops.flush();
                    ops.close();
                } catch (IOException e) {
                    try {
                        throw new Exception(e);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }

        }




    }




    /**
     * 创建单元格表头样式
     *
     * @param workbook 工作薄
     */
    private CellStyle createCellHeadStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        //设置对齐样式
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 表头样式
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }


    /**
     * 创建单元格表头样式
     *
     * @param workbook 工作薄
     */
    private CellStyle createCellHeadTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        //设置对齐样式
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 表头样式
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        font.setFontHeightInPoints((short) 32);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * 创建单元格正文样式
     *
     * @param workbook 工作薄
     */
    private CellStyle createCellContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        //设置对齐样式
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(XSSFCellStyle.NO_FILL);
        style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * 单元格样式(Integer)列表
     */
    private CellStyle createCellContent4IntegerStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        //设置对齐样式
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(XSSFCellStyle.NO_FILL);
        style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style.setFont(font);
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));//数据格式只显示整数
        return style;
    }

    /**
     * 单元格样式(Double)列表
     */
    private CellStyle createCellContent4DoubleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        //设置对齐样式
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(XSSFCellStyle.NO_FILL);
        style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style.setFont(font);
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));//保留两位小数点
        return style;
    }

    /**
     * 单元格样式列表
     */
    private Map<String, CellStyle> styleMap(Workbook workbook) {
        Map<String, CellStyle> styleMap = new LinkedHashMap<String, CellStyle>();
        styleMap.put("head", createCellHeadStyle(workbook));
        styleMap.put("headTitle", createCellHeadTitleStyle(workbook));
        styleMap.put("content", createCellContentStyle(workbook));
        styleMap.put("integer", createCellContent4IntegerStyle(workbook));
        styleMap.put("double", createCellContent4DoubleStyle(workbook));
        return styleMap;
    }

    public Long getCountOver() {
        return countOver;
    }

    public void setCountOver(Long countOver) {
        this.countOver = countOver;
    }

    public Long getCountAll() {
        return countAll;
    }

    public void setCountAll(Long countAll) {
        this.countAll = countAll;
    }
}