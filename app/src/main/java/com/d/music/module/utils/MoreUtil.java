package com.d.music.module.utils;

import android.content.Context;

import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.media.SyncUtil;
import com.d.music.mvp.activity.PlayActivity;
import com.d.music.utils.Util;
import com.d.music.view.dialog.SongInfoDialog;
import com.d.music.view.popup.AddToListPopup;

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
