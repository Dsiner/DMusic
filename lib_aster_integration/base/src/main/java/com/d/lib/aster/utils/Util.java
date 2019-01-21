package com.d.lib.aster.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Util
 * Created by D on 2017/10/25.
 */
public class Util {
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Print the thread information of the current code
     */
    public static void printThread(String tag) {
        ULog.d(tag + " current thread--> id: " + Thread.currentThread().getId()
                + " name: " + Thread.currentThread().getName());
    }

    /**
     * Get the first generic type, interface only
     */
    public static <T> Class<T> getFirstCls(T t) {
        Type[] types = t.getClass().getGenericInterfaces();
        Type[] params = ((ParameterizedType) types[0]).getActualTypeArguments();
        Class<T> reponseClass = (Class) params[0];
        return reponseClass;
    }

    /**
     * Delete file/folder
     */
    public static void deleteFile(File file) {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length <= 0) {
                return;
            }
            for (File f : files) {
                deleteFile(f);
            }
            // If you want to keep the folder, just delete the file, please comment this line
            file.delete();
        } else if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Closes {@code closeable}, ignoring any checked exceptions. Does nothing if {@code closeable} is
     * null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    public static void executeMain(@NonNull Runnable r) {
        mainHandler.post(r);
    }
}
