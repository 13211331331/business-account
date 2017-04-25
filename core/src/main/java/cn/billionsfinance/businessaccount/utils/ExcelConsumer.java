package cn.billionsfinance.businessaccount.utils;

import cn.billionsfinance.businessaccount.core.bean.BeanExcelExport;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanlin.huang on 2017/4/14.
 */
public class ExcelConsumer implements Runnable {


    //private ConsoleProgressBar consoleProgressBar;


    private List<String> columnNames;

    public ExcelConsumer(List<String> columnNames) {
       // this.consoleProgressBar = consoleProgressBar;
        this.columnNames = columnNames;
    }

    public void run() {
        int i = 0;
        ArrayList<BeanExcelExport> lastList = null;
        lastList = new ArrayList<BeanExcelExport>();
        while(!(ExportExcel2007.countOver == 0)){
            ArrayList<BeanExcelExport> list = null;
            try {
                if(lastList.size()>0){
                    list = lastList;
                    lastList = new ArrayList<BeanExcelExport>();
                }else{
                    list = ExeclBasket.consume();
                }
                boolean flag = false;
                if(list != null){

                    for(BeanExcelExport bean:list){
                        if(!flag){
                            Sheet sheet = null;
                            if(ExportExcel2007.SCHEMA == 1){
                                sheet = ExportExcel2007.tplWorkBook.get(0).getSheet(ExportExcel2007.SHEETS_OR_FILES.get(ExportExcel2007.PAGE_CURRENT.intValue()));
                            }
                            if(ExportExcel2007.SCHEMA == 2){
                                int fileIndex = ExportExcel2007.getFileIndex1();//(bean.getSheetOrFile());
                                sheet = ExportExcel2007.tplWorkBook.get(fileIndex).getSheet(ExportExcel2007.SHEETS_OR_FILES.get(fileIndex));
                            }
                            Row row = sheet.createRow(i + 2);
                            for(int j=0;j<columnNames.size();j++){
                                Cell contentCell = row.createCell(j);
                                contentCell.setCellValue(bean.getRowColumns().get(columnNames.get(j)));
                            }
                            //写入成功一行数据递增行数
                            //每当行数达到设置的值就刷新数据到硬盘,以清理内存
                            if(i%ExportExcel2007.flushRows==0){
                                try {
                                    ((SXSSFSheet)sheet).flushRows();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            i++;
                            ExportExcel2007.countOverNONO();
                            if(i == ExportExcel2007.SHEET_FILE_SIZE.intValue()){
                                ExportExcel2007.pageYESYES();
                                i =0;
                                flag = true;
                            }
                        }
                        else{
                            lastList.add(bean);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }
}
