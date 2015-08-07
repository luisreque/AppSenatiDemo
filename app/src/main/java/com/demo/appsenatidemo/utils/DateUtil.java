package com.demo.appsenatidemo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Equipo on 02/08/2015.
 */
public class DateUtil {

    public static String formatDateYY_MM_DD(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String aux = c.get(Calendar.YEAR)+"-"+completeValue(c.get(Calendar.MONTH)+1)+"-"+
                completeValue(c.get(Calendar.DAY_OF_MONTH));

        return aux;
    }

    public static String  completeValue(int num)
    {
        if(num<10)return "0"+num;
        return String.valueOf(num);
    }


    public static String formatHora(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String aux = c.get(Calendar.HOUR_OF_DAY)+1+":"+
                completeValue(c.get(Calendar.MINUTE));
        return aux;
    }
}
