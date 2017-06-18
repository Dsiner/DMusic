package com.d.music.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.d.music.R;
import com.d.music.model.FileModel;
import com.d.music.module.repeatclick.OnClickFastListener;
import com.d.music.utils.fileutil.FileUtil;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;

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
        holder.setText(R.id.tv_music_count, item.musicCount + "首");
        holder.setChecked(R.id.cb_check, item.isChecked);
        holder.setViewOnClickListener(R.id.ll_dir_sub, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                String p = item.absolutePath;
                if (!FileUtil.isEndPath(p)) {
                    if (listener != null) {
                        listener.onPath(p);
                    }
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
