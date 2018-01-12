package cn.billionsfinance.businessaccount.utils;


import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by hanlin.huang on 2017/4/10.
 */
public class ExportTest {



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

            String[] sqls = getSqlAndRename();
            String sql = sqls[1];

            String countSql = "SELECT COUNT(1) SUM FROM (" + sql + ")";
            ConsoleProgressBar CP1 = new ConsoleProgressBar(0, 2, 50, '#', '=');
            CP1.show(1, "初始数据库...");

            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            Integer count = 0;
            ResultSetMetaData rsmd = null;
            try {
                conn = JdbcUtil.getConnection();
                stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);

                CP1.show(2, "初始数据库完成");

                ConsoleProgressBar CP2 = new ConsoleProgressBar(0, 2, 50, '#', '=');
                CP2.show(1, "获取总记录数...");
                rs = stmt.executeQuery(countSql);
                while (rs.next()) {
                    count = rs.getInt("SUM");
                }
                CP2.show(2, "读取文件："+sqls[0]+"  总记录数：" + count);

                JdbcUtil.close(rs);
                rs = stmt.executeQuery(sql);
                rsmd = rs.getMetaData();

                List<String> list = new ArrayList<String>();
                List<String> list2 = new ArrayList<String>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String name = rsmd.getColumnName(i);
                    list.add(name);
                    list2.add(rsmd.getColumnTypeName(i));
                }

                ExportExcel2007 exportExcel2007 = new ExportExcel2007();

                String path = ExportTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                if (path.substring(0, 1).endsWith("/")) {
                    path = path.substring(1, path.length());
                }
                if (path.endsWith(".jar")) {
                    path = path.substring(0, path.lastIndexOf("/"));
                }
                try {
                    exportExcel2007.exportExcel(path, sqls[0].substring(0, sqls[0].indexOf(".")), count, list,list2, rs);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                while (true){
                    if(ExportExcel2007.completeAll){
                        Date end = new Date();

                        System.out.println("");
                        System.out.println("--------------------------------------------------");
                        System.out.println("导出文件："+sqls[0]+" 总耗时："+ DateDistance.getDistanceTime(start,end));
                        System.out.println("--------------------------------------------------");
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                JdbcUtil.close(rs);
                JdbcUtil.close(stmt);
                JdbcUtil.close(conn);

            }
        }



    }

    private static String[] getSqlAndRename() {
        String[] arr = new String[2];
        String path = ExportTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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
            if(str.endsWith(".sql")){
                sqlFile = str;
                break;
            }
        }
        arr[0]= sqlFile;

        if(sqlFile == null){
            try {
                throw new Exception("未找到sql文件");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        File file = new File(path + sqlFile);
        File file1 = new File(path + sqlFile+".over");

        BufferedReader reader = null;
        String sql = "";
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file),"utf-8"));
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

        sql =  sql.replaceAll("\\$","");
        sql =  sql.replaceAll("\\{","@");
        sql =  sql.replaceAll("\\}", "@");
        for (Map.Entry<String, String> entry : ExportExcel2007.SQL_MAP.entrySet()) {
            sql = sql.replaceAll("@"+entry.getKey()+"@",entry.getValue());
        }
        file.renameTo(file1);
        arr[1]= sql;
        return arr;
    }



    private static boolean initConfig() throws Exception {

        Boolean result = true;
        String path = ExportTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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
        String sqlConfigFile = path + "sql-config.properties";



        Properties pps = new Properties();
        Properties sqlPps = new Properties();

        InputStream in = new BufferedInputStream(new FileInputStream(configFile));
        InputStream sqlIn = new BufferedInputStream(new FileInputStream(sqlConfigFile));
        pps.load(in);
        sqlPps.load(sqlIn);

        String jdbc_driver = pps.getProperty("jdbc.driver");
        String jdbc_url = pps.getProperty("jdbc.url");
        String jdbc_username = pps.getProperty("jdbc.username");
        String jdbc_password = pps.getProperty("jdbc.password");
        JdbcUtil.setUrl(jdbc_url);
        JdbcUtil.setDriver(jdbc_driver);
        JdbcUtil.setUser(jdbc_username);
        JdbcUtil.setPassword(jdbc_password);

        //String DEFAULT_COLUMN_SIZE = pps.getProperty("DEFAULT_COLUMN_SIZE","15");
        //ExportExcel2007.DEFAULT_COLUMN_SIZE = Integer.valueOf(DEFAULT_COLUMN_SIZE);

        //String SHEET_FILE_SIZE = pps.getProperty("SHEET_FILE_SIZE","1000");
       // ExportExcel2007.SHEET_FILE_SIZE = Long.valueOf(SHEET_FILE_SIZE);

        String SCHEMA = pps.getProperty("SCHEMA","1");
        ExportExcel2007.SCHEMA = Integer.valueOf(SCHEMA);

        //String THREAD_NUMBER = pps.getProperty("THREAD_NUMBER","50");
        //ExportExcel2007.THREAD_NUMBER = Integer.valueOf(THREAD_NUMBER);


        //String QUEUE_LIST_SIZE = pps.getProperty("QUEUE_LIST_SIZE","5000");
        //ExportExcel2007.QUEUE_LIST_SIZE = Integer.valueOf(QUEUE_LIST_SIZE);

        String SHOW_THREAD = pps.getProperty("SHOW_THREAD","1");
        ExportExcel2007.SHOW_THREAD = Integer.valueOf(SHOW_THREAD);


        String PAGE_SIZESTR = pps.getProperty("PAGE_SIZE","10000");
        ExportExcel2007.PAGE_SIZE =  Integer.valueOf(PAGE_SIZESTR);

        String EXCEL_SPLIT = pps.getProperty("EXCEL_SPLIT","0");
        ExportExcel2007.EXCEL_SPLIT =  Integer.valueOf(EXCEL_SPLIT);



        // 返回Properties中包含的key-value的Set视图
        Set<Map.Entry<Object, Object>> set = sqlPps.entrySet();
        // 返回在此Set中的元素上进行迭代的迭代器
        Iterator<Map.Entry<Object, Object>> it = set.iterator();
        String key = null, value = null;
        // 循环取出key-value
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = it.next();
            key = String.valueOf(entry.getKey());
            value = String.valueOf(entry.getValue());
            key = key == null ? key : key.trim();
            value = value == null ? value : value.trim();
            // 将key-value放入map中
            //System.out.println(key);
            //System.out.println(value);
            ExportExcel2007.SQL_MAP.put(key,value);
        }


        return result;

    }


}
