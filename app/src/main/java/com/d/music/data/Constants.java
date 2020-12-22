package com.d.music.data;

import android.os.Environment;

import com.d.music.R;
import com.d.music.data.database.greendao.bean.MusicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Constants
 * Created by D on 2017/4/29.
 */
public class Constants {

    public static class Path {
        public static final String NAME = "DMusic";

        public static final String GLIDE_CACHE = "/image_cache";

        public static final String PATH = Environment.getExternalStorageDirectory().getPath()
                + "/" + NAME;
        public static final String LOG = PATH + "/log";
        public static final String CACHE = PATH + "/cache";
        public static final String DOWNLOAD = PATH + "/download";
        public static final String SONG = PATH + "/song";
        public static final String MV = PATH + "/mv";
        public static final String LYRIC = PATH + "/lyric";
    }

    /**
     * 播放器模式
     */
    public static class PlayerMode {
        public static final int PLAYER_MODE_NORMAL = 0; // 普通模式
        public static final int PLAYER_MODE_MINIMALIST = 1; // 极简模式
        public static final int PLAYER_MODE_NOTIFICATION = 2; // 通知栏模式

        public static int sPlayerMode = PLAYER_MODE_NORMAL; // 当前播放器模式
    }

    /**
     * 当前播放列表播放模式
     */
    public static class PlayMode {
        public static final int PLAY_MODE_ALL_REPEAT = 0; // 列表循环
        public static final int PLAY_MODE_ORDER = 1; // 顺序播放
        public static final int PLAY_MODE_SHUFFLE = 2; // 随机播放
        public static final int PLAY_MODE_SINGLE_CYCLE = 3; // 单曲循环

        public static final int[] PLAY_MODE_DRAWABLE = {R.drawable.module_play_ic_play_all_repeat,
                R.drawable.module_play_ic_play_order,
                R.drawable.module_play_ic_play_shuffle,
                R.drawable.module_play_ic_play_single_cycle};
        public static final int[] PLAY_MODE = {R.string.module_common_play_all_repeat,
                R.string.module_common_play_order,
                R.string.module_common_play_shuffle,
                R.string.module_common_play_single_cycle};
    }

    /**
     * 当前播放状态
     */
    public static class PlayStatus {
        public static final int PLAY_STATUS_STOP = 0; // 停止
        public static final int PLAY_STATUS_PLAYING = 1; // 正在播放
        public static final int PLAY_STATUS_PAUSE = 2; // 暂停
    }

    /**
     * 播放控制标志
     */
    public static class PlayFlag {
        public static final int PLAY_FLAG_PLAY_PAUSE = 1; // 播放/暂停
        public static final int PLAY_FLAG_NEXT = 2; // 下一首
        public static final int PLAY_FLAG_PRE = 3; // 上一首
        public static final int PLAY_FLAG_EXIT = 4; // 上一首

        public static final String PLAYER_RELOAD = "com.d.music.action.player_control_reload"; // 从歌曲列表点中歌曲播放
        public static final String PLAYER_CONTROL_PLAY_PAUSE = "com.d.music.action.player_control_play_pause"; // 播放/暂停
        public static final String PLAYER_CONTROL_NEXT = "com.d.music.action.player_control_next"; // 下一首
        public static final String PLAYER_CONTROL_PREV = "com.d.music.action.player_control_prev"; // 上一首
        public static final String PLAYER_CONTROL_EXIT = "com.d.music.action.player_control_exit"; // 退出应用
        public static final String PLAYER_CONTROL_TIMING = "com.d.music.action.player_control_timing"; // 睡眠定时
    }

    /**
     * 静态堆内存
     */
    public static class Heap {
        // 歌曲排序, 管理用
        public static List<MusicModel> sModels = new ArrayList<>();
    }
}
