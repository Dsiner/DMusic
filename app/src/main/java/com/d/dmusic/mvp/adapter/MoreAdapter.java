package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.d.dmusic.R;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;

import java.util.List;

/**
 * MorePopup适配器
 * Created by D on 2017/4/29.
 */
public class MoreAdapter extends CommonAdapter<MoreAdapter.Bean> {
    public MoreAdapter(Context context, List<Bean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, final CommonHolder holder, final Bean item) {
        holder.setImageResource(R.id.iv_icon, item.icon);
        holder.setText(R.id.tv_lable, item.lable);
        holder.itemView.setOnClickListener(item.listener);
    }

    public static class Bean {
        int icon;
        String lable;
        View.OnClickListener listener;

        public Bean(int icon, String lable, View.OnClickListener listener) {
            this.icon = icon;
            this.lable = lable;
            this.listener = listener;
        }
    }
}
