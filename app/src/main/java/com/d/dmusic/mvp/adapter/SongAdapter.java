package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.d.dmusic.R;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.CollectionMusic;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.module.service.MusicControl;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.utils.Util;
import com.d.dmusic.view.dialog.AddToListDialog;
import com.d.dmusic.view.dialog.SongInfoDialog;
import com.d.xrv.adapter.CommonAdapter;
import com.d.xrv.adapter.CommonHolder;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends CommonAdapter<MusicModel> {
    private int type;// 列表标识

    public SongAdapter(Context context, List<MusicModel> datas, int layoutId, int type) {
        super(context, datas, layoutId);
        this.type = type;
    }

    public void setDatas(List<MusicModel> datas) {
        if (mDatas != null && datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
        }
    }

    @Override
    public void convert(final int position, final CommonHolder holder, final MusicModel item) {
        holder.setText(R.id.tv_list_name, item.songName);
        holder.setText(R.id.tv_title, item.singer);
        if (item.isChecked) {
            holder.setChecked(R.id.cb_more, true);
            holder.setViewVisibility(R.id.llyt_more_cover, View.VISIBLE);
        } else {
            holder.setChecked(R.id.cb_more, false);
            holder.setViewVisibility(R.id.llyt_more_cover, View.GONE);
        }

        holder.setViewOnClickListener(R.id.llyt_song, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicControl control = MusicService.getControl();
                control.init((List<MusicModel>) MusicModel.clone(mDatas, MusicDB.MUSIC), position);
            }
        });
        holder.setViewOnClickListener(R.id.llyt_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.isChecked = !item.isChecked;
                if (item.isChecked) {
                    holder.setChecked(R.id.cb_more, true);
                    holder.setViewVisibility(R.id.llyt_more_cover, View.VISIBLE);
                } else {
                    holder.setChecked(R.id.cb_more, false);
                    holder.setViewVisibility(R.id.llyt_more_cover, View.GONE);
                }
            }
        });
        if (item.isCollected) {
            holder.setText(R.id.tv_collect, "已收藏");
        } else {
            holder.setText(R.id.tv_collect, "收藏");
        }
        holder.setViewOnClickListener(R.id.llyt_collect, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.isCollected = !item.isCollected;
                if (item.isCollected) {
                    MusicDBUtil.getInstance(mContext).insertOrReplaceMusic(item.clone(new CollectionMusic()), MusicDB.COLLECTION_MUSIC);
                    //将下拉菜单收回
                    item.isChecked = false;
                    holder.setChecked(R.id.cb_more, false);
                    holder.setViewVisibility(R.id.llyt_more_cover, View.GONE);
                    holder.setText(R.id.tv_collect, "已收藏");
                } else {
                    holder.setText(R.id.tv_collect, "收藏");
                    Util.toast(mContext, "已取消收藏");
                }
            }
        });
        holder.setViewOnClickListener(R.id.llyt_add_to_list, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MusicModel> list = new ArrayList<MusicModel>();
                list.add(item);
                new AddToListDialog(mContext, list, type).show();
            }
        });
        holder.setViewOnClickListener(R.id.llyt_info, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SongInfoDialog(mContext, item).show();
            }
        });
    }
}
