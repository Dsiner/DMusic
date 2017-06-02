package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.d.dmusic.R;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.repeatclick.OnClickFastListener;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.lib.xrv.itemtouchhelper.ItemTouchHelperViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * HandleAdapter
 * Created by D on 2017/6/3.
 */
public class HandleAdapter extends CommonAdapter<MusicModel> {

    public HandleAdapter(Context context, List<MusicModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, final CommonHolder holder, final MusicModel item) {
        holder.setText(R.id.tv_song_name, item.songName);
        holder.setText(R.id.tv_singer, item.singer);
        holder.setChecked(R.id.cb_check, item.isSortChecked);
        holder.itemView.setOnClickListener(new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                item.isSortChecked = !item.isSortChecked;
                holder.setChecked(R.id.cb_check, item.isSortChecked);
            }
        });
        holder.getView(R.id.iv_handler).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && getItemCount() > 1 && startDragListener != null) {
                    startDragListener.onStartDrag(holder);
                    return true;
                }
                return false;
            }
        });
        holder.setOnItemTouchListener(new ItemTouchHelperViewHolder() {
            @Override
            public void onItemSelected() {
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.color_lgray));
            }

            @Override
            public void onItemClear() {
                holder.itemView.setBackgroundColor(0);
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
            MusicModel from = mDatas.get(fromPosition);
            mDatas.remove(fromPosition);
            mDatas.add(toPosition, from);
        } else {
            Collections.swap(mDatas, fromPosition, toPosition);
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
}
