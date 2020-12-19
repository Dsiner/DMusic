package com.d.music.play.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.R;

import java.util.List;

/**
 * SearchHistoryAdapter
 * Created by D on 2017/4/29.
 */
public class SearchHistoryAdapter extends CommonAdapter<String> {
    private OnClickListener mOnClickListener;

    public SearchHistoryAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(final int position, final CommonHolder holder, final String item) {
        holder.setText(R.id.tv_tag, item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(position, item);
                }
            }
        });
        holder.setOnClickListener(R.id.iv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onDelete(position, item);
                }
            }
        });
    }

    public void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }

    public interface OnClickListener {
        void onClick(int position, String item);

        void onDelete(int position, String item);
    }
}
