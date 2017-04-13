package cn.billionsfinance.businessaccount.core.bean;

import java.io.Serializable;

/**
 * Created by hanlin.huang on 2017/4/13.
 */
public class BeanExcelExport implements Serializable {

    //sheet
    private String sheet;

    //行编号
    private Long row;

    //列编号
    private Long column;

    //单元格内容
    private String value;


    public Long getRow() {
        return row;
    }

    public void setRow(Long row) {
        this.row = row;
    }

    public Long getColumn() {
        return column;
    }

    public void setColumn(Long column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSheet() {
        return sheet;
    }

    public void setSheet(String sheet) {
        this.sheet = sheet;
    }
}
