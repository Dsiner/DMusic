package com.d.lib.common.util.log;

import android.text.TextUtils;
import android.util.Log;

/**
 * Log tool, you can print the log class name, method name, line number
 */
public class ULog {

    private static String LOG_TAG = "ULog";

    /**
     * Debug switch
     */
    private static boolean DEBUG = true;

    public static void setDebug(boolean debug, String tag) {
        DEBUG = debug;
        LOG_TAG = tag;
    }

    private ULog() {
    }

    public static void v(String tag, String msg) {
        if (!DEBUG || TextUtils.isEmpty(tag) && TextUtils.isEmpty(msg)) {
            return;
        }
        println(Log.VERBOSE, tag + " " + msg);
    }

    public static void v(String msg) {
        if (!DEBUG || TextUtils.isEmpty(msg)) {
            return;
        }
        println(Log.VERBOSE, msg);
    }

    public static void d(String tag, String msg) {
        if (!DEBUG || TextUtils.isEmpty(tag) && TextUtils.isEmpty(msg)) {
            return;
        }
        println(Log.DEBUG, tag + " " + msg);
    }

    public static void d(String msg) {
        if (!DEBUG || TextUtils.isEmpty(msg)) {
            return;
        }
        println(Log.DEBUG, msg);
    }

    public static void i(String tag, String msg) {
        if (!DEBUG || TextUtils.isEmpty(tag) && TextUtils.isEmpty(msg)) {
            return;
        }
        println(Log.INFO, tag + " " + msg);
    }

    public static void i(String msg) {
        if (!DEBUG || TextUtils.isEmpty(msg)) {
            return;
        }
        println(Log.INFO, msg);
    }

    public static void w(String tag, String msg) {
        if (!DEBUG || TextUtils.isEmpty(tag) && TextUtils.isEmpty(msg)) {
            return;
        }
        println(Log.WARN, tag + " " + msg);
    }

    public static void w(String msg) {
        if (!DEBUG || TextUtils.isEmpty(msg)) {
            return;
        }
        println(Log.WARN, msg);
    }

    public static void e(String tag, String msg) {
        if (!DEBUG || TextUtils.isEmpty(tag) && TextUtils.isEmpty(msg)) {
            return;
        }
        println(Log.ERROR, tag + " " + msg);
    }

    public static void e(String msg) {
        if (!DEBUG || TextUtils.isEmpty(msg)) {
            return;
        }
        println(Log.ERROR, msg);
    }

    private static void println(int priority, String msg) {
        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 2;
        final StackTraceElement ste = stack[i];
        Log.println(priority, LOG_TAG, String.format("[%s][%s][%s]%s",
                ste.getFileName(),
                ste.getMethodName(),
                ste.getLineNumber(),
                msg));
    }

    /**
     * Print information about the current thread
     */
    public static void printThread(final String tag) {
        d(tag + " Current thread--> Id: " + Thread.currentThread().getId()
                + " Name: " + Thread.currentThread().getName());
    }
}
