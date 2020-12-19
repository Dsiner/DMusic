package com.d.music.setting.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.setting.model.RadioModel;

import java.util.List;

/**
 * RadioAdapter
 * Created by D on 2017/6/16.
 */
public class ModeAdapter extends CommonAdapter<RadioModel> {
    private int index;

    public ModeAdapter(Context context, List<RadioModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void convert(final int position, final CommonHolder holder, final RadioModel item) {
        holder.setText(R.id.tv_content, item.content);
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
