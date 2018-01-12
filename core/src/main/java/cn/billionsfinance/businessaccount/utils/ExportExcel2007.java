package cn.billionsfinance.businessaccount.utils;

import com.sun.rowset.CachedRowSetImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import javax.sql.rowset.CachedRowSet;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Excel 相关操作类(大数据量写入但受Excel数据行数限制)
 * 先写入Excel标题(writeExcelTitle)，再写入数据(writeExcelData)，最后释放资源(dispose)
 */
public class ExportExcel2007 {

    public static int PAGE_SIZE = 10000;
    //总数
    public static Integer countAll = 0;
    //剩余未处理
    public static Integer countOver = 0;

    public static List<SXSSFWorkbook> tplWorkBook;

    public static String directory;

    public static List<String> excelFileName;

    public static List<String> SHEETS_OR_FILES;

    //刷新写入硬盘数据阀值
    public static final int flushRows = 100;

    //是否启用拆分模式
    public static int EXCEL_SPLIT = 0;
    public static int SCHEMA = 1;
    public static int SHOW_THREAD = 1;


    public static Integer PAGE_NUMBER = 0;

    public static boolean complete = false;

    public static boolean completeAll = false;

    public static Map<String,String> SQL_MAP = new HashMap<String,String>();


    /**
     * 导出字符串数据
     *
     * @param columnNames 表头
     */
    public void exportExcel(String directory, String fileName,Integer count, List<String> columnNames,List<String> columnTypes,ResultSet rs) throws IOException, SQLException {

        this.countAll = count;
        this.countOver = count;


        List<String> sheetsOrFiles = new ArrayList<String>();

        if(count > PAGE_SIZE){
            Integer temp1 = count / PAGE_SIZE;
            Integer temp2 = count % PAGE_SIZE;
            PAGE_NUMBER = temp1;
            if(temp2 > 0){
                temp1 = temp1 + 1;
                PAGE_NUMBER = temp1;
            }
            for(Integer i = 1; i<=temp1;i++){
                if(i == temp1){
                    sheetsOrFiles.add(fileName + ((i-1l)*PAGE_SIZE+1) + "-" + count);
                }
                else{
                    sheetsOrFiles.add(fileName + ((i-1l)*PAGE_SIZE+1) + "-" + ((i)*PAGE_SIZE));
                }
            }
        }
        else{
            sheetsOrFiles.add(fileName);
            PAGE_NUMBER = 1;
        }
        if(ExportExcel2007.EXCEL_SPLIT == 0){
            sheetsOrFiles.clear();
            sheetsOrFiles.add(fileName);
        }
        SHEETS_OR_FILES = sheetsOrFiles;
        ExportExcel2007.directory = directory;

        if(ExportExcel2007.EXCEL_SPLIT == 0){
            ExportExcel2007.excelFileName = Arrays.asList(fileName);
        }

        if(ExportExcel2007.EXCEL_SPLIT == 1){
            if(ExportExcel2007.SCHEMA == 1){
                ExportExcel2007.excelFileName = Arrays.asList(fileName);
            }
            if(ExportExcel2007.SCHEMA == 2){
                ExportExcel2007.excelFileName = sheetsOrFiles;
            }
        }

        tplWorkBook = new ArrayList<SXSSFWorkbook>();

        if(ExportExcel2007.EXCEL_SPLIT == 1){
            for(int i=0;i<PAGE_NUMBER;i++){
                SXSSFWorkbook book = new SXSSFWorkbook(flushRows);
                if(ExportExcel2007.SCHEMA == 1){

                    for(String str:sheetsOrFiles){

                        Sheet  sheet = book.createSheet(str);
                        // 合并单元格
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnNames.size() - 1));

                        // 产生表格标题行
                        Row rowMerged = sheet.createRow(0);
                        Cell mergedCell = rowMerged.createCell(0);
                        mergedCell.setCellValue(new XSSFRichTextString(str));
                        //写入成功一行数据递增行数

                        // 产生表格表头列标题行
                        Row row = sheet.createRow(1);
                        for (int j = 0; j < columnNames.size(); j++) {
                            Cell cell = row.createCell(j);
                            RichTextString text = new XSSFRichTextString(columnNames.get(j));
                            cell.setCellValue(text);
                        }
                        sheet.createFreezePane( 0, 2, 0, 2 );
                    }
                }

                if(ExportExcel2007.SCHEMA == 2){

                    Sheet  sheet = book.createSheet(ExportExcel2007.SHEETS_OR_FILES.get(i));
                    // 合并单元格
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnNames.size() - 1));

                    // 产生表格标题行
                    Row rowMerged = sheet.createRow(0);
                    Cell mergedCell = rowMerged.createCell(0);
                    mergedCell.setCellValue(new XSSFRichTextString(ExportExcel2007.SHEETS_OR_FILES.get(i)));
                    //写入成功一行数据递增行数

                    // 产生表格表头列标题行
                    Row row = sheet.createRow(1);
                    for (int j = 0; j < columnNames.size(); j++) {
                        Cell cell = row.createCell(j);
                        //cell.setCellStyle(headStyle);
                        RichTextString text = new XSSFRichTextString(columnNames.get(j));
                        cell.setCellValue(text);
                    }
                    sheet.createFreezePane( 0, 2, 0, 2 );
                }
                tplWorkBook.add(book);
            }
        }
        if(ExportExcel2007.EXCEL_SPLIT == 0){
            SXSSFWorkbook book = new SXSSFWorkbook(flushRows);
            Sheet  sheet = book.createSheet(ExportExcel2007.SHEETS_OR_FILES.get(0));
            // 合并单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnNames.size() - 1));

            // 产生表格标题行
            Row rowMerged = sheet.createRow(0);
            Cell mergedCell = rowMerged.createCell(0);
            mergedCell.setCellValue(new XSSFRichTextString(ExportExcel2007.SHEETS_OR_FILES.get(0)));
            //写入成功一行数据递增行数

            // 产生表格表头列标题行
            Row row = sheet.createRow(1);
            for (int j = 0; j < columnNames.size(); j++) {
                Cell cell = row.createCell(j);
                //cell.setCellStyle(headStyle);
                RichTextString text = new XSSFRichTextString(columnNames.get(j));
                cell.setCellValue(text);
            }
            sheet.createFreezePane( 0, 2, 0, 2 );
            tplWorkBook.add(book);
        }


        AsynWorker.doAsynWork(new Object[]{}, this, "closeFile");
        AsynWorker.doAsynWork(new Object[]{}, this, "showProcess");
        if(SHOW_THREAD == 1){
            AsynWorker.doAsynWork(new Object[]{}, new ThreadViewer(), "showThreads");
        }
        putting(columnNames,columnTypes,count, rs);
    }

    public void showProcess(){
        ConsoleProgressBar CP3 = new ConsoleProgressBar(0, countAll, 50, '#','=');
        boolean flag = false;
        //写入成功一行数据递增行数
        while (true){
            try {
                Integer over = ExportExcel2007.countAll  - ExportExcel2007.countOver;
                if(!flag){
                    CP3.show(over,"正在导出第"+(over)+"条数据...");
                }
                if(over.intValue() == ExportExcel2007.countAll.intValue()){
                    flag = true;
                }
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeFile(){
        while (true){
            if(complete){
                try {
                    List<OutputStream> ops = new ArrayList<OutputStream>();
                    try {

                        for(String str:ExportExcel2007.excelFileName){
                            OutputStream os = new FileOutputStream(assertFile(directory, str));
                            ops.add(os);
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    for(int i=0;i<ops.size();i++){
                        OutputStream outputStream = ops.get(i);
                        tplWorkBook.get(i).write(outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }

                    completeAll = true;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
    public void putting(List<String> columnNames,List<String> columnTypes, Integer count,ResultSet rs) throws SQLException {

        int tmp1 = count/PAGE_SIZE;
        int tmp2 = count%PAGE_SIZE;
        int pages = tmp1;
        if(tmp2 != 0){
            pages = tmp1 + 1;
        }
        for(int i=1;i<=pages;i++){
            CachedRowSet crs = new CachedRowSetImpl();
            crs.setPageSize(PAGE_SIZE);
            crs.populate(rs, (i-1)*PAGE_SIZE + 1);
            puttingPage(i,crs,columnNames,columnTypes);
        }

    }

    public void puttingPage(Integer page,CachedRowSet crs, List<String> columnNames,List<String> columnTypes) throws SQLException {
        crs.beforeFirst(); // 可滚动
        int index = 0;
        while (crs.next()) {

            Sheet sheet = null;
            Row row = null;
            if(ExportExcel2007.EXCEL_SPLIT == 0){
                sheet = ExportExcel2007.tplWorkBook.get(0).getSheet(SHEETS_OR_FILES.get(0));
                row = sheet.createRow(index + 2);
            }

            if(ExportExcel2007.EXCEL_SPLIT == 1){
                if(ExportExcel2007.SCHEMA == 1){
                    sheet = ExportExcel2007.tplWorkBook.get(0).getSheet(SHEETS_OR_FILES.get(page - 1));
                    row = sheet.createRow(index + 2);
                }
                if(ExportExcel2007.SCHEMA == 2){
                    sheet = ExportExcel2007.tplWorkBook.get(page-1).getSheet(SHEETS_OR_FILES.get(page-1));
                    row = sheet.createRow(index + 2);
                }
            }


            index ++;

            int index1 = 0;
            for(String key:columnNames){
                boolean isNumber = true;
                Float f = null;
                String str = "";
                try {
                    if(ifIsNumber(index1, columnTypes)){
                        f = crs.getFloat(key);
                        isNumber = true;
                        if(f == null){
                            f = 0f;
                        }
                    }else{
                        str = crs.getString(key);
                        isNumber = false;
                    }


                } catch (SQLException e) {
                    isNumber = false;
                    e.printStackTrace();
                }

                Cell contentCell = row.createCell(index1);
                if(isNumber){
                    contentCell.setCellValue(f);
                }else {
                    contentCell.setCellValue(str);
                }

                index1 ++;
            }
            ExportExcel2007.countOverNONO();


        }

    }

    private boolean ifIsNumber(int index1, List<String> columnTypes) {
        if(columnTypes.get(index1).indexOf("NUMBER") != -1){
            return true;
        }
        return false;
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


    public static synchronized void countOverNONO() {
        countOver = countOver - 1;
        if(countOver == 0){
            complete = true;
        }
    }


}