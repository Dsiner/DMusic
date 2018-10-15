package com.d.music.component.media;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.d.music.data.Constants;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.transfer.manager.Transfer;
import com.d.music.utils.FileUtil;

import java.io.File;

/**
 * HitTagget
 * Created by D on 2018/8/23.
 */
public class HitTarget {

    @NonNull
    public static String hitLrc(MusicModel model) {
        final String filePostfix = Transfer.PREFIX_LRC;
        String tempPath = "";
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = model.lrcUrl;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = model.fileFolder + File.separator + model.songName + filePostfix;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = Constants.Path.lyric + model.songName + filePostfix;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = Constants.Path.cache + model.songName + filePostfix;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = "";
        }
        return tempPath;
    }

    @NonNull
    public static String hitSong(MusicModel model) {
        final String filePostfix = !TextUtils.isEmpty(model.filePostfix) ? "." + model.filePostfix
                : Transfer.PREFIX_SONG;
        String tempPath = "";
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = model.songUrl;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = model.fileFolder + File.separator + model.songName + filePostfix;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = Constants.Path.download + model.songName + filePostfix;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = Constants.Path.cache + model.songName + filePostfix;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = "";
        }
        return tempPath;
    }

    public static boolean secondPassSong(MusicModel model) {
        final String filePostfix = !TextUtils.isEmpty(model.filePostfix) ? "." + model.filePostfix
                : Transfer.PREFIX_SONG;
        final String oldPath = Constants.Path.cache + model.songName + filePostfix;
        final String destPath = Constants.Path.download + model.songName + filePostfix;
        if (!TextUtils.isEmpty(destPath) && FileUtil.isFileExist(destPath)) {
            return true;
        }
        if (!TextUtils.isEmpty(oldPath) && FileUtil.isFileExist(oldPath)) {
            // File movement only
            FileUtil.renameFile(oldPath, destPath);
            return true;
        }
        return false;
    }

    public static boolean secondPassMV(MusicModel model) {
        final String filePostfix = Transfer.PREFIX_MV;
        final String destPath = Constants.Path.mv + model.songName + filePostfix;
        return !TextUtils.isEmpty(destPath) && FileUtil.isFileExist(destPath);
    }
}
