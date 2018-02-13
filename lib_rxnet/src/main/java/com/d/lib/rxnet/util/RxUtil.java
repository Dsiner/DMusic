package com.d.lib.rxnet.util;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Util
 * Created by D on 2017/10/25.
 */
public class RxUtil {

    /**
     * 打印当前代码所在线程信息
     */
    public static void printThread(String tag) {
        RxLog.d(tag + Thread.currentThread().getId() + "--NAME--" + Thread.currentThread().getName());
    }

    /**
     * 获取第一泛型类型 仅限Interface
     */
    public static <T> Class<T> getFirstCls(T t) {
        Type[] types = t.getClass().getGenericInterfaces();
        Type[] params = ((ParameterizedType) types[0]).getActualTypeArguments();
        Class<T> reponseClass = (Class) params[0];
        return reponseClass;
    }

    /**
     * 删除文件/文件夹
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
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }
}
