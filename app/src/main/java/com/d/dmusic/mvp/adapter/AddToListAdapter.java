package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.d.dmusic.R;
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.xrv.adapter.CommonAdapter;
import com.d.xrv.adapter.CommonHolder;

import java.util.List;

/**
 * 添加到列表Dialog适配器
 *
 * @author D
 */
public class AddToListAdapter extends CommonAdapter<CustomList> {
    public AddToListAdapter(Context context, List<CustomList> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, CustomList item) {
        holder.setText(R.id.tv_list_name, item.listName);
        holder.setViewOnClickListener(R.id.llyt_selected, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
