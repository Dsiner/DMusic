package com.d.music.utils.fileutil;

import android.os.Environment;
import android.text.TextUtils;

import com.d.music.model.FileModel;
import com.d.music.module.media.MediaUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by D on 2017/4/30.
 */
public class FileUtil {

    public static String getRootPath() {
        if (MemoryStatus.isExternalStorageMounted()) {
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
     * @param path:path
     * @param withCount:是否同步获取该路径下歌曲数
     * @return models:文件夹列表
     */
    public static List<FileModel> getFiles(String path, boolean withCount) {
        List<FileModel> models = new ArrayList<FileModel>();
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return models;//如果当前path不存在，或不是路径，直接返回空的models
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    FileModel model = new FileModel();
                    String p = f.getAbsolutePath();
                    model.absolutePath = p;
                    model.name = p.substring(p.lastIndexOf("/") + 1);
                    model.type = FileModel.TYPE_DIR;

                    File[] fs = f.listFiles();
                    model.isEmptyDir = fs != null && fs.length != 0;

                    if (withCount) {
                        model.musicCount = getMusicCount(p);
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
        List<FileModel> models = new ArrayList<FileModel>();
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
                            models = new ArrayList<FileModel>();
                        }
                        models.addAll(ms);
                    }
                } else {
                    FileModel m = getFilterFile(f.getAbsolutePath());
                    if (m != null) {
                        if (models == null) {
                            models = new ArrayList<FileModel>();
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
        for (String format : MediaUtil.imageFormatSet) {
            if (path.endsWith(format)) {
                model = new FileModel();
                model.absolutePath = path;
                model.name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
                model.postfix = path.substring(path.lastIndexOf(".") - 1);
                model.type = FileModel.TYPE_FILTER;
            }
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
                    for (String format : MediaUtil.imageFormatSet) {
                        if (p.endsWith(format)) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * 创建文件
     */
    public static File createFile(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
        return file;
    }

    /**
     * 创建目录
     */
    public static File createDir(String path) {
        File dir = new File(path);
        dir.mkdir();
        return dir;
    }

    /**
     * 创建多层级目录
     */
    public static File createDirs(String path) {
        File dir = new File(path);
        dir.mkdirs();
        return dir;
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }
}