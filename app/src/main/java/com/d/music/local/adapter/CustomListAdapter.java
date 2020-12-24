package com.d.music.local.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.d.lib.common.component.quickclick.OnAvailableClickListener;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.lib.pulllayout.rv.adapter.MultiItemTypeSupport;
import com.d.lib.slidelayout.SlideLayout;
import com.d.lib.taskscheduler.TaskScheduler;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.CustomListModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.event.eventbus.RefreshEvent;
import com.d.music.local.fragment.SongFragment;
import com.d.music.util.SlideHelper;
import com.d.music.widget.dialog.NewListDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * CustomListAdapter
 * Created by D on 2017/5/6.
 */
public class CustomListAdapter extends CommonAdapter<CustomListModel> {
    private SlideHelper mSlideHelper;
    private RefreshEvent mRefreshEvent;

    public CustomListAdapter(Context context, List<CustomListModel> datas, MultiItemTypeSupport<CustomListModel> multiItemTypeSupport) {
        super(context, datas, multiItemTypeSupport);
        mSlideHelper = new SlideHelper();
        mRefreshEvent = new RefreshEvent(RefreshEvent.TYPE_INVALID, RefreshEvent.SYNC_CUSTOM_LIST);
    }

    @Override
    public void convert(final int position, CommonHolder holder, final CustomListModel item) {
        if (holder.layoutId == R.layout.module_local_adapter_custom_list) {
            holder.setText(R.id.tv_list_name, item.name);
            holder.setText(R.id.tv_song_count, String.format(mContext.getResources().getString(
                    R.string.module_common_song_unit_format),
                    (item.count != null ? item.count : 0)));
            final SlideLayout slSlide = holder.getView(R.id.sl_slide);
            slSlide.setOpen(item.exIsOpen, false);
            slSlide.setOnStateChangeListener(new SlideLayout.OnStateChangeListener() {
                @Override
                public boolean onInterceptTouchEvent(SlideLayout layout) {
                    mSlideHelper.closeAll(layout);
                    return false;
                }

                @Override
                public void onStateChanged(SlideLayout layout, boolean isOpen) {
                    item.exIsOpen = isOpen;
                    mSlideHelper.onStateChanged(layout, isOpen);
                }
            });
            holder.setOnClickListener(R.id.tv_stick, new OnAvailableClickListener() {
                @Override
                public void onAvailableClick(View v) {
                    slSlide.setOpen(false, false);
                    stick(item);
                }
            });
            holder.setOnClickListener(R.id.tv_delete, new OnAvailableClickListener() {
                @Override
                public void onAvailableClick(View v) {
                    slSlide.close();
                    mDatas.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mDatas.size());
                    delete(item);
                }
            });
            holder.setOnClickListener(R.id.llyt_item, new OnAvailableClickListener() {
                @Override
                public void onAvailableClick(View v) {
                    if (slSlide.isOpen()) {
                        slSlide.close();
                        return;
                    }
                    MainActivity.getManger().replace(SongFragment.getInstance(item.pointer, item.name));
                }
            });
        } else if (holder.layoutId == R.layout.module_local_adapter_custom_list_add) {
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mSlideHelper.closeAll(null);
                }
            });
            holder.itemView.setOnClickListener(new OnAvailableClickListener() {
                @Override
                public void onAvailableClick(View v) {
                    new NewListDialog(mContext).show();
                }
            });
        }
    }

    private void delete(final CustomListModel item) {
        TaskScheduler.executeTask(new Runnable() {
            @Override
            public void run() {
                DBManager.getInstance(mContext).optMusic().delete(AppDatabase.CUSTOM_LIST, item);
                DBManager.getInstance(mContext).optMusic().deleteAll(item.pointer);
            }
        });
    }

    private void stick(final CustomListModel item) {
        item.seq = DBManager.getInstance(mContext).optCustomList().queryMinSeq() - 1;
        DBManager.getInstance(mContext).optCustomList().insertOrReplace(item);
        EventBus.getDefault().post(mRefreshEvent);
    }

    public void closeAllF() {
        if (mSlideHelper != null) {
            mSlideHelper.closeAll(null);
        }
    }
}
