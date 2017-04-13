package cn.billionsfinance.businessaccount.utils;

import java.sql.*;

/**
 * Created by hanlin.huang on 2017/4/10.
 */
public class JdbcUtil {

    private static String url;
    //system为登陆oracle数据库的用户名
    private static String user;
    //manager为用户名system的密码
    private static String password;

    private static String driver;

    //连接数据库的方法
    public  static Connection getConnection(){
        try {
            //初始化驱动包
            Class.forName(driver);
            //根据数据库连接字符，名称，密码给conn赋值
            Connection conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void close(Connection conn) {
        if(conn != null){
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void close(Statement stmt) {
        if(stmt != null){
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        JdbcUtil.url = url;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        JdbcUtil.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        JdbcUtil.password = password;
    }

    public static String getDriver() {
        return driver;
    }

    public static void setDriver(String driver) {
        JdbcUtil.driver = driver;
    }

    public static void close(ResultSet rs) {
        if(rs != null){
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
