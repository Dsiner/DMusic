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
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.dmusic.module.swipelayout.SwipeLayout;
import com.d.dmusic.module.swipelayout.adapters.RecyclerSwipeAdapter;
import com.d.dmusic.mvp.fragment.SongFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * CustomListAdapter
 * Created by D on 2017/5/6.
 */
public class CustomListAdapter extends RecyclerSwipeAdapter<CustomListAdapter.SimpleViewHolder> {
    private Context mContext;
    private List<CustomList> mDatas;

    public CustomListAdapter(Context context, List<CustomList> datas) {
        mContext = context;
        mDatas = datas == null ? new ArrayList<CustomList>() : datas;
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
        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(viewHolder.slSwipe);
                mDatas.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDatas.size());
                mItemManger.closeAllItems();
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

                MainActivity.fManger.beginTransaction().replace(R.id.framement, songFragment)
                        .addToBackStack(null).commitAllowingStateLoss();
            }
        });
        mItemManger.bindView(viewHolder.itemView, position);
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
        TextView tvDelete;

        SimpleViewHolder(View itemView) {
            super(itemView);
            slSwipe = (SwipeLayout) itemView.findViewById(R.id.sl_swipe);
            llytItem = (LinearLayout) itemView.findViewById(R.id.llyt_item);
            tvListName = (TextView) itemView.findViewById(R.id.tv_list_name);
            tvSongCount = (TextView) itemView.findViewById(R.id.tv_song_count);
            tvDelete = (TextView) itemView.findViewById(R.id.tv_delete);
        }
    }
}
