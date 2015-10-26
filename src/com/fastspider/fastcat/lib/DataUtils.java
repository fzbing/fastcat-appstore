package com.fastspider.fastcat.lib;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtils {

    private static final DateFormat FORMATOR_TIME = new SimpleDateFormat(
            "HH:mm:ss");

    public static String getStringTime(Date date) {
        return FORMATOR_TIME.format(date);
    }

    /**
     * 0 ��ʾ��ĩ ��1-6��ʾ��һ������
     * 
     * @return
     */
    public static final int getCurrentDayOfWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }
}