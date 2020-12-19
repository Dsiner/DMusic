package com.d.music.play.adapter;

import android.content.Context;
import android.view.View;

import com.d.music.R;
import com.d.music.online.model.SearchHotRespModel;
import com.d.music.widget.flowlayout.CommonHolder;
import com.d.music.widget.flowlayout.FlowLayoutAdapter;

import java.util.List;

/**
 * FlowTagAdapter
 * Created by D on 2018/10/23.
 **/
public class FlowTagAdapter extends FlowLayoutAdapter<SearchHotRespModel.HotsBean> {
    private OnClickListener mOnClickListener;

    public FlowTagAdapter(Context context, List<SearchHotRespModel.HotsBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, final SearchHotRespModel.HotsBean item) {
        holder.setText(R.id.tv_tag, item.first);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(v, item.first);
                }
            }
        });
    }

    public void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }

    public interface OnClickListener {
        void onClick(View v, String tag);
    }
}
