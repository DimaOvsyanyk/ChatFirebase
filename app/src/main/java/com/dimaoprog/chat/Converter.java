package com.dimaoprog.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converter {

    public static String dateToString(long time) {
        Date date = new Date();
        date.setTime(time);
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

}