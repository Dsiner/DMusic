package com.d.dmusic.module.media;

import android.content.Context;

import com.d.dmusic.module.events.RefreshEvent;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.CollectionMusic;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.utils.TaskManager;

import org.greenrobot.eventbus.EventBus;

/**
 * SyncUtil
 * Created by D on 2017/5/9.
 */
public class SyncUtil {

    public static void upCollected(final Context context, final MusicModel item) {
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
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.SYNC_COLLECTIONG));
            }
        });
    }
}
