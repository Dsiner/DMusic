package com.d.music.module.media.media;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.d.lib.common.utils.log.ULog;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.module.media.SyncManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * MusicFactory
 * Created by D on 2017/5/2.
 */
public class MusicFactory {
    private Context context;

    private MusicFactory(Context context) {
        this.context = context;
    }

    public static MusicFactory createFactory(Context context) {
        return new MusicFactory(context);
    }

    @NonNull
    public List<MusicModel> query(List<String> paths) {
        if (paths == null || paths.size() <= 0) {
            return new ArrayList<>();
        }
        return Media.query(context, paths);
    }

    @Deprecated
    public List<MusicModel> getMusic(List<String> paths) {
        if (paths == null) {
            return null;
        }
        List<MusicInfo> infos = Media.getMusicInfos(context);
        if (infos == null || infos.size() == 0) {
            return new ArrayList<>();
        }
        List<MusicModel> list = new ArrayList<>();
        for (String p : paths) {
            getMusic(infos, list, p);
        }
        return list;
    }

    @Deprecated
    private void getMusic(@NonNull List<MusicInfo> infos, @NonNull List<MusicModel> list, @NonNull String path) {
        HashMap<String, MusicModel> collections = SyncManager.getCollections(context);
        int size = infos.size();
        for (int i = 0; i < size; i++) {
            MusicInfo info = infos.get(i);
            String url = info.url;
            ULog.d("url" + url);
            ULog.d("path" + path);
            if (url.length() > path.length()) {
                // 加"/"表明为路径
                if (url.substring(0, path.length() + 1).equals(path + "/")) {
                    String displayName = info.displayName;
                    // 文件格式过滤，MediaPlayer对其他格式支持不好（例如.wma）
                    if (Media.endsWith(displayName)) {
                        MusicModel model = new MusicModel();
                        model.songName = displayName.substring(0, displayName.lastIndexOf("."));
                        model.artistName = info.artist;
                        model.albumName = info.album;
                        model.fileDuration = info.duration;
                        model.fileSize = info.size;
                        model.filePostfix = displayName.substring(displayName.lastIndexOf("."));
                        model.url = info.url;
                        model.fileFolder = info.url.substring(0, info.url.lastIndexOf("/"));
                        model.timeStamp = System.currentTimeMillis();
                        MusicModel collect = collections.get(model.url);
                        if (collect != null) {
                            model.isCollected = collect.isCollected;
                        }
                        list.add(model);
                    }
                }
            }
        }
    }

    public static class Media {
        static HashSet<String> formatSet = new HashSet<>();

        static {
            formatSet.add(".mp3");
            formatSet.add(".wav");
            formatSet.add(".ape");
        }

        public static boolean endsWith(String url) {
            if (url == null) {
                return false;
            }
            int index = url.lastIndexOf(".");
            return index != -1 && formatSet.contains(url.substring(index));
        }

        public static boolean matchPath(List<String> paths, @NonNull String url) {
            if (paths == null || paths.size() <= 0) {
                return true;
            }
            int count = paths.size();
            for (int i = 0; i < count; i++) {
                String path = paths.get(i);
                if (url.length() > path.length()
                        && url.substring(0, path.length() + 1).equals(path + "/")) {
                    return true;
                }
            }
            return false;
        }

        @NonNull
        public static List<MusicModel> queryAll(Context context) {
            return query(context, null);
        }

        @NonNull
        public static List<MusicModel> query(Context context, List<String> paths) {
            List<MusicModel> datas = new ArrayList<>();
            HashMap<String, MusicModel> collections = SyncManager.getCollections(context);
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐
                    if (isMusic != 0) {
                        // 只把音乐添加到集合当中
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐id
                        String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))); // 音乐标题
                        String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));// 显示名称
                        long artistId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));// 艺术家ID
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)); // 艺术家
                        long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));// 专辑ID
                        String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)); // 专辑
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)); // 时长
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径

                        if (Media.endsWith(displayName) && Media.matchPath(paths, url)) {
                            MusicModel model = new MusicModel();
                            model.type = MusicModel.TYPE_LOCAL;
                            model.url = url;
                            model.songId = "" + id;
                            model.songName = displayName.substring(0, displayName.lastIndexOf("."));
                            model.artistId = "" + artistId;
                            model.artistName = artist;
                            model.albumId = "" + albumId;
                            model.albumName = album;
                            model.fileDuration = duration;
                            model.fileSize = size;
                            model.filePostfix = displayName.substring(displayName.lastIndexOf("."));
                            model.fileFolder = url.substring(0, url.lastIndexOf("/"));
                            model.timeStamp = System.currentTimeMillis();
                            MusicModel collect = collections.get(model.url);
                            if (collect != null) {
                                model.isCollected = collect.isCollected;
                            }
                            datas.add(model);
                        }
                    }
                } while (cursor.moveToNext());
                cursor.close();
                return datas;
            }
            if (cursor != null) {
                cursor.close();
            }
            return datas;
        }

        @Deprecated
        public static List<MusicInfo> getMusicInfos(Context context) {
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                List<MusicInfo> infos = new ArrayList<>();
                do {
                    int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐
                    if (isMusic != 0) {
                        // 只把音乐添加到集合当中
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐id
                        String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))); // 音乐标题
                        String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));// 显示名称
                        long artistId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));// 艺术家ID
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)); // 艺术家
                        long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));// 专辑ID
                        String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)); // 专辑
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)); // 时长
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径

                        MusicInfo info = new MusicInfo();
                        info.url = url;
                        info.id = id;
                        info.title = title;
                        info.displayName = displayName;
                        info.artistId = artistId;
                        info.artist = artist;
                        info.albumId = albumId;
                        info.album = album;
                        info.duration = duration;
                        info.size = size;
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
}
