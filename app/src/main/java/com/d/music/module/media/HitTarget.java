package com.d.music.module.media;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.d.music.common.Constants;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.utils.FileUtil;

/**
 * HitTagget
 * Created by D on 2018/8/23.
 */
public class HitTarget {

    @NonNull
    public static String hitLrc(MusicModel model) {
        String tempPath = "";
        String filePostfix = ".lrc";
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = model.lrcUrl;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = model.fileFolder + "/" + model.songName + filePostfix;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = Constants.Path.lyric + model.songName + filePostfix;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = Constants.Path.cache + model.songName + filePostfix;
        }
        return tempPath;
    }

    @NonNull
    public static String hitSong(MusicModel model) {
        String tempPath = "";
        String filePostfix = !TextUtils.isEmpty(model.filePostfix) ? "." + model.filePostfix : ".mp3";
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = model.url;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = model.fileFolder + "/" + model.songName + filePostfix;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = Constants.Path.download + model.songName + filePostfix;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = Constants.Path.cache + model.songName + filePostfix;
        }
        return tempPath;
    }
}
