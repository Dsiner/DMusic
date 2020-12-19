package com.d.music.online.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.online.activity.DetailActivity;
import com.d.music.online.model.RadioModel;

import java.util.List;

/**
 * RadioAdapter
 * Created by D on 2018/8/11.
 */
public class RadioAdapter extends CommonAdapter<RadioModel> {

    public RadioAdapter(Context context, List<RadioModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, final RadioModel item) {
        holder.setText(R.id.tv_title, "" + item.name);
        Glide.with(mContext)
                .load(item.thumb)
                .apply(new RequestOptions().dontAnimate())
                .into((ImageView) holder.getView(R.id.iv_cover));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.openActivity(mContext, DetailActivity.TYPE_RADIO, item.ch_name, item.name, item.thumb);
            }
        });
    }
}
