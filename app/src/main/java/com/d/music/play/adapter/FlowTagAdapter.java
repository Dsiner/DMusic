package com.d.music.play.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.common.component.lv.CommonHolder;
import com.d.music.R;
import com.d.music.online.model.SearchHotRespModel;
import com.d.music.view.flowlayout.FlowLayoutAdapter;

import java.util.List;

/**
 * FlowTagAdapter
 * Created by D on 2018/10/23.
 **/
public class FlowTagAdapter extends FlowLayoutAdapter<SearchHotRespModel.HotsBean> {
    private OnClickListener onClickListener;

    public FlowTagAdapter(Context context, List<SearchHotRespModel.HotsBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, final SearchHotRespModel.HotsBean item) {
        holder.setText(R.id.tv_tag, item.first);
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v, item.first);
                }
            }
        });
    }

    public interface OnClickListener {
        void onClick(View v, String tag);
    }

    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
    }
}
