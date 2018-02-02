package cn.billionsfinance.businessaccount.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by hanlin.huang on 2018/2/2.
 */
public class HTMLPageParser {

    public static void main(String[] args) throws Exception {
        String url = "http://www.cnlist.org/gongsi/1352401.html";
        //目的网页URL地址
        //String sss = getURLInfo(url,"utf-8");
        //System.out.println(sss);

        try {

            Document doc = Jsoup.connect(url).get();

            Elements container = doc.getElementsByClass("zdcp");

            Document containerDoc = Jsoup.parse(container.toString());

            Elements module = containerDoc.getElementsByClass("zdcp-rt");

            Document moduleDoc = Jsoup.parse(module.toString());

            //Elements clearfix = moduleDoc.getElementsByClass("clearfix");  //DOM的形式

            Elements clearfix = moduleDoc.getElementsByClass("jtxx");
            Document moduleName = Jsoup.parse(clearfix.toString());

          //  Elements nameE = moduleName.select("a");
             String name =   moduleName.text();
            System.out.println(name);



        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String getURLInfo(String urlInfo,String charset) throws Exception {
        //读取目的网页URL地址，获取网页源码
        URL url = new URL(urlInfo);
        HttpURLConnection httpUrl = (HttpURLConnection)url.openConnection();
        InputStream is = httpUrl.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            //这里是对链接进行处理
            //line = line.replaceAll("</?a[^>]*>", "");
            //这里是对样式进行处理
            //line = line.replaceAll("<(\\w+)[^>]*>", "<$1>");
            sb.append(line);
        }
        is.close();
        br.close();
        //获得网页源码
        return sb.toString();
    }



}



