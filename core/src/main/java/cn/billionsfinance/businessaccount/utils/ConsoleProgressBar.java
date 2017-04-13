package cn.billionsfinance.businessaccount.utils;

import java.text.DecimalFormat;

/**
 * Created by hanlin.huang on 2017/4/10.
 */
public class ConsoleProgressBar {

    private char starChar = '[';
    private char endChar = ']';

    private long minimum = 0; // 进度条起始值

    private long maximum = 100; // 进度条最大值

    private long barLen = 100; // 进度条长度

    private char showChar = '>'; // 用于进度条显示的字符

    private char notCompleteChar = '='; // 用于未完成显示的字符

    private DecimalFormat formater = new DecimalFormat("#.##%");

    private String message = "";

    /**
     * 使用系统标准输出，显示字符进度条及其百分比。
     */
    public ConsoleProgressBar() {
    }

    /**
     * 使用系统标准输出，显示字符进度条及其百分比。
     *
     * @param minimum 进度条起始值
     * @param maximum 进度条最大值
     * @param barLen 进度条长度
     */
    public ConsoleProgressBar(long minimum, long maximum,
                              long barLen) {
        this(minimum, maximum, barLen, '>','=');
    }

    /**
     * 使用系统标准输出，显示字符进度条及其百分比。
     *
     * @param minimum 进度条起始值
     * @param maximum 进度条最大值
     * @param barLen 进度条长度
     * @param showChar 用于进度条显示的字符
     */
    public ConsoleProgressBar(long minimum, long maximum,
                              long barLen, char showChar,char notCompleteChar) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.barLen = barLen;
        this.showChar = showChar;
        this.notCompleteChar = notCompleteChar;
    }

    /**
     * 显示进度条。
     *
     * @param value 当前进度。进度必须大于或等于起始点且小于等于结束点（start <= current <= end）。
     */
    public void show(long value) {
        if (value < minimum || value > maximum) {
            return;
        }

        reset();
        minimum = value;
        float rate = (float) (minimum*1.0 / maximum);
        long len = (long) (rate * barLen);
        draw(len, rate);
        if (minimum == maximum) {
            afterComplete();
        }
    }

    /**
     * 显示进度条。
     *
     * @param value 当前进度。进度必须大于或等于起始点且小于等于结束点（start <= current <= end）。
     */
    public void show(long value,String message) {
        this.message = message;
        if (value < minimum || value > maximum) {
            return;
        }

        reset();
        minimum = value;
        float rate = (float) (minimum*1.0 / maximum);
        long len = (long) (rate * barLen);
        draw(len, rate);
        if (minimum == maximum) {
            afterComplete();
        }
    }

    private void draw(long len, float rate) {
        System.out.print(starChar);
        for (int i = 0; i < len; i++) {
            System.out.print(showChar);
        }
        for (long i = len+1; i <= barLen; i++) {
            System.out.print(notCompleteChar);
        }
        System.out.print(endChar);
        System.out.print( " " + format(rate) + " " + message);
    }

    private void reset() {
        System.out.print('\r');
    }

    private void afterComplete() {
        System.out.print('\n');
    }

    private String format(float num) {
        return formater.format(num);
    }

    public static void main(String[] args) throws InterruptedException {
        ConsoleProgressBar cpb = new ConsoleProgressBar(0, 12, 50, '#','=');
        for (int i = 1; i <= 60; i++) {
            cpb.show(i);
            Thread.sleep(100);
        }

        for (int i = 61; i <= 100; i++) {
            cpb.show(i,"正在出十分");
            cpb.show(i,"dsg");
            Thread.sleep(100);
        }
    }

    public long getBarLen() {
        return barLen;
    }

    public void setBarLen(long barLen) {
        this.barLen = barLen;
    }

    public char getShowChar() {
        return showChar;
    }

    public void setShowChar(char showChar) {
        this.showChar = showChar;
    }

    public long getMaximum() {
        return maximum;
    }

    public void setMaximum(long maximum) {
        this.maximum = maximum;
    }

    public long getMinimum() {
        return minimum;
    }

    public void setMinimum(long minimum) {
        this.minimum = minimum;
    }

    public char getEndChar() {
        return endChar;
    }

    public void setEndChar(char endChar) {
        this.endChar = endChar;
    }

    public char getStarChar() {
        return starChar;
    }

    public void setStarChar(char starChar) {
        this.starChar = starChar;
    }

    public char getNotCompleteChar() {
        return notCompleteChar;
    }

    public void setNotCompleteChar(char notCompleteChar) {
        this.notCompleteChar = notCompleteChar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
