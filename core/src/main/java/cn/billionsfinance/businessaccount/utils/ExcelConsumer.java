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


    private ConsoleProgressBar consoleProgressBar;


    private List<String> columnNames;

    public ExcelConsumer(ConsoleProgressBar consoleProgressBar,String directory, String fileName,List<String> columnNames) {
        this.consoleProgressBar = consoleProgressBar;
        this.columnNames = columnNames;
    }

    public void run() {
        while(!(ExportExcel2007.countOver == 0)){
            ArrayList<BeanExcelExport> list = null;
            try {
                list = ExeclBasket.consume();
                int i = 0;
                if(list != null){
                    for(BeanExcelExport bean:list){

                        Sheet sheet = ExportExcel2007.tplWorkBook.getSheet(bean.getSheetOrFile());
                        Row row = sheet.createRow(bean.getRow());

                        for(int j=0;j<columnNames.size();j++){
                            Cell contentCell = row.createCell(j);
                            contentCell.setCellValue(bean.getRowColumns().get(columnNames.get(j)));
                        }

                        ExportExcel2007.countOverNONO();
                        consoleProgressBar.show(ExportExcel2007.countAll  - ExportExcel2007.countOver,"正在导出第"+(ExportExcel2007.countAll  - ExportExcel2007.countOver)+"条数据...");
                        i++;
                        //写入成功一行数据递增行数

                        //每当行数达到设置的值就刷新数据到硬盘,以清理内存
                        if(i%ExportExcel2007.flushRows==0){
                            try {
                                ((SXSSFSheet)sheet).flushRows();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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