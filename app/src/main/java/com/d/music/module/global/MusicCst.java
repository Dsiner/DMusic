package com.d.music.module.global;

import com.d.music.R;
import com.d.music.module.greendao.music.base.MusicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * MusicCst
 * Created by D on 2017/4/29.
 */
public class MusicCst {
    /**
     * 播放器模式
     */
    public static final int PLAYER_MODE_NORMAL = 0;//普通模式
    public static final int PLAYER_MODE_MINIMALIST = 1;//极简模式
    public static final int PLAYER_MODE_NOTIFICATION = 2;//通知栏模式

    /**
     * 当前播放列表播放模式
     */
    public static final int PLAY_MODE_ALL_REPEAT = 0;//列表循环
    public static final int PLAY_MODE_ORDER = 1;//顺序播放
    public static final int PLAY_MODE_SHUFFLE = 2;//随机播放
    public static final int PLAY_MODE_SINGLE_CYCLE = 3;//单曲循环
    public static final int[] PLAY_MODE_DRAWABLE = {R.drawable.ic_play_all_repeat, R.drawable.ic_play_order,
            R.drawable.ic_play_shuffle, R.drawable.ic_play_single_cycle};
    public static final String[] PLAY_MODE = {"列表循环", "顺序播放", "随机播放", "单曲循环"};

    /**
     * 当前播放状态
     */
    public static final int PLAY_STATUS_STOP = 0x0;//停止
    public static final int PLAY_STATUS_PLAYING = 0x1;//正在播放
    public static final int PLAY_STATUS_PAUSE = 0x2;//暂停

    /**
     * 播放控制标志
     */
    public static final int PLAY_FLAG_PLAY_PAUSE = 0x1;//播放/暂停
    public static final int PLAY_FLAG_NEXT = 0x2;//下一首
    public static final int PLAY_FLAG_PRE = 0x3;//上一首
    public static final int PLAY_FLAG_EXIT = 0x4;//上一首

    public static final String PLAYER_RELOAD = "com.d.music.action.player_control_reload";//从歌曲列表点中歌曲播放
    public static final String PLAYER_CONTROL_PLAY_PAUSE = "com.d.music.action.player_control_play_pause";//播放/暂停
    public static final String PLAYER_CONTROL_NEXT = "com.d.music.action.player_control_next";//下一首
    public static final String PLAYER_CONTROL_PREV = "com.d.music.action.player_control_prev";//上一首
    public static final String PLAYER_CONTROL_EXIT = "com.d.music.action.player_control_exit";//退出应用
    public static final String PLAYER_CONTROL_TIMING = "com.d.music.action.player_control_timing";//睡眠定时

    public static final String MUSIC_CURRENT_POSITION = "com.d.music.action.music_current_position";//用来接收service的广播
    public static final String MUSIC_SEEK_TO_TIME = "com.d.music.action.music_seek_to_time";//用来发送SeekBar进度改变广播，播放时间跳转

    public static final String TAG_EXIT = "tag_exit";

    public static final String SKIN_NAME = "dmusicskin_";//dmusicskin_0、dmusicskin_1、dmusicskin_2...dmusicskin__n皮肤包如此命名
    public static final String SKIN_NAME_POSTFIX = ".skin";//皮肤包拼接后缀名
    public static final int SKIN_COUNT = 18;//皮肤包总数目

    /**
     * variable
     */
    public static int playerMode = 0;//当前播放器模式
    public static List<MusicModel> models = new ArrayList<MusicModel>();//歌曲排序、管理用，全局静态变量
}
