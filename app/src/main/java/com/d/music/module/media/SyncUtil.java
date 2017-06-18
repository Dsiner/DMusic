package com.d.music.module.media;

import android.content.Context;

import com.d.music.module.events.RefreshEvent;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.CollectionMusic;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.greendao.util.MusicDBUtil;
import com.d.music.utils.TaskManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * SyncUtil
 * Created by D on 2017/5/9.
 */
public class SyncUtil {
    /**
     * 收藏type-取消收藏
     */
    public static void unCollected(final Context context, final List<MusicModel> list, final int type) {
        if (type != MusicDB.COLLECTION_MUSIC || list == null || list.size() <= 0) {
            return;
        }
        TaskManager.getIns().executeTask(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    MusicModel item = list.get(i);
                    if (item == null) {
                        continue;
                    }
                    MusicDBUtil.getInstance(context).updateColleted(item.url, false);
                }
                EventBus.getDefault().post(new RefreshEvent(type, RefreshEvent.SYNC_COLLECTIONG));
            }
        });
    }

    public static void upCollected(final Context context, final MusicModel item, final int type) {
        TaskManager.getIns().executeTask(new Runnable() {
            @Override
            public void run() {
                if (item.isCollected) {
                    MusicDBUtil.getInstance(context).insertOrReplaceMusic(item.clone(new CollectionMusic()), MusicDB.COLLECTION_MUSIC);
                    MusicDBUtil.getInstance(context).updateColleted(item.url, true);
                } else {
                    MusicDBUtil.getInstance(context).delete(MusicDB.COLLECTION_MUSIC, item.clone(new CollectionMusic()));
                    MusicDBUtil.getInstance(context).updateColleted(item.url, false);
                }
                EventBus.getDefault().post(new RefreshEvent(type, RefreshEvent.SYNC_COLLECTIONG));
            }
        });
    }
}
