package com.d.music.online.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.online.activity.DetailActivity;
import com.d.music.online.model.SingerModel;

import java.util.List;

/**
 * SingerAdapter
 * Created by D on 2018/8/11.
 */
public class SingerAdapter extends CommonAdapter<SingerModel> {

    public SingerAdapter(Context context, List<SingerModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, final SingerModel item) {
        holder.setText(R.id.tv_seq, "" + (position + 1));
        holder.setTextColor(R.id.tv_seq, position < 3 ? ContextCompat.getColor(mContext, R.color.lib_pub_color_red)
                : ContextCompat.getColor(mContext, R.color.lib_pub_color_text_sub));
        holder.setText(R.id.tv_singer, "" + item.name);
        holder.setText(R.id.tv_score, mContext.getResources().getString(R.string.module_common_heat)
                + "?");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.openActivity(mContext, DetailActivity.TYPE_ARTIST,
                        item.ting_uid, item.name, item.avatar_big);
            }
        });
        Glide.with(mContext)
                .load(item.avatar_middle)
                .apply(new RequestOptions().dontAnimate())
                .into((ImageView) holder.getView(R.id.iv_cover));
    }
}
