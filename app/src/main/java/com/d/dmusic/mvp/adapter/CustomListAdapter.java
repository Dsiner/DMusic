package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.dmusic.MainActivity;
import com.d.dmusic.R;
import com.d.dmusic.module.events.RefreshEvent;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.module.swipelayout.SwipeLayout;
import com.d.dmusic.module.swipelayout.adapters.RecyclerSwipeAdapter;
import com.d.dmusic.mvp.fragment.SongFragment;
import com.d.dmusic.utils.TaskManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * CustomListAdapter
 * Created by D on 2017/5/6.
 */
public class CustomListAdapter extends RecyclerSwipeAdapter<CustomListAdapter.SimpleViewHolder> {
    private Context mContext;
    private List<CustomList> mDatas;
    private RefreshEvent event;

    public CustomListAdapter(Context context, List<CustomList> datas) {
        mContext = context;
        mDatas = datas == null ? new ArrayList<CustomList>() : datas;
        event = new RefreshEvent(RefreshEvent.SYNC_CUSTOM_LIST);
    }

    public void setDatas(List<CustomList> datas) {
        if (mDatas != null && datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_custom_list, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final CustomList item = mDatas.get(position);
        viewHolder.slSwipe.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.tvListName.setText(item.listName);
        viewHolder.tvSongCount.setText((item.songCount != null ? item.songCount : 0) + "首");
        viewHolder.tvStick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.closeItem(position);
                stick(item);
            }
        });
        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(viewHolder.slSwipe);
                mItemManger.closeItem(position);
                mDatas.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDatas.size());
                delete(item);
            }
        });
        viewHolder.llytItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", item.listName);
                bundle.putInt("type", item.pointer);
                SongFragment songFragment = new SongFragment();
                songFragment.setArguments(bundle);

                MainActivity.replace(songFragment);
            }
        });
        mItemManger.bindView(viewHolder.itemView, position);
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

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.sl_swipe;
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout slSwipe;
        LinearLayout llytItem;
        TextView tvListName;
        TextView tvSongCount;
        TextView tvStick;
        TextView tvDelete;

        SimpleViewHolder(View itemView) {
            super(itemView);
            slSwipe = (SwipeLayout) itemView.findViewById(R.id.sl_swipe);
            llytItem = (LinearLayout) itemView.findViewById(R.id.llyt_item);
            tvListName = (TextView) itemView.findViewById(R.id.tv_list_name);
            tvSongCount = (TextView) itemView.findViewById(R.id.tv_song_count);
            tvStick = (TextView) itemView.findViewById(R.id.tv_stick);
            tvDelete = (TextView) itemView.findViewById(R.id.tv_delete);
        }
    }
}
