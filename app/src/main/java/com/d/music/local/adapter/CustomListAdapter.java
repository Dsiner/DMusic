package com.d.music.local.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.d.lib.common.module.repeatclick.OnClickFastListener;
import com.d.lib.common.module.taskscheduler.TaskScheduler;
import com.d.lib.slidelayout.SlideLayout;
import com.d.lib.slidelayout.SlideManager;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.lib.xrv.adapter.MultiItemTypeSupport;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.local.fragment.SongFragment;
import com.d.music.module.events.RefreshEvent;
import com.d.music.module.greendao.bean.CustomListModel;
import com.d.music.module.greendao.db.AppDB;
import com.d.music.module.greendao.util.AppDBUtil;
import com.d.music.view.dialog.NewListDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * CustomListAdapter
 * Created by D on 2017/5/6.
 */
public class CustomListAdapter extends CommonAdapter<CustomListModel> {
    private SlideManager manager;
    private RefreshEvent event;

    public CustomListAdapter(Context context, List<CustomListModel> datas, MultiItemTypeSupport<CustomListModel> multiItemTypeSupport) {
        super(context, datas, multiItemTypeSupport);
        manager = new SlideManager();
        event = new RefreshEvent(RefreshEvent.TYPE_INVALID, RefreshEvent.SYNC_CUSTOM_LIST);
    }

    @Override
    public void convert(final int position, CommonHolder holder, final CustomListModel item) {
        if (holder.mLayoutId == R.layout.adapter_custom_list) {
            holder.setText(R.id.tv_list_name, item.name);
            holder.setText(R.id.tv_song_count, (item.count != null ? item.count : 0) + "首");
            final SlideLayout slSlide = holder.getView(R.id.sl_slide);
            slSlide.setOpen(item.exIsOpen, false);
            slSlide.setOnStateChangeListener(new SlideLayout.OnStateChangeListener() {
                @Override
                public void onChange(SlideLayout layout, boolean isOpen) {
                    item.exIsOpen = isOpen;
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
                    MainActivity.getManger().replace(SongFragment.getInstance(item.pointer, item.name));
                }
            });
        } else if (holder.mLayoutId == R.layout.adapter_custom_list_add) {
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return manager.closeAll(null);
                }
            });
            holder.itemView.setOnClickListener(new OnClickFastListener() {
                @Override
                public void onFastClick(View v) {
                    new NewListDialog(mContext).show();
                }
            });
        }
    }

    private void delete(final CustomListModel item) {
        TaskScheduler.executeTask(new Runnable() {
            @Override
            public void run() {
                AppDBUtil.getIns(mContext).optMusic().delete(AppDB.CUSTOM_LIST, item);
                AppDBUtil.getIns(mContext).optMusic().deleteAll(item.pointer);
            }
        });
    }

    private void stick(final CustomListModel item) {
        item.seq = AppDBUtil.getIns(mContext).optCustomList().queryMinSeq() - 1;
        AppDBUtil.getIns(mContext).optCustomList().insertOrReplace(item);
        EventBus.getDefault().post(event);
    }

    public void closeAllF() {
        if (manager != null) {
            manager.closeAll(null);
        }
    }
}
