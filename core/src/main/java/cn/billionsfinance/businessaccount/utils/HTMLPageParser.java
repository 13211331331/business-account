package cn.billionsfinance.businessaccount.utils;


import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by hanlin.huang on 2018/2/2.
 */
public class HTMLPageParser {


   private static String driver = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://118.190.44.59:3306/bbs?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT&useSSL=false";
    private static String username = "root";
    private static String password = "bqjr_hhl123456";
    private static Connection conn = null;
    private static  PreparedStatement pstmt;




    public static void main(String[] args) throws Exception {

        try {
            Class.forName(driver); //classLoader,加载对应驱动//(1)
            conn = (Connection) DriverManager.getConnection(url, username, password);//进行链接(2)
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {//这异常要经常处理!(3)
            e.printStackTrace();
        }


        //创建PreparedStatment实例


       // int urlNumber = 1352401;


        //int start = 113;
        int start = 3967;
        int end = 24000000;

        for(int i = start;i<end;i++){
            insertOne(i);
            Thread.sleep(200);
        }



        conn.close();//关

    }

    private static void insertOne(int urlNumber) throws IOException {
        String sql = "insert into data_user (name,mobile,address,company,tel,sourceid) values(?,?,?,?,?,?)";//(4)

           String url = "http://www.cnlist.org/gongsi/"+urlNumber+".html";
        Document doc = null;
        try {
            //org.jsoup.Connection connection = Jsoup.connect(url);
            //if(connection != null){
                doc = Jsoup.connect(url).get();
           // }

        }catch (Exception e){
            System.out.println(urlNumber+ "  null");
            return;
        }


            Elements container = doc.getElementsByClass("zdcp");

            Document containerDoc = Jsoup.parse(container.toString());

            Elements module = containerDoc.getElementsByClass("zdcp-rt");

            Document moduleDoc = Jsoup.parse(module.toString());

            //Elements clearfix = moduleDoc.getElementsByClass("clearfix");  //DOM的形式

            Elements clearfix = moduleDoc.getElementsByClass("jtxx");

        Elements comanyNmaeDoc = moduleDoc.getElementsByClass("fts");


            Document moduleName = Jsoup.parse(clearfix.toString());




            Elements userInfosDoc = moduleName.getElementsByTag("dd");



            int i = 1;
            String name  = "";
            String mobile ="";
            String address ="";

             String comanyname  = "";
             String tel  = "";

        comanyname = comanyNmaeDoc.text();
            for (Element link : userInfosDoc) {

                String linkText = link.text();
                if(i == 1){
                    name = linkText;
                }
                if(i == 2){
                    tel = linkText;
                }

                if(i == 3){
                    mobile = linkText;
                }

                if(i == 4){
                    address = linkText;
                }
                i++;
            }



            try {
                //设置sql语句
                pstmt =  conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, mobile);
                pstmt.setString(3, address);

                pstmt.setString(4, comanyname);
                pstmt.setString(5, tel);
                pstmt.setInt(6, urlNumber);

                System.out.println(urlNumber + "  " + "name:"+name + "  mobile:"+mobile +  "   address:"+address);
                //执行
                pstmt.executeUpdate();//这玩意应该是返回affected row(s) 受影响列
                pstmt.close();//关
            } catch (SQLException e) {//处理异常
                e.printStackTrace();
            }



    }


}



