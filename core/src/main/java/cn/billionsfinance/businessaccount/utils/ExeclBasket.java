package cn.billionsfinance.businessaccount.utils;

import cn.billionsfinance.businessaccount.core.bean.BeanExcelExport;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hanlin.huang on 2017/4/14.
 */
public class ExeclBasket {
    // 篮子
    public static BlockingQueue<ArrayList<BeanExcelExport>> queue = null;

    // 生产，放入篮子
    public static void produce(ArrayList<BeanExcelExport> list) throws InterruptedException {
        if(queue == null){
            queue = new LinkedBlockingQueue<ArrayList<BeanExcelExport>>(ExportExcel2007.THREAD_NUMBER);
        }
        // put方法放入一个苹果，若basket满了，等到basket有位置
        queue.put(list);
    }

    // 消费，从篮子中取走
    public static ArrayList<BeanExcelExport> consume() throws InterruptedException {
        // take方法取出一个苹果，若basket为空，等到basket有苹果为止(获取并移除此队列的头部)
        if(queue != null){
            return queue.take();
        }
        return null;
    }
}
