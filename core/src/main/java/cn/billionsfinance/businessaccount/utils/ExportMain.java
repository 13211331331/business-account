package cn.billionsfinance.businessaccount.utils;


import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by hanlin.huang on 2017/4/10.
 */
public class ExportMain {

    public static void main(String[] args) {
        Date start = new Date();

        boolean isNext = true;

        try {
            isNext = initConfig();
        } catch (Exception e) {
            e.printStackTrace();
            isNext = false;
            return;
        }

        if(isNext){
            String sql = getSql();

            String countSql = "SELECT COUNT(1) SUM FROM (" + sql + ")";
            ConsoleProgressBar CP1 = new ConsoleProgressBar(0, 2, 50, '#', '=');
            CP1.show(1, "初始数据库...");

            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            Long count = 0l;
            ResultSetMetaData rsmd = null;
            try {
                conn = JdbcUtil.getConnection();
                stmt = conn.createStatement();
                CP1.show(2, "初始数据库完成");

                ConsoleProgressBar CP2 = new ConsoleProgressBar(0, 2, 50, '#', '=');
                CP2.show(1, "获取总记录数...");
                rs = stmt.executeQuery(countSql);
                while (rs.next()) {
                    count = rs.getLong("SUM");
                }
                CP2.show(2, "总记录数：" + count);

                JdbcUtil.close(rs);
                rs = stmt.executeQuery(sql);
                rsmd = rs.getMetaData();


                List<String> list = new ArrayList<String>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String name = rsmd.getColumnName(i);
                    list.add(name);
                }

                ExportExcel2007 exportExcel2007 = new ExportExcel2007();


                String path = ExportMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                if (path.substring(0, 1).endsWith("/")) {
                    path = path.substring(1, path.length());
                }
                if (path.endsWith(".jar")) {
                    path = path.substring(0, path.lastIndexOf("/"));
                }

                File files = new File(path);
                String[] fileNames = files.list();
                String sqlFile = null;
                for(String str:fileNames){
                    if(str.lastIndexOf(".sql") != -1){
                        sqlFile = str;
                    }
                }

                try {
                    exportExcel2007.exportExcel(path, sqlFile.substring(0, sqlFile.indexOf(".")), count, list, rs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Date end = new Date();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                System.out.println("");
                System.out.println("--------------------------------------------------");
                System.out.println("总耗时："+ DateDistance.getDistanceTime(start,end));
                System.out.println("--------------------------------------------------");

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                JdbcUtil.close(rs);
                JdbcUtil.close(stmt);
                JdbcUtil.close(conn);

            }
        }







    }

    private static String getSql() {
        String path = ExportMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.substring(0, 1).endsWith("/")) {
            path = path.substring(1, path.length());
        }
        if (path.endsWith(".jar")) {
            path = path.substring(0, path.lastIndexOf("/") + 1);
        }

        File files = new File(path);
        String[] fileNames = files.list();
        String sqlFile = null;
        for(String str:fileNames){
            if(str.lastIndexOf(".sql") != -1){
                sqlFile = str;
            }
        }

        if(sqlFile == null){
            try {
                throw new Exception("未找到sql文件");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        File file = new File(path + sqlFile);
        BufferedReader reader = null;
        String sql = "";
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file),"utf-8"));
            //reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                sql += tempString.replace(";","");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        if(StringUtil.isMessyCode(sql)){
            sql = "";
            try {
                reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file),"GBK"));
                String tempString = null;

                // 一次读入一行，直到读入null为文件结束
                while ((tempString = reader.readLine()) != null) {
                    // 显示行号
                    sql += tempString.replace(";","");
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }
        StringUtil.isMessyCode(sql);
        return sql;
    }

    private static boolean initConfig() throws Exception {

        Boolean result = true;
        String path = ExportMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.substring(0, 1).endsWith("/")) {
            path = path.substring(1, path.length());
        }
        if (path.endsWith(".jar")) {
            path = path.substring(0, path.lastIndexOf("/") + 1);
        }

        File files = new File(path);
        String[] fileNames = files.list();
        String sqlFile = null;
        for(String str:fileNames){
            if(str.lastIndexOf(".sql") != -1){
                sqlFile = str;
            }
        }

        if(sqlFile == null){
            try {
                result = false;
                throw new Exception("未找到sql文件");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String configFile = path + "config.properties";



        Properties pps = new Properties();

        InputStream in = new BufferedInputStream(new FileInputStream(configFile));
        pps.load(in);
        String jdbc_driver = pps.getProperty("jdbc.driver");
        String jdbc_url = pps.getProperty("jdbc.url");
        String jdbc_username = pps.getProperty("jdbc.username");
        String jdbc_password = pps.getProperty("jdbc.password");
        JdbcUtil.setUrl(jdbc_url);
        JdbcUtil.setDriver(jdbc_driver);
        JdbcUtil.setUser(jdbc_username);
        JdbcUtil.setPassword(jdbc_password);
        String DEFAULT_COLUMN_SIZE = pps.getProperty("DEFAULT_COLUMN_SIZE","15");
        ExportExcel2007.DEFAULT_COLUMN_SIZE = Integer.valueOf(DEFAULT_COLUMN_SIZE);

        String SHEET_FILE_SIZE = pps.getProperty("SHEET_FILE_SIZE","1000");
        ExportExcel2007.SHEET_FILE_SIZE = Long.valueOf(SHEET_FILE_SIZE);

        String SCHEMA = pps.getProperty("SCHEMA","1");
        ExportExcel2007.SCHEMA = Integer.valueOf(SCHEMA);

        String THREAD_NUMBER = pps.getProperty("THREAD_NUMBER","50");
        ExportExcel2007.THREAD_NUMBER = Integer.valueOf(THREAD_NUMBER);


        String QUEUE_LIST_SIZE = pps.getProperty("QUEUE_LIST_SIZE","5000");
        ExportExcel2007.QUEUE_LIST_SIZE = Integer.valueOf(QUEUE_LIST_SIZE);


        return result;

    }


}
