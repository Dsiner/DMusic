package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.d.dmusic.R;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.itemtouchhelper.ItemTouchHelperAdapter;
import com.d.dmusic.module.itemtouchhelper.ItemTouchHelperViewHolder;
import com.d.dmusic.module.itemtouchhelper.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HandlerAdapter
 * Created by D on 2017/4/30.
 */
public class HandlerAdapter<T extends MusicModel> extends RecyclerView.Adapter<HandlerAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {
    protected Context mContext;
    protected List<T> mDatas;
    protected int mLayoutId;
    private final OnStartDragListener startDragListener;

    public HandlerAdapter(Context context, List<T> datas, int layoutId, OnStartDragListener startDragListener) {
        mContext = context;
        mDatas = datas == null ? new ArrayList<T>() : datas;
        mLayoutId = layoutId;
        this.startDragListener = startDragListener;
    }

    public void setData(List<T> datas) {
        if (mDatas != null && datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
        }
    }

    public List<T> getData() {
        return mDatas;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        convert(position, holder, mDatas.get(position));
    }

    private void convert(final int position, final ItemViewHolder holder, final T item) {
        holder.tvSongName.setText(item.songName);
        holder.tvSinger.setText(item.singer);
        holder.ivHandler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && startDragListener != null) {
                    startDragListener.onStartDrag(holder);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (Math.abs(fromPosition - toPosition) > 1) {
            T from = mDatas.get(fromPosition);
            mDatas.remove(fromPosition);
            mDatas.add(toPosition, from);
        } else {
            Collections.swap(mDatas, fromPosition, toPosition);
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        TextView tvSongName;
        TextView tvSinger;
        ImageView ivHandler;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tv_song_name);
            tvSinger = (TextView) itemView.findViewById(R.id.tv_singer);
            ivHandler = (ImageView) itemView.findViewById(R.id.iv_handler);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
