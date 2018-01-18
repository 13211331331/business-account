package cn.billionsfinance.businessaccount.utils;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.output.StringBuilderWriter;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hanlin.huang on 2018/1/18.
 */
public class FreeMarkerTest {
    public static void main(String[] args) throws IOException, TemplateException {

        // 创建插值的map
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("user", "rr");
        map.put("url", "http://www.baidu.com/");
        map.put("name", "百度");

        // 创建一个模板对象
        Template t = new Template(null, new StringReader("用户名：${user};URL：    ${url};姓名： 　${name}"), null);
        //t.process(map, writer);
        StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
         //t.process(map, new OutputStreamWriter(System.out));
         t.process(map, stringBuilderWriter);
        System.out.println(stringBuilderWriter.toString());

    }
}
