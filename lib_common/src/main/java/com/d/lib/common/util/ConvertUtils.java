package com.d.lib.common.util;

import android.text.TextUtils;

import java.text.DecimalFormat;

public class ConvertUtils {
    private static final char[] HEX_DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Math.pow(...) is very expensive, so avoid calling it and create it
     * yourself.
     */
    private static final int POW_10[] = {
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000
    };

    private ConvertUtils() {
    }

    public static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) return "";
        int len = bytes.length;
        if (len <= 0) return "";
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = HEX_DIGITS[bytes[i] >> 4 & 0x0f];
            ret[j++] = HEX_DIGITS[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    /**
     * Returns the data size (byte) corresponding text
     */
    public static String formatSize(long size) {
        DecimalFormat format = new DecimalFormat("####.00");
        if (size < 1024) {
            return size + "bytes";
        } else if (size < 1024 * 1024) {
            float kb = size / 1024f;
            return format.format(kb) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mb = size / (1024 * 1024f);
            return format.format(mb) + "MB";
        } else {
            float gb = size / (1024 * 1024 * 1024f);
            return format.format(gb) + "GB";
        }
    }

    /**
     * Format a number properly with the given number of digits
     *
     * @param number the number to format
     * @param digits the number of digits
     */
    public static String formatDecimal(double number, int digits) {
        number = roundNumber((float) number, digits);
        StringBuffer a = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                a.append(".");
            a.append("0");
        }
        DecimalFormat nf = new DecimalFormat("###,###,###,##0" + a.toString());
        String formatted = nf.format(number);
        return formatted;
    }

    public static float roundNumber(float number, int digits) {
        try {
            if (digits == 0) {
                int r0 = (int) Math.round(number);
                return r0;
            } else if (digits > 0) {
                if (digits > 9)
                    digits = 9;
                StringBuffer a = new StringBuffer();
                for (int i = 0; i < digits; i++) {
                    if (i == 0)
                        a.append(".");
                    a.append("0");
                }
                DecimalFormat nf = new DecimalFormat("#" + a.toString());
                String formatted = nf.format(number);
                return Float.valueOf(formatted);
            } else {
                digits = -digits;
                if (digits > 9)
                    digits = 9;
                int r2 = (int) (number / POW_10[digits] + 0.5);
                return r2 * POW_10[digits];
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return number;
        }
    }

    public static boolean convertBoolean(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        if (TextUtils.equals(value, "1")) {
            return true;
        } else if (TextUtils.equals(value, "0")) {
            return false;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int convertInt(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0;
        }
        try {
            return Integer.parseInt(string);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long convertLong(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0;
        }
        try {
            return Long.parseLong(string);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static float convertFloat(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Float.parseFloat(value);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static double convertDouble(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Double.parseDouble(value);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }
}
