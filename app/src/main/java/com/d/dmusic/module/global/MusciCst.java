package com.d.dmusic.module.global;

import com.d.dmusic.R;
import com.d.dmusic.module.greendao.music.base.MusicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * MusciCst
 * Created by D on 2017/4/29.
 */
public class MusciCst {
    /**
     * 当前播放列表播放模式
     */
    public static final int PLAY_MODE_ALL_REPEAT = 0;//列表循环
    public static final int PLAY_MODE_ORDER = 1;//顺序播放
    public static final int PLAY_MODE_SHUFFLE = 2;//随机播放
    public static final int PLAY_MODE_SINGLE_CYCLE = 3;//单曲循环
    public static final int PLAY_MODE_DRAWABLE[] = {R.drawable.play_mode_all_repeat, R.drawable.play_mode_order,
            R.drawable.play_mode_shuffle, R.drawable.play_mode_single_cycle};
    public static final String PLAY_MODE[] = {"列表循环", "顺序播放", "随机播放", "单曲循环"};

    /**
     * 当前播放状态
     */
    public static final int PLAY_STATUS_STOP = 0x0;//无
    public static final int PLAY_STATUS_PLAYING = 0x1;//正在播放
    public static final int PLAY_STATUS_PAUSE = 0x2;//暂停

    /**
     * 播放控制标志
     */
    public static final int PLAY_FLAG_PLAY_PAUSE = 0x1;//播放/暂停
    public static final int PLAY_FLAG_NEXT = 0x2;//下一首
    public static final int PLAY_FLAG_PRE = 0x3;//上一首

    public static final String PLAYER_RELOAD = "com.d.dmusic.action.player_control_reload";//从歌曲列表点中歌曲播放
    public static final String PLAYER_CONTROL_PLAY_PAUSE = "com.d.dmusic.action.player_control_play_pause";//播放/暂停
    public static final String PLAYER_CONTROL_NEXT = "com.d.dmusic.action.player_control_next";//下一首
    public static final String PLAYER_CONTROL_PREV = "com.d.dmusic.action.player_control_prev";//上一首

    public static final String MUSIC_CURRENT_INFO = "com.d.dmusic.action.music_current_info";//用来接收service的广播,更新当前正在播放歌曲信息
    public static final String MUSIC_CURRENT_POSITION = "com.d.dmusic.action.music_current_position";//用来接收service的广播
    public static final String MUSIC_SEEK_TO_TIME = "com.d.dmusic.action.music_seek_to_time";//用来发送SeekBar进度改变广播，播放时间跳转

    /**
     * variable
     */
    public static int playerMode;// 音乐播放器模式，0:普通模式，1：极简模式，2：通知栏模式
    public static List<MusicModel> models = new ArrayList<>();
}
