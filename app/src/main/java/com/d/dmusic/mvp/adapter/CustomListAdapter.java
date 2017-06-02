package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.d.dmusic.MainActivity;
import com.d.dmusic.R;
import com.d.dmusic.module.events.RefreshEvent;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.module.repeatclick.OnClickFastListener;
import com.d.dmusic.mvp.fragment.SongFragment;
import com.d.dmusic.utils.TaskManager;
import com.d.lib.slidelayout.SlideLayout;
import com.d.lib.slidelayout.SlideManager;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * CustomListAdapter
 * Created by D on 2017/5/6.
 */
public class CustomListAdapter extends CommonAdapter<CustomList> {
    private SlideManager manager;
    private RefreshEvent event;

    public CustomListAdapter(Context context, List<CustomList> datas, int layoutId) {
        super(context, datas, layoutId);
        manager = new SlideManager();
        event = new RefreshEvent(RefreshEvent.TYPE_INVALID, RefreshEvent.SYNC_CUSTOM_LIST);
    }

    @Override
    public void convert(final int position, CommonHolder holder, final CustomList item) {
        holder.setText(R.id.tv_list_name, item.listName);
        holder.setText(R.id.tv_song_count, (item.songCount != null ? item.songCount : 0) + "首");
        final SlideLayout slSlide = holder.getView(R.id.sl_slide);
        slSlide.setOpen(item.isOpen, false);
        slSlide.setOnStateChangeListener(new SlideLayout.OnStateChangeListener() {
            @Override
            public void onChange(SlideLayout layout, boolean isOpen) {
                item.isOpen = isOpen;
                manager.onChange(layout, isOpen);
            }

            @Override
            public boolean closeAll(SlideLayout layout) {
                return manager.closeAll(layout);
            }
        });
        holder.setViewOnClickListener(R.id.tv_stick, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                slSlide.setOpen(false, false);
                stick(item);
            }
        });
        holder.setViewOnClickListener(R.id.tv_delete, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                slSlide.close();
                mDatas.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDatas.size());
                delete(item);
            }
        });
        holder.setViewOnClickListener(R.id.llyt_item, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                if (slSlide.isOpen()) {
                    slSlide.close();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("title", item.listName);
                bundle.putInt("type", item.pointer);
                SongFragment songFragment = new SongFragment();
                songFragment.setArguments(bundle);

                MainActivity.replace(songFragment);
            }
        });
    }

    private void delete(final CustomList item) {
        TaskManager.getIns().executeTask(new Runnable() {
            @Override
            public void run() {
                MusicDBUtil.getInstance(mContext).delete(MusicDB.CUSTOM_LIST, item);
                MusicDBUtil.getInstance(mContext).deleteAll(item.pointer);
            }
        });
    }

    private void stick(final CustomList item) {
        item.seq = MusicDBUtil.getInstance(mContext).queryCustomListMinSeq() - 1;
        MusicDBUtil.getInstance(mContext).insertOrReplaceCustomList(item);
        EventBus.getDefault().post(event);
    }

    public void closeAllF() {
        if (manager != null) {
            manager.closeAll(null);
        }
    }
}
