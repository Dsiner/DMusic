package com.d.music.setting.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.setting.model.RadioModel;

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

    public int getIndex() {
        return index - 1;
    }

    public void setIndex(int index) {
        this.index = index + 1;
    }

    @Override
    public void convert(final int position, final CommonHolder holder, final RadioModel item) {
        holder.setBackgroundResource(R.id.flyt_check, item.color);
        holder.setVisibility(R.id.iv_check, item.isChecked ? View.VISIBLE : View.GONE);
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
