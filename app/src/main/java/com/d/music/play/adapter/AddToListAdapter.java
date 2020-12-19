package com.d.music.play.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.CustomListModel;

import java.util.List;

/**
 * 添加到列表Popup适配器
 * Created by D on 2017/4/29.
 */
public class AddToListAdapter extends CommonAdapter<CustomListModel> {

    public AddToListAdapter(Context context, List<CustomListModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, final CommonHolder holder, final CustomListModel item) {
        holder.setText(R.id.tv_list_name, item.name);
        holder.setChecked(R.id.cb_check, item.exIsChecked);
        holder.setOnClickListener(R.id.llyt_item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.exIsChecked = !item.exIsChecked;
                holder.setChecked(R.id.cb_check, item.exIsChecked);
            }
        });
    }
}
