package com.d.music.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by D on 2017/4/28.
 */
public class MemoryUtils {

    /**
     * Checks if external storage is mounted
     * 判断SD卡是否存在
     */
    public static boolean isExternalStorageMounted() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    // 获取内部存储器有用空间大小
    static public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    // 获取内部存储器空间总大小
    static public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    // 获取SD卡有用空间大小，错误返回-1
    static public long getAvailableExternalMemorySize() {
        if (isExternalStorageMounted()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    // 获取SD卡总空间大小，错误返回-1
    static public long getTotalExternalMemorySize() {
        if (isExternalStorageMounted()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }
}
