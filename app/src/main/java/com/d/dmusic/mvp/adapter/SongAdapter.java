package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.d.dmusic.R;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.repeatclick.OnClickFastListener;
import com.d.dmusic.module.service.MusicControl;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.module.utils.MoreUtil;
import com.d.dmusic.mvp.view.ISongView;
import com.d.dmusic.view.popup.MorePopup;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;

import java.util.List;

public class SongAdapter extends CommonAdapter<MusicModel> {
    private int type;// 列表标识
    private boolean IsSubPull;
    private ISongView listener;

    public SongAdapter(Context context, List<MusicModel> datas, int layoutId, int type, ISongView listener) {
        super(context, datas, layoutId);
        this.type = type;
        this.listener = listener;
    }

    public void setSubPull(boolean subPull) {
        IsSubPull = subPull;
    }

    @Override
    public void convert(final int position, final CommonHolder holder, final MusicModel item) {
        if (item.isLetter) {
            holder.setText(R.id.tv_letter, item.letter);
            holder.setViewVisibility(R.id.llyt_section, View.VISIBLE);
        } else {
            holder.setViewVisibility(R.id.llyt_section, View.GONE);
        }
        if (item.letter != null) {
            holder.setViewVisibility(R.id.v_right_space, View.VISIBLE);
        } else {
            holder.setViewVisibility(R.id.v_right_space, View.GONE);
        }
        holder.setText(R.id.tv_list_name, item.songName);
        holder.setText(R.id.tv_title, item.singer);
        if (item.isChecked) {
            holder.setChecked(R.id.cb_more, true);
            holder.setViewVisibility(R.id.llyt_more_cover, View.VISIBLE);
        } else {
            holder.setChecked(R.id.cb_more, false);
            holder.setViewVisibility(R.id.llyt_more_cover, View.GONE);
        }
        if (item.isCollected) {
            holder.setText(R.id.tv_collect, "已收藏");
        } else {
            holder.setText(R.id.tv_collect, "收藏");
        }
        holder.setViewOnClickListener(R.id.llyt_song, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                MusicControl control = MusicService.getControl();
                control.init(mDatas, position, true);
            }
        });
        holder.setViewOnClickListener(R.id.flyt_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IsSubPull) {
                    new MorePopup(mContext, MorePopup.TYPE_SONG_SUB, item).show();
                } else {
                    item.isChecked = !item.isChecked;
                    if (item.isChecked) {
                        holder.setChecked(R.id.cb_more, true);
                        holder.setViewVisibility(R.id.llyt_more_cover, View.VISIBLE);
                    } else {
                        holder.setChecked(R.id.cb_more, false);
                        holder.setViewVisibility(R.id.llyt_more_cover, View.GONE);
                    }
                }
            }
        });
        holder.setViewOnClickListener(R.id.llyt_collect, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                MoreUtil.collect(mContext, item, type, true);
                if (item.isCollected) {
                    holder.setText(R.id.tv_collect, "已收藏");
                    //将下拉菜单收回
                    pullUp(item, holder);
                } else {
                    if (type == MusicDB.COLLECTION_MUSIC) {
                        mDatas.remove(position);
                        notifyDataSetChanged();
                        if (listener != null) {
                            listener.notifyDataCountChanged(mDatas.size());
                        }
                    } else {
                        holder.setText(R.id.tv_collect, "收藏");
                        //将下拉菜单收回
                        pullUp(item, holder);
                    }
                }
            }
        });
        holder.setViewOnClickListener(R.id.llyt_add_to_list, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                MoreUtil.addToList(mContext, item, type);
            }
        });
        holder.setViewOnClickListener(R.id.llyt_info, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                MoreUtil.showInfo(mContext, item);
            }
        });
    }

    private void pullUp(final MusicModel item, final CommonHolder holder) {
        item.isChecked = false;
        holder.setChecked(R.id.cb_more, false);
        holder.setViewVisibility(R.id.llyt_more_cover, View.GONE);
    }
}
