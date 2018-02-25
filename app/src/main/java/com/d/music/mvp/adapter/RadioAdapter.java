package com.d.music.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.mvp.model.RadioModel;

import java.util.List;

/**
 * RadioAdapter
 * Created by D on 2017/6/16.
 */
public class RadioAdapter extends CommonAdapter<RadioModel> {
    private int index;

    public RadioAdapter(Context context, List<RadioModel> datas, int layoutId) {
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
        holder.setText(R.id.tv_content, item.content);
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
