package com.d.lib.rxnet.utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Util
 * Created by D on 2017/10/25.
 */
public class Util {

    /**
     * Print the thread information of the current code
     */
    public static void printThread(String tag) {
        ULog.d(tag + " current thread--> id: " + Thread.currentThread().getId() + " name: " + Thread.currentThread().getName());
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

    @SuppressLint("CheckResult")
    public static void executeMain(@NonNull Runnable r) {
        Observable.just(r).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Runnable>() {
            @Override
            public void accept(Runnable runnable) throws Exception {
                runnable.run();
            }
        });
    }
}
