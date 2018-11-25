package com.d.music.play.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.music.R;

import java.util.List;

/**
 * SearchHistoryAdapter
 * Created by D on 2017/4/29.
 */
public class SearchHistoryAdapter extends CommonAdapter<String> {
    private OnClickListener listener;

    public SearchHistoryAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(final int position, final CommonHolder holder, final String item) {
        holder.setText(R.id.tv_tag, item);
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position, item);
                }
            }
        });
        holder.setViewOnClickListener(R.id.iv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDelete(position, item);
                }
            }
        });
    }

    public interface OnClickListener {
        void onClick(int position, String item);

        void onDelete(int position, String item);
    }

    public void setOnClickListener(OnClickListener l) {
        this.listener = l;
    }
}
