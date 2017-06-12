package com.d.dmusic.module.utils;

import android.content.Context;

import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.media.SyncUtil;
import com.d.dmusic.mvp.activity.PlayActivity;
import com.d.dmusic.utils.Util;
import com.d.dmusic.view.dialog.SongInfoDialog;
import com.d.dmusic.view.popup.AddToListPopup;

import java.util.ArrayList;
import java.util.List;

/**
 * MoreUtil
 * Created by D on 2017/6/6.
 */
public class MoreUtil {
    /**
     * 添加到歌单
     *
     * @param context:context
     * @param model：model
     * @param type:当前列表标识-排除本列表
     */
    public static void addToList(Context context, MusicModel model, int type) {
        List<MusicModel> list = new ArrayList<>();
        list.add(model);
        new AddToListPopup(context, list, type).show();
    }

    /**
     * 收藏/取消收藏
     */
    public static void collect(Context context, MusicModel model, int type, boolean isTip) {
        model.isCollected = !model.isCollected;
        if (type != MusicDB.MUSIC) {
            PlayActivity.isNeedReLoad = true;//通知PlayActivity播放页刷新
        }
        SyncUtil.upCollected(context.getApplicationContext(), model, type);//数据库操作
        if (!isTip) {
            return;
        }
        if (model.isCollected) {
            Util.toast(context, "已收藏");
        } else {
            Util.toast(context, "已取消收藏");
        }
    }

    /**
     * 歌曲详情
     */
    public static void showInfo(Context context, MusicModel model) {
        new SongInfoDialog(context, model).show();
    }
}
