package com.d.music.component.media;

import android.content.Context;

import com.d.lib.taskscheduler.TaskScheduler;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDB;
import com.d.music.data.database.greendao.util.AppDBUtil;
import com.d.music.event.eventbus.RefreshEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

/**
 * SyncManager
 * Created by D on 2017/5/9.
 */
public class SyncManager {

    /**
     * 取消收藏
     */
    public static void unCollected(final Context context, final List<MusicModel> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        TaskScheduler.executeTask(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    MusicModel item = list.get(i);
                    if (item == null) {
                        continue;
                    }
                    AppDBUtil.getIns(context).optMusic().updateColleted(item.url, false);
                }
                EventBus.getDefault().post(new RefreshEvent(AppDB.COLLECTION_MUSIC, RefreshEvent.SYNC_COLLECTIONG));
            }
        });
    }

    /**
     * 更新收藏状态
     */
    public static void updateCollected(final Context context, final int type, final MusicModel item) {
        TaskScheduler.executeTask(new Runnable() {
            @Override
            public void run() {
                if (item.isCollected) {
                    AppDBUtil.getIns(context).optMusic().insertOrReplace(AppDB.COLLECTION_MUSIC, item);
                    AppDBUtil.getIns(context).optMusic().updateColleted(item.url, true);
                } else {
                    AppDBUtil.getIns(context).optMusic().delete(AppDB.COLLECTION_MUSIC, item);
                    AppDBUtil.getIns(context).optMusic().updateColleted(item.url, false);
                }
                EventBus.getDefault().post(new RefreshEvent(type, RefreshEvent.SYNC_COLLECTIONG));
            }
        });
    }

    /**
     * 更新收藏状态
     */
    @Deprecated
    public static List<MusicModel> updateCollected(final Context context, final List<MusicModel> list) {
        if (list == null || list.size() <= 0) {
            return list;
        }

        HashMap<String, MusicModel> collections = getCollections(context);
        if (collections == null || collections.size() <= 0) {
            return list;
        }

        int size = list.size();
        for (int i = 0; i < size; i++) {
            MusicModel model = list.get(i);
            MusicModel collect = collections.get(model.url);
            if (collect != null) {
                model.isCollected = collect.isCollected;
            }
        }
        return list;
    }

    public static HashMap<String, MusicModel> getCollections(Context context) {
        List<MusicModel> datas = AppDBUtil.getIns(context).optMusic()
                .queryAll(AppDB.COLLECTION_MUSIC);
        if (datas == null || datas.size() <= 0) {
            return new HashMap<>();
        }
        int count = datas.size();
        HashMap<String, MusicModel> collections = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            MusicModel model = datas.get(i);
            collections.put(model.url, model);
        }
        return collections;
    }
}
