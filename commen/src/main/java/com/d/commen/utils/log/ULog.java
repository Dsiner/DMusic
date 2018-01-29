package com.d.commen.utils.log;

import android.text.TextUtils;
import android.util.Log;

/**
 * Log工具类，可以打印Log日志类名，方法名，行号
 */
public class ULog {
    public static boolean DEVELOP_MODE = true;

    private static final String LOG_TAG = "DMusic";
    private static ULog log;

    public synchronized static ULog getInstance() {
        if (log == null)
            log = new ULog();
        return log;
    }

    private ULog() {
    }

    public static void v(String message) {
        if (DEVELOP_MODE) {
            if (TextUtils.isEmpty(message)) {
                return;
            }
            final StackTraceElement[] stack = new Throwable().getStackTrace();
            final int i = 1;
            final StackTraceElement ste = stack[i];
            Log.println(Log.VERBOSE, LOG_TAG, String.format("[%s][%s][%s]%s", ste.getFileName(), ste.getMethodName(), ste.getLineNumber(), message));
        }
    }

    public static void d(String message) {
        if (DEVELOP_MODE) {
            if (TextUtils.isEmpty(message)) {
                return;
            }
            final StackTraceElement[] stack = new Throwable().getStackTrace();
            final int i = 1;
            final StackTraceElement ste = stack[i];
            Log.println(Log.DEBUG, LOG_TAG, String.format("[%s][%s][%s]%s", ste.getFileName(), ste.getMethodName(), ste.getLineNumber(), message));
        }
    }


    public static void i(String message) {
        if (DEVELOP_MODE) {
            if (TextUtils.isEmpty(message)) {
                return;
            }
            final StackTraceElement[] stack = new Throwable().getStackTrace();
            final int i = 1;
            final StackTraceElement ste = stack[i];
            Log.println(Log.INFO, LOG_TAG, String.format("[%s][%s][%s]%s", ste.getFileName(), ste.getMethodName(), ste.getLineNumber(), message));
        }
    }


    public static void w(String message) {
        if (DEVELOP_MODE) {
            if (TextUtils.isEmpty(message)) {
                return;
            }
            final StackTraceElement[] stack = new Throwable().getStackTrace();
            final int i = 1;
            final StackTraceElement ste = stack[i];
            Log.println(Log.WARN, LOG_TAG, String.format("[%s][%s][%s]%s", ste.getFileName(), ste.getMethodName(), ste.getLineNumber(), message));
        }
    }

    public static void e(String message) {
        if (DEVELOP_MODE) {
            if (TextUtils.isEmpty(message)) {
                return;
            }
            final StackTraceElement[] stack = new Throwable().getStackTrace();
            final int i = 1;
            final StackTraceElement ste = stack[i];
            Log.println(Log.ERROR, LOG_TAG, String.format("[%s][%s][%s]%s", ste.getFileName(), ste.getMethodName(), ste.getLineNumber(), message));
        }
    }
}
