package cn.billionsfinance.businessaccount.utils;

import cn.billionsfinance.businessaccount.core.bean.BeanExcelExport;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Excel 相关操作类(大数据量写入但受Excel数据行数限制)
 * 先写入Excel标题(writeExcelTitle)，再写入数据(writeExcelData)，最后释放资源(dispose)
 */
public class ExportExcel2007 {

    public static int THREAD_NUMBER = 50;

    //总数
    public static Long countAll = 0l;
    //剩余未处理
    public static Long countOver = 0l;

    public static SXSSFWorkbook tplWorkBook;

    public static String directory;
    public static String fileName;


    //默认列宽度
    public static int DEFAULT_COLUMN_SIZE = 15;

    public static Long SHEET_FILE_SIZE = 1000l;

    //刷新写入硬盘数据阀值
    public static final int flushRows = 100;

    public static int SCHEMA = 1;

    public static int QUEUE_LIST_SIZE;

    public static Long PAGE_NUMBER = 0l;
    public static Long PAGE_CURRENT = 0L;


    /**
     * 导出字符串数据
     *
     * @param columnNames 表头
     */
    public void exportExcel(String directory, String fileName,Long count, String sheetName, List<String> columnNames,ResultSet rs) throws IOException {

        this.countAll = count;
        this.countOver = count;

        List<String> sheetsOrFiles = new ArrayList<String>();

        if(count > SHEET_FILE_SIZE){
            Long temp1 = count / SHEET_FILE_SIZE;
            Long temp2 = count % SHEET_FILE_SIZE;
            PAGE_NUMBER = temp1;
            if(temp2 > 0){
                temp1 = temp1 + 1;
                PAGE_NUMBER = temp1;
            }
            for(Long i = 1l; i<=temp1;i++){
                if(i == temp1){
                    sheetsOrFiles.add(sheetName + ((i-1l)*SHEET_FILE_SIZE+1) + "-" + count);
                }
                else{
                    sheetsOrFiles.add(sheetName + ((i-1l)*SHEET_FILE_SIZE+1) + "-" + ((i)*SHEET_FILE_SIZE));
                }
            }
        }
        else{
            sheetsOrFiles.add(sheetName);
            PAGE_NUMBER = 1l;
        }

        this.tplWorkBook = new SXSSFWorkbook(flushRows);
        Map<String, CellStyle> cellStyleMap = styleMap(tplWorkBook);
        // 表头样式
        CellStyle headStyle = cellStyleMap.get("head");
        CellStyle headTitleStyle = cellStyleMap.get("headTitle");
        // 生成一个表格

        for(String str:sheetsOrFiles){

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

        AsynWorker.doAsynWork(new Object[]{(ArrayList<String>) sheetsOrFiles, (ArrayList<String>) columnNames, directory, fileName}, this, "doingExport");
        AsynWorker.doAsynWork(new Object[]{}, this, "closeFile");
        putting(columnNames, rs, sheetsOrFiles);
    }


    public void closeFile(){
        while (true){
            if(ExportExcel2007.countOver<=0){
                try {
                    OutputStream ops = null;
                    try {
                        ops = new FileOutputStream(assertFile(directory, fileName));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    tplWorkBook.write(ops);
                    ops.flush();
                    ops.close();
                    System.exit(0);
                } catch (IOException e) {
                    try {
                        throw new Exception(e);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
    public void putting(List<String> columnNames, ResultSet rs, List<String> sheetsOrFiles) {

        ArrayList<BeanExcelExport> list;
        int index = 0;
        try {
            list = new ArrayList<BeanExcelExport>();
            while (rs.next()) {
                BeanExcelExport bean = new BeanExcelExport();
                bean.setSheetOrFile(getSheetOrFileName(sheetsOrFiles));
                bean.setRow(index +2);
                bean.setRowColumns(getMap(rs,columnNames));
                list.add(bean);
                index++;
                if(index == SHEET_FILE_SIZE.intValue()){
                    ArrayList<BeanExcelExport> listAdd = (ArrayList<BeanExcelExport>) list.clone();
                    try {
                        ExeclBasket.produce(listAdd);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    index = 0;
                    pageYESYES();
                    list = new ArrayList<BeanExcelExport>();
                }
            }
            ArrayList<BeanExcelExport> listAdd = (ArrayList<BeanExcelExport>) list.clone();
            try {
                ExeclBasket.produce(listAdd);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getSheetOrFileName(List<String> sheetsOrFiles) {
        return sheetsOrFiles.get(PAGE_CURRENT.intValue());
    }


    public void doingExport(ArrayList<String> sheetsOrFiles,ArrayList<String> columnNames,String directory, String fileName) {
        ExportExcel2007.directory = directory;
        ExportExcel2007.fileName = fileName;

        ExecutorService service = Executors.newFixedThreadPool(THREAD_NUMBER);
        ConsoleProgressBar CP3 = new ConsoleProgressBar(0, countAll, 50, '#','=');

        //ExcelConsumer consumer1 = new ExcelConsumer(CP3,directory,fileName,columnNames);
        //service.submit(consumer1);
        for(int i=0;i<THREAD_NUMBER;i++){
            if(this.countOver == 0){
                break;
            }
            ExcelConsumer consumer1 = new ExcelConsumer(CP3,columnNames);
            service.submit(consumer1);
            //if(true)break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 断言Excel文件写入之前的条件
     *
     * @param directory 目录
     * @param fileName  文件名
     * @return file
     * @throws java.io.IOException
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


    private Map<String, String> getMap(ResultSet rs, List<String> columnNames) {
        Map<String, String> map = new HashMap<String, String>();
        for(String key:columnNames){
            try {
                map.put(key,rs.getString(key));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map;
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

    public static synchronized void countOverNONO() {
        countOver = countOver - 1;
    }
    public static synchronized void pageYESYES() {
        PAGE_CURRENT = PAGE_CURRENT + 1l;
    }
}