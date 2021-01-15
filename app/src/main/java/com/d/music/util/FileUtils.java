package com.d.music.util;

import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.d.music.component.media.media.MusicFactory;
import com.d.music.local.model.FileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by D on 2017/4/30.
 */
public class FileUtils {

    public static String getRootPath() {
        if (MemoryUtils.isExternalStorageMounted()) {
            return Environment.getExternalStorageDirectory().getPath();
        } else {
            return Environment.getRootDirectory().getPath();
        }
    }

    public static String getParentPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        int index = path.lastIndexOf("/");
        if (index != -1) {
            return path.substring(0, path.lastIndexOf("/"));
        }
        return path;
    }

    /**
     * 获取文件夹列表
     *
     * @param path      path
     * @param withCount 是否同步获取该路径下歌曲数
     * @return 文件夹列表
     */
    @NonNull
    public static List<FileModel> getFiles(String path, boolean withCount) {
        List<FileModel> models = new ArrayList<>();
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            // 如果当前path不存在，或不是路径，直接返回空的models
            return models;
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory() && !f.isHidden()) {
                    FileModel model = new FileModel();
                    String p = f.getAbsolutePath();
                    model.absolutePath = p;
                    model.name = p.substring(p.lastIndexOf("/") + 1);
                    model.type = FileModel.TYPE_DIR;

                    File[] fs = f.listFiles();
                    model.isEmptyDir = fs == null || fs.length <= 0;

                    if (withCount) {
                        model.count = getMusicCount(p);
                    }
                    models.add(model);
                }
            }
        }
        return models;
    }

    public static boolean isEndPath(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return true;
        }
        int count = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    count++;
                }
            }
        }
        return count == 0;
    }

    public static List<FileModel> getFilterFiles(List<String> paths) {
        List<FileModel> models = new ArrayList<>();
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) {
                continue;
            }
            if (!file.isDirectory()) {
                FileModel m = getFilterFile(file.getAbsolutePath());
                if (m != null) {
                    models.add(m);
                }
            } else {
                List<FileModel> ms = getFilterFiles(path);
                if (ms != null && ms.size() > 0) {
                    models.addAll(ms);
                }
            }
        }
        return models;
    }

    /**
     * 获取目标文件
     */
    private static List<FileModel> getFilterFiles(String dir) {
        List<FileModel> models = null;
        File file = new File(dir);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    List<FileModel> ms = getFilterFiles(f.getAbsolutePath());
                    if (ms != null && ms.size() > 0) {
                        if (models == null) {
                            models = new ArrayList<>();
                        }
                        models.addAll(ms);
                    }
                } else {
                    FileModel m = getFilterFile(f.getAbsolutePath());
                    if (m != null) {
                        if (models == null) {
                            models = new ArrayList<>();
                        }
                        models.add(m);
                    }
                }
            }
        }
        return models;
    }

    private static FileModel getFilterFile(String path) {
        FileModel model = null;
        if (MusicFactory.Media.endsWith(path)) {
            model = new FileModel();
            model.absolutePath = path;
            model.name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
            model.postfix = path.substring(path.lastIndexOf(".") - 1);
            model.type = FileModel.TYPE_FILTER;
        }
        return model;
    }

    /**
     * 获取当前路径下歌曲数
     */
    public static int getMusicCount(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return 0;
        }
        return getMusicCountLoop(path);
    }

    private static int getMusicCountLoop(String path) {
        int count = 0;
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                String p = f.getAbsolutePath();
                if (f.isDirectory()) {
                    count += getMusicCountLoop(p);
                } else {
                    if (MusicFactory.Media.endsWith(p)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static boolean renameFile(String path, String dest) {
        File oldFile = new File(path);
        File destFile = new File(dest);
        return oldFile.exists() && !destFile.exists() && oldFile.renameTo(destFile);
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.exists() && file.delete();
    }
}