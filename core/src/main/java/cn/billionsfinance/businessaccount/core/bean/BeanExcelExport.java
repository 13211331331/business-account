package cn.billionsfinance.businessaccount.core.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by hanlin.huang on 2017/4/13.
 */
public class BeanExcelExport implements Serializable {

    //sheet名称或者文件名称
    private String sheetOrFile;

    //行编号
    private int row;

    //单元格内容
    private Map<String,String> rowColumns;


    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Map<String, String> getRowColumns() {
        return rowColumns;
    }

    public void setRowColumns(Map<String, String> rowColumns) {
        this.rowColumns = rowColumns;
    }

    public String getSheetOrFile() {
        return sheetOrFile;
    }

    public void setSheetOrFile(String sheetOrFile) {
        this.sheetOrFile = sheetOrFile;
    }
}
