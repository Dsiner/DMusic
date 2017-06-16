package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.d.dmusic.R;
import com.d.dmusic.module.greendao.music.base.MusicModel;
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
    private int count = 0;
    private OnChangeListener listener;

    public HandleAdapter(Context context, List<MusicModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public void setCount(int count) {
        if (count < 0) {
            count = 0;
        }
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public void convert(int position, final CommonHolder holder, final MusicModel item) {
        holder.setText(R.id.tv_song_name, item.songName);
        holder.setText(R.id.tv_singer, item.singer);
        holder.setChecked(R.id.cb_check, item.isSortChecked);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.isSortChecked = !item.isSortChecked;
                holder.setChecked(R.id.cb_check, item.isSortChecked);
                if (item.isSortChecked) {
                    count++;
                } else {
                    count--;
                }
                submitCount(count);
            }
        });
        holder.getView(R.id.flyt_handler).setOnTouchListener(new View.OnTouchListener() {
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
                holder.setImageResource(R.id.iv_handler, R.drawable.ic_sort_handler_press);
                holder.setBackground(R.id.cb_check, R.drawable.selector_toggle_press);
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.color_dgray));
                holder.itemView.setAlpha(0.6f);
            }

            @Override
            public void onItemClear() {
                holder.setImageResource(R.id.iv_handler, R.drawable.ic_sort_handler);
                holder.setBackground(R.id.cb_check, R.drawable.selector_toggle);
                holder.itemView.setBackgroundColor(0);
                holder.itemView.setAlpha(1f);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        MusicModel model = mDatas.get(position);
        if (model.isSortChecked && count > 0) {
            count--;
        }
        if (listener != null) {
            listener.onDelete(model);
        }
        mDatas.remove(position);
        notifyItemRemoved(position);
        submitCount(count);
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

    private void submitCount(int count) {
        if (listener != null) {
            listener.onCountChange(count);
        }
    }

    public interface OnChangeListener {
        void onDelete(MusicModel model);

        void onCountChange(int count);
    }

    public void setOnChangeListener(OnChangeListener listener) {
        this.listener = listener;
    }
}
