package com.d.lib.common.util;

public class DateUtils {

    private DateUtils() {
    }

    /**
     * Format time, convert milliseconds into seconds: (00:00) format
     */
    public static String formatTime(int time) {
        StringBuilder sb;
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;
        if (min / 10 < 1) {
            sb = new StringBuilder("0");
            sb.append(String.valueOf(min));
        } else {
            sb = new StringBuilder(String.valueOf(min));
        }
        sb.append(":");
        if (sec / 10 < 1) {
            sb.append("0");
        }
        sb.append(String.valueOf(sec));
        return sb.toString();
    }
}
