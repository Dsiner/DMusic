package com.d.music.commen;

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
        return settings.getBoolean("isFirst", true);
    }

    /**
     * 设置是否皮肤包加载成功
     */
    public void putSkinLoaded(boolean isFirst) {
        editor.putBoolean("isSkinLoaded", isFirst);
        save();
    }

    /**
     * 获取是否皮肤包加载成功
     */
    public boolean getSkinLoaded() {
        return settings.getBoolean("isSkinLoaded", false);
    }

    /**
     * 设置音乐播放器模式
     * 音乐播放器模式，0：普通模式，1：极简模式，2：通知栏模式
     */
    public void putPlayerMode(int playerMode) {
        editor.putInt("playerMode", playerMode);
        save();
    }

    /**
     * 获取音乐播放器模式
     * 音乐播放器模式，0：普通模式，1：极简模式，2：通知栏模式
     */
    public int getPlayerMode() {
        return settings.getInt("playerMode", 0);
    }

    /**
     * 设置是否启动时自动播放
     */
    public void putIsAutoPlay(boolean isAutoPlay) {
        editor.putBoolean("isAutoPlay", isAutoPlay);
        save();
    }

    /**
     * 获取是否启动时自动播放
     */
    public boolean getIsAutoPlay() {
        return settings.getBoolean("isAutoPlay", true);
    }

    /**
     * 设置上次退出应用时的播放位置
     */
    public void putLastPlayPosition(int position) {
        editor.putInt("lastPlayPosition", position);
        save();
    }

    /**
     * 获取上次退出应用时的播放位置
     */
    public int getLastPlayPosition() {
        return settings.getInt("lastPlayPosition", 0);
    }

    /**
     * 设置当前列表播放模式
     * 播放模式，0：列表循环，1：顺序播放，2：随机播放，3：单曲循环
     */
    public void putPlayMode(int playerMode) {
        editor.putInt("playMode", playerMode);
        save();
    }

    /**
     * 获取当前列表播放模式
     * 播放模式，0：列表循环，1：顺序播放，2：随机播放，3：单曲循环
     */
    public int getPlayMode() {
        return settings.getInt("playMode", 0);
    }

    /**
     * 设置歌曲操作菜单是否是下拉显示模式
     */
    public void putIsSubPull(boolean isPull) {
        editor.putBoolean("setting_isSubPullDown", isPull);
        save();
    }

    /**
     * 获取歌曲操作菜单是否是下拉显示模式
     */
    public boolean getIsSubPull() {
        return settings.getBoolean("setting_isSubPullDown", false);
    }

    /**
     * 设置是否显示新建歌单
     */
    public void putIsShowAdd(boolean isShow) {
        editor.putBoolean("setting_isShowAdd", isShow);
        save();
    }

    /**
     * 获取是否显示新建歌单
     */
    public boolean getIsShowAdd() {
        return settings.getBoolean("setting_isShowAdd", true);
    }

    /**
     * 设置是否头像/封面自动旋转
     */
    public void putIsAlbumRotate(boolean isRotate) {
        editor.putBoolean("setting_isAlbumRotate", isRotate);
        save();
    }

    /**
     * 获取是否头像/封面自动旋转
     */
    public boolean getIsAlbumRotate() {
        return settings.getBoolean("setting_isAlbumRotate", true);
    }

    /**
     * 设置是否晃动手机切歌
     */
    public void putIsShake(boolean isShake) {
        editor.putBoolean("setting_isShake", isShake);
        save();
    }

    /**
     * 获取是否晃动手机切歌
     */
    public boolean getIsShake() {
        return settings.getBoolean("setting_isShake", true);
    }

    /**
     * 设置是否显示首页弹出菜单按钮
     */
    public void putIsShowMenuIcon(boolean isShow) {
        editor.putBoolean("setting_isShowMenuIcon", isShow);
        save();
    }

    /**
     * 获取是否显示首页弹出菜单按钮
     */
    public boolean getIsShowMenuIcon() {
        return settings.getBoolean("setting_isShowMenuIcon", true);
    }

    /**
     * 设置签名 - singular
     */
    public void putSignature(String signature) {
        editor.putString("setting_signature", signature);
        save();
    }

    /**
     * 获取签名 - singular
     */
    public String getSignature() {
        return settings.getString("setting_signature", "畅听");
    }

    /**
     * 设置简笔 - be simple or wonderful
     */
    public void putStroke(String signature) {
        editor.putString("setting_stroke", signature);
        save();
    }

    /**
     * 获取简笔 - be simple or wonderful
     */
    public String getStroke() {
        return settings.getString("setting_stroke", "畅音乐,享自由");
    }

    /**
     * 设置皮肤
     */
    public void putSkin(int type) {
        editor.putInt("skin_type", type);
        save();
    }

    /**
     * 获取皮肤
     */
    public int getSkin() {
        return settings.getInt("skin_type", -1);
    }

    /**
     * 设置睡眠定时类型
     */
    public void putSleepType(int skin) {
        editor.putInt("sleep_type", skin);
        save();
    }

    /**
     * 获取睡眠定时类型
     */
    public int getSleepType() {
        return settings.getInt("sleep_type", 0);
    }

    /**
     * 设置睡眠定时自定义时间
     */
    public void putSleepTiming(long time) {
        editor.putLong("sleep_timing", time);
        save();
    }

    /**
     * 获取睡眠定时自定义时间
     */
    public long getSleepTiming() {
        return settings.getLong("sleep_timing", 0);
    }
}
