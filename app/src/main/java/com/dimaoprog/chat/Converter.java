package com.dimaoprog.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Converter {

    private static final int CHAT_FORMAT = 0;
    private static final int MESSAGE_FORMAT = 1;

    public static String dateToStringForMessages(long time) {
        return new SimpleDateFormat(getPattern(time, MESSAGE_FORMAT), Locale.getDefault()).format(time);
    }

    public static String dateToStringForChat(long time) {
        return new SimpleDateFormat(getPattern(time, CHAT_FORMAT), Locale.getDefault()).format(time);
    }

    private static String getPattern(long time, int format) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return ((currentTime - time) < 24 * 60 * 60 *1000) ? "HH:mm" :
                format == MESSAGE_FORMAT ? "dd.MM.yyyy" : "dd.MM.yyyy HH:mm";
    }
}