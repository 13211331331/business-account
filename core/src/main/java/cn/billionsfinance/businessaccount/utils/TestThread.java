package cn.billionsfinance.businessaccount.utils;

/**
 * Created by hanlin.huang on 2017/4/24.
 */
public class TestThread {
    public static void main(String[] args) {
        Thread thread = new Thread(){
            public void run() {
                try {
                    Thread.currentThread().sleep(10*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };

        };
        thread.setName("test111");
        thread.start();

        Thread thread1 = new Thread(){
            public void run() {
                try {
                    Thread.currentThread().sleep(20*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        };
        thread1.setName("test222");
        thread1.start();

        Thread thread2 = new Thread(){
            public void run() {
                try {
                    Thread.currentThread().sleep(30*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        };
        thread2.setName("test333");
        thread2.start();
      //  ThreadViewer.showThreads();

        AsynWorker.doAsynWork(new Object[]{}, new ThreadViewer(), "showThreads");
        System.out.println(222);
        System.out.println(3333);
    }
}
