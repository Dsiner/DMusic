package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.d.dmusic.R;
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.dmusic.module.repeatclick.OnClickFastListener;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;

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
    public void convert(int position, final CommonHolder holder, final CustomList item) {
        holder.setText(R.id.tv_list_name, item.listName);
        holder.setChecked(R.id.cb_check, item.isChecked);
        holder.setViewOnClickListener(R.id.llyt_item, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                item.isChecked = !item.isChecked;
                holder.setChecked(R.id.cb_check, item.isChecked);
            }
        });
    }
}
