package com.d.music.play.adapter;

import android.content.Context;

import com.d.lib.common.component.lv.CommonHolder;
import com.d.music.R;
import com.d.music.view.flowlayout.FlowLayoutAdapter;

import java.util.List;

/**
 * FlowTagAdapter
 * Created by D on 2018/10/23.
 **/
public class FlowTagAdapter extends FlowLayoutAdapter<String> {
    public FlowTagAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, String item) {
        holder.setText(R.id.tv_tag, item);
    }
}
