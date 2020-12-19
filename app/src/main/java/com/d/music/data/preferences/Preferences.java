package com.d.music.data.preferences;

import android.content.Context;

import com.d.lib.common.data.preference.AbstractPreference;
import com.d.music.App;
import com.d.music.R;

/**
 * Preferences
 * Created by D on 2017/4/29.
 */
public class Preferences extends AbstractPreference {
    private static Preferences instance;

    private Preferences(Context context) {
        super(context, "Preferences");
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

    /************************* 是/否 第一次启动 *************************/
    public void putIsFirst(boolean b) {
        mEditor.putBoolean(Keys.KEY_IS_FIRST, b);
        save();
    }

    /****************************** - Operation - ******************************/

    public boolean getIsFirst() {
        return mSettings.getBoolean(Keys.KEY_IS_FIRST, true);
    }

    /************************* 是/否 皮肤包加载成功 *************************/
    public void putIsSkinLoaded(boolean b) {
        mEditor.putBoolean(Keys.KEY_IS_SKIN_LOADED, b);
        save();
    }

    public boolean getIsSkinLoaded() {
        return mSettings.getBoolean(Keys.KEY_IS_SKIN_LOADED, false);
    }

    /**
     * 设置/获取 音乐播放器模式
     * 音乐播放器模式, 0: 普通模式; 1: 极简模式; 2: 通知栏模式
     */
    public void putPlayerMode(int mode) {
        mEditor.putInt(Keys.KEY_PLAYER_MODE, mode);
        save();
    }

    public int getPlayerMode() {
        return mSettings.getInt(Keys.KEY_PLAYER_MODE, 0);
    }

    /************************* 是/否 启动时自动播放 *************************/
    public void putIsAutoPlay(boolean b) {
        mEditor.putBoolean(Keys.KEY_IS_AUTO_PLAY, b);
        save();
    }

    public boolean getIsAutoPlay() {
        return mSettings.getBoolean(Keys.KEY_IS_AUTO_PLAY, true);
    }

    /************************* 设置/获取 上次退出应用时的播放位置 *************************/
    public void putLastPlayPosition(int position) {
        mEditor.putInt(Keys.KEY_LAST_PLAY_POSITION, position);
        save();
    }

    public int getLastPlayPosition() {
        return mSettings.getInt(Keys.KEY_LAST_PLAY_POSITION, 0);
    }

    /**
     * 设置/获取 当前列表播放模式
     * 播放模式, 0: 列表循环; 1: 顺序播放; 2: 随机播放; 3: 单曲循环
     */
    public void putPlayMode(int mode) {
        mEditor.putInt(Keys.KEY_PLAY_MODE, mode);
        save();
    }

    public int getPlayMode() {
        return mSettings.getInt(Keys.KEY_PLAY_MODE, 0);
    }

    /************************* 设置/获取 歌曲操作菜单是否是下拉显示模式 *************************/
    public void putIsSubPull(boolean b) {
        mEditor.putBoolean(Keys.KEY_IS_SETTING_SUB_PULL_DOWN, b);
        save();
    }

    public boolean getIsSubPull() {
        return mSettings.getBoolean(Keys.KEY_IS_SETTING_SUB_PULL_DOWN, false);
    }

    /************************* 是/否 显示新建歌单 *************************/
    public void putIsShowAdd(boolean b) {
        mEditor.putBoolean(Keys.KEY_IS_SETTING_SHOW_ADD, b);
        save();
    }

    public boolean getIsShowAdd() {
        return mSettings.getBoolean(Keys.KEY_IS_SETTING_SHOW_ADD, true);
    }

    /************************* 是/否 头像/封面自动旋转 *************************/
    public void putIsAlbumRotate(boolean b) {
        mEditor.putBoolean(Keys.KEY_IS_SETTING_ALBUM_ROTATE, b);
        save();
    }

    public boolean getIsAlbumRotate() {
        return mSettings.getBoolean(Keys.KEY_IS_SETTING_ALBUM_ROTATE, true);
    }

    /************************* 是/否 晃动手机切歌 *************************/
    public void putIsShake(boolean b) {
        mEditor.putBoolean(Keys.KEY_IS_SETTING_SHAKE, b);
        save();
    }

    public boolean getIsShake() {
        return mSettings.getBoolean(Keys.KEY_IS_SETTING_SHAKE, true);
    }

    /************************* 是/否 显示首页弹出菜单按钮 *************************/
    public void putIsShowMenuIcon(boolean b) {
        mEditor.putBoolean(Keys.KEY_IS_SETTING_SHOW_MENU_ICON, b);
        save();
    }

    public boolean getIsShowMenuIcon() {
        return mSettings.getBoolean(Keys.KEY_IS_SETTING_SHOW_MENU_ICON, true);
    }

    /************************* 设置/获取 签名 - singular *************************/
    public void putSignature(String content) {
        mEditor.putString(Keys.KEY_SETTING_SIGNATURE, content);
        save();
    }

    public String getSignature() {
        return mSettings.getString(Keys.KEY_SETTING_SIGNATURE,
                App.getContext().getResources().getString(R.string.module_common_signature_default));
    }

    /************************* 设置/获取 简笔 - Be simple or wonderful *************************/
    public void putStroke(String content) {
        mEditor.putString(Keys.KEY_SETTING_STROKE, content);
        save();
    }

    public String getStroke() {
        return mSettings.getString(Keys.KEY_SETTING_STROKE,
                App.getContext().getResources().getString(R.string.module_common_stroke_default));
    }

    /************************* 设置/获取 皮肤 *************************/
    public void putSkinType(int type) {
        mEditor.putInt(Keys.KEY_SKIN_TYPE, type);
        save();
    }

    public int getSkinType() {
        return mSettings.getInt(Keys.KEY_SKIN_TYPE, -1);
    }

    /************************* 设置/获取 睡眠定时类型 *************************/
    public void putSleepType(int type) {
        mEditor.putInt(Keys.KEY_SLEEP_TYPE, type);
        save();
    }

    public int getSleepType() {
        return mSettings.getInt(Keys.KEY_SLEEP_TYPE, 0);
    }

    /************************* 设置/获取 睡眠定时自定义时间 *************************/
    public void putSleepTiming(long time) {
        mEditor.putLong(Keys.KEY_SLEEP_TIMING, time);
        save();
    }

    public long getSleepTiming() {
        return mSettings.getLong(Keys.KEY_SLEEP_TIMING, 0);
    }

    /************************* 设置/获取 热门搜索 *************************/
    public void putSearchHot(String content) {
        mEditor.putString(Keys.KEY_SEARCH_HOT, content);
        save();
    }

    public String getSearchHot() {
        return mSettings.getString(Keys.KEY_SEARCH_HOT, "");
    }

    /************************* 设置/获取 搜索历史 *************************/
    public void putSearchHistory(String content) {
        mEditor.putString(Keys.KEY_SEARCH_HISTORY, content);
        save();
    }

    public String getSearchHistory() {
        return mSettings.getString(Keys.KEY_SEARCH_HISTORY, "");
    }

    public interface Keys {
        String KEY_IS_FIRST = "key_is_first";

        String KEY_IS_SKIN_LOADED = "key_is_skin_loaded";
        String KEY_PLAYER_MODE = "key_player_mode";
        String KEY_IS_AUTO_PLAY = "key_is_auto_play";
        String KEY_LAST_PLAY_POSITION = "key_last_play_position";
        String KEY_PLAY_MODE = "key_play_mode";
        String KEY_IS_SETTING_SUB_PULL_DOWN = "key_is_setting_sub_pull_down";
        String KEY_IS_SETTING_SHOW_ADD = "key_is_setting_show_add";

        String KEY_IS_SETTING_ALBUM_ROTATE = "key_is_setting_album_rotate";
        String KEY_IS_SETTING_SHAKE = "key_is_setting_shake";
        String KEY_IS_SETTING_SHOW_MENU_ICON = "key_is_setting_show_menu_icon";
        String KEY_SETTING_SIGNATURE = "key_setting_signature";
        String KEY_SETTING_STROKE = "key_setting_stroke";

        String KEY_SKIN_TYPE = "key_skin_type";
        String KEY_SLEEP_TYPE = "key_sleep_type";
        String KEY_SLEEP_TIMING = "key_sleep_timing";

        String KEY_SEARCH_HOT = "key_search_hot";
        String KEY_SEARCH_HISTORY = "key_search_history";
    }
}
