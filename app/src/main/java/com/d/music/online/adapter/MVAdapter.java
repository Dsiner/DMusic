package com.d.music.online.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.online.activity.MVDetailActivity;
import com.d.music.online.model.MVModel;

import java.util.List;

/**
 * MVAdapter
 * Created by D on 2018/8/11.
 */
public class MVAdapter extends CommonAdapter<MVModel> {

    public MVAdapter(Context context, List<MVModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, final MVModel item) {
        holder.setText(R.id.tv_play_count, "播放次数: " + formatPlayCount(item.playCount));
        holder.setText(R.id.tv_seq, "" + (position + 1));
        holder.setTextColor(R.id.tv_seq, position < 3 ? ContextCompat.getColor(mContext, R.color.lib_pub_color_red)
                : ContextCompat.getColor(mContext, R.color.lib_pub_color_white));
        holder.setText(R.id.tv_title, "" + item.name);
        holder.setText(R.id.tv_singer, "" + item.artistName);
        Glide.with(mContext)
                .load(item.cover)
                .apply(new RequestOptions().dontAnimate())
                .into((ImageView) holder.getView(R.id.iv_cover));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MVDetailActivity.openActivity(mContext, item.id);
            }
        });
    }

    private String formatPlayCount(int count) {
        if (count < 10000) {
            return "" + count;
        }
        return count / 10000 + "." + count / 1000 % 10 + "万";
    }
}
