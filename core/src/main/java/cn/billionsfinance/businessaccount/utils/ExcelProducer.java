package cn.billionsfinance.businessaccount.utils;


import java.sql.ResultSet;
import java.util.List;

/**
 * Created by hanlin.huang on 2017/4/14.
 */
public class ExcelProducer implements Runnable {


    private ResultSet rs;

    private List<String> columnNames;

    public ExcelProducer(ResultSet rs,List<String> columnNames) {
        this.rs = rs;
        this.columnNames = columnNames;
    }

    public void run() {




    }
}
