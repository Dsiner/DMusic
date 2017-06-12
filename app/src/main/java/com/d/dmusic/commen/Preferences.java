package com.d.dmusic.commen;

import android.content.Context;

/**
 * Preferences
 * Created by D on 2017/4/29.
 */
public class Preferences extends AbstractPreference {
    private static Preferences instance;

    private Preferences(Context context) {
        super(context);
    }

    public static Preferences getInstance(Context context) {
        if (instance == null) {
            synchronized (Preferences.class) {
                if (instance == null) {
                    instance = new Preferences(context);
                }
            }
        }
        return instance;
    }

    private void save() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    /**
     * 设置是否是第一次启动
     */
    public void putIsFirst(boolean isFirst) {
        editor.putBoolean("isFirst", isFirst);
        save();
    }

    /**
     * 获取是否是第一次启动
     */
    public boolean getIsFirst() {
        return settings.getBoolean("isFirst", false);
    }

    /**
     * 设置音乐播放器模式
     */
    public void putPlayerMode(int playerMode) {
        editor.putInt("playerMode", playerMode);
        save();
    }

    /**
     * 获取音乐播放器模式
     * 音乐播放器模式，0:普通模式，1：极简模式，2：通知栏模式
     */
    public int getPlayerMode() {
        return settings.getInt("playerMode", 0);
    }

    /**
     * 设置当前列表播放模式
     * 音乐播放器模式，0:普通模式，1：极简模式，2：通知栏模式
     */
    public void putPlayMode(int playerMode) {
        editor.putInt("playMode", playerMode);
        save();
    }

    /**
     * 获取当前列表播放模式
     */
    public int getPlayMode() {
        return settings.getInt("playMode", 0);
    }

    /**
     * 设置歌曲菜单是否是下拉显示模式
     */
    public void putIsMenuPullMode(boolean isPullDown) {
        editor.putBoolean("isMenuPullDown", isPullDown);
        save();
    }

    /**
     * 获取歌曲菜单是否是下拉显示模式
     */
    public boolean getIsMenuPullDown() {
        return settings.getBoolean("isMenuPullDown", false);
    }
}
