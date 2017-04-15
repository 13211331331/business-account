package cn.billionsfinance.businessaccount.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hanlin.huang on 2017/4/15.
 */
public class DateDistance {

    public static void main(String[] arg){
        try {
            System.out.println(DateDistance.getDistanceDays("2017-01-12","2017-01-16"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            System.out.println(DateDistance.getDistanceTime("2017-01-12 12:34:54", "2017-01-16 03:54:44"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            long[] asr =  DateDistance.getDistanceTimes("2017-01-12 12:34:54", "2017-01-16 03:54:44");
            for(long a:asr){
                System.out.println(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Date d = new Date();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Date d1 = new Date();
        long[] asr1 = DateDistance.getDistanceTimes(d,d1);
        for(long a:asr1){
            System.out.println(a);
        }

    }
    /**
     * 两个时间之间相差距离多少天
     * @param
     * @param
     * @return 相差天数
     */
    public static long getDistanceDays(String str1, String str2) throws Exception{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date one;
        Date two;
        long days=0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};
        return times;
    }
    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String str1, String str2) {
        long[] times = getDistanceTimes(str1, str2);
        return times[0] + "天" + times[1] + "小时" + times[2] + "分" + times[3] + "秒";
    }


    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param  //时间参数 1 格式：1990-01-01 12:00:00
     * @param  //时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(Date one, Date two) {

        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;

        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff ;
        if(time1<time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff/1000-day*24*60*60-hour*60*60-min*60);

        long[] times = {day, hour, min, sec};
        return times;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param //str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param //str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(Date one, Date two) {
        long[] times = getDistanceTimes(one, two);
        return times[0] + " 天 " + times[1] + " 小时 " + times[2] + " 分 " + times[3] + " 秒";
    }


}
