package com.d.music.local.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.d.lib.common.component.repeatclick.OnClickFastListener;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.local.model.FileModel;

import java.util.List;

/**
 * DirAdapter
 * Created by D on 2017/4/30.
 */
public class DirAdapter extends CommonAdapter<FileModel> {
    private OnPathListener listener;

    public DirAdapter(Context context, List<FileModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, final CommonHolder holder, final FileModel item) {
        holder.setText(R.id.tv_dir, item.name);
        holder.setTextColor(R.id.tv_dir, ContextCompat.getColor(mContext,
                item.isEmptyDir ? R.color.lib_pub_color_text_disable
                        : R.color.lib_pub_color_text_main));
        holder.setText(R.id.tv_music_count, String.format(mContext.getResources().getString(R.string.module_common_song_unit_format),
                item.count));
        holder.setViewVisibility(R.id.tv_music_count, View.GONE);
        holder.setChecked(R.id.cb_check, item.isChecked);
        holder.setViewVisibility(R.id.flyt_selected, item.isEmptyDir ? View.INVISIBLE : View.VISIBLE);
        holder.setViewOnClickListener(R.id.ll_dir_sub, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                String p = item.absolutePath;
                if (listener != null) {
                    listener.onPath(p);
                }
            }
        });
        holder.setViewOnClickListener(R.id.flyt_selected, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.isChecked = !item.isChecked;
                holder.setChecked(R.id.cb_check, item.isChecked);
            }
        });
    }

    public interface OnPathListener {
        void onPath(String path);
    }

    public void setOnPathListener(OnPathListener listener) {
        this.listener = listener;
    }
}
