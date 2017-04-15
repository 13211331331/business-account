/**
 * @className: ThreadPoolUtil
 * @description:TODO
 * @company: CCTVShow
 * @author: Zhouych
 * @createDate: 2015-9-8 下午8:50:24 
 * @updateUser: Zhouych
 * @updateDate: 2015-9-8 下午8:50:24 
 * @version: 1.0
 */
package cn.billionsfinance.businessaccount.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HuangHL
 * @date 2015/12/15
 * @see
 */
public class ThreadPool
{

    private static final int MAXSIZE = 1000;
    private static final int CORESIZE = 50;
    private static final int KEEPERTIME = 2;
    private static ThreadPoolExecutor executor;

    static
    {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        executor = new ThreadPoolExecutor(CORESIZE, MAXSIZE, KEEPERTIME, TimeUnit.SECONDS, queue);
    }

    public static void excute(Runnable runnable)
    {
        if (null != runnable)
        {
            executor.execute(runnable);
        }
    }
}
