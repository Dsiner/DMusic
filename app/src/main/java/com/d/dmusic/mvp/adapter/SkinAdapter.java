package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.d.dmusic.R;
import com.d.dmusic.mvp.model.RadioModel;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;

import java.util.List;

/**
 * SkinAdapter
 * Created by D on 2017/6/16.
 */
public class SkinAdapter extends CommonAdapter<RadioModel> {
    private int index;

    public SkinAdapter(Context context, List<RadioModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void convert(final int position, final CommonHolder holder, final RadioModel item) {
        holder.setBackground(R.id.flyt_check, item.color);
        holder.setViewVisibility(R.id.iv_check, item.isChecked ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.isChecked) {
                    item.isChecked = true;
                    mDatas.get(index).isChecked = false;
                    index = position;
                    notifyDataSetChanged();
                }
            }
        });
    }
}
