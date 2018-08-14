package com.d.music.common;

import android.os.Environment;

/**
 * Constants
 * Created by D on 2017/4/29.
 */
public class Constants {

    public static class Path {
        private final static String path = Environment.getExternalStorageDirectory().getPath() + "/DMusic/";
        public final static String log = path + "log/";
        public final static String cache = path + "cache/";
        public final static String glide_cache = "/image_cache";
        public final static String download = path + "download/";
        public final static String song = path + "song/";
        public final static String lyric = path + "lyric/";
    }
}
