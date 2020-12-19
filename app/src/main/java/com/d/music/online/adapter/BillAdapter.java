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
import com.d.music.online.model.BillModel;

import java.util.List;

/**
 * BillAdapter
 * Created by D on 2018/8/11.
 */
public class BillAdapter extends CommonAdapter<BillModel> {
    private int[] musics = new int[]{R.id.tv_music_0, R.id.tv_music_1, R.id.tv_music_2};

    public BillAdapter(Context context, List<BillModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, final BillModel item) {
        holder.setVisibility(musics[0], View.GONE);
        holder.setVisibility(musics[1], View.GONE);
        holder.setVisibility(musics[2], View.GONE);
        if (item.content != null && item.content.size() > 0) {
            for (int i = 0; i < item.content.size() && i < 3; i++) {
                holder.setVisibility(musics[i], View.VISIBLE);
                holder.setText(musics[i], (i + 1) + ". " + item.content.get(i).title + " - " + item.content.get(i).author);
            }
        }
        Glide.with(mContext)
                .load(item.pic_s192)
                .apply(new RequestOptions().dontAnimate())
                .into((ImageView) holder.getView(R.id.iv_cover));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.openActivity(mContext, DetailActivity.TYPE_BILL, "" + item.type, "" + item.name);
            }
        });
    }
}
