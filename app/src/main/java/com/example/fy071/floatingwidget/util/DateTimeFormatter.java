package com.example.fy071.floatingwidget.util;

import java.util.Locale;

public class DateTimeFormatter {

    public static String dateFormatter(int year, int month, int day) {
        return String.valueOf(year) + "." + (month + 1) + "." + day;
    }

    public static String timeFormatter(int hour, int minute) {
        return String.format(Locale.CHINA, "%02d:%02d", hour, minute);
    }
}
