package com.d.dmusic.module.media;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * MediaUtil
 * Created by D on 2017/4/29.
 */
public class MediaUtil {
    public static String[] imageFormatSet = new String[]{".ape", ".mp3", ".wav"};

    public static List<MusicInfo> getMusicInfos(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            List<MusicInfo> infos = new ArrayList<>();
            do {
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐
                if (isMusic != 0) {
                    //只把音乐添加到集合当中
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐id
                    String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))); // 音乐标题
                    String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));// 显示名称
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)); // 艺术家
                    long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));// 专辑ID
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)); // 专辑
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)); // 时长
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径

                    MusicInfo info = new MusicInfo();
                    info.id = id;
                    info.title = title;
                    info.displayName = displayName;
                    info.artist = artist;
                    info.albumId = albumId;
                    info.album = album;
                    info.duration = duration;
                    info.size = size;
                    info.url = url;
                    infos.add(info);
                }
            } while (cursor.moveToNext());
            cursor.close();
            return infos;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}