package com.d.music.local.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.common.module.repeatclick.OnClickFastListener;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.local.view.ISongView;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.service.MusicControl;
import com.d.music.module.service.MusicService;
import com.d.music.module.utils.MoreUtil;
import com.d.music.view.popup.MorePopup;

import java.util.List;

public class SongAdapter extends CommonAdapter<MusicModel> {
    private int type;// 列表标识
    private boolean isSubPull;
    private ISongView listener;

    public SongAdapter(Context context, List<MusicModel> datas, int layoutId, int type, ISongView listener) {
        super(context, datas, layoutId);
        this.type = type;
        this.listener = listener;
    }

    public void setSubPull(boolean subPull) {
        isSubPull = subPull;
    }

    @Override
    public void convert(final int position, final CommonHolder holder, final MusicModel item) {
        holder.setViewVisibility(R.id.llyt_section, item.isLetter ? View.VISIBLE : View.GONE);
        holder.setViewVisibility(R.id.v_right_space, item.letter != null ? View.VISIBLE : View.GONE);
        if (item.isLetter) {
            holder.setText(R.id.tv_letter, item.letter);
        }

        holder.setText(R.id.tv_list_name, item.songName);
        holder.setText(R.id.tv_title, item.singer);
        holder.setChecked(R.id.cb_more, item.isChecked);
        holder.setViewVisibility(R.id.llyt_more_cover, item.isChecked ? View.VISIBLE : View.GONE);
        holder.setText(R.id.tv_collect, item.isCollected ? "已收藏" : "收藏");
        holder.setViewOnClickListener(R.id.llyt_song, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                MusicControl control = MusicService.getControl(mContext);
                control.init(mContext, mDatas, position, true);
            }
        });
        holder.setViewOnClickListener(R.id.flyt_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSubPull) {
                    MorePopup morePopup = new MorePopup(mContext, MorePopup.TYPE_SONG_SUB, item, type);
                    morePopup.setOnOperationLitener(new MorePopup.OnOperationLitener() {
                        @Override
                        public void onCollect() {
                            collect(item, holder, position);
                        }
                    });
                    morePopup.show();
                } else {
                    item.isChecked = !item.isChecked;
                    holder.setChecked(R.id.cb_more, item.isChecked);
                    holder.setViewVisibility(R.id.llyt_more_cover, item.isChecked ? View.VISIBLE : View.GONE);
                }
            }
        });
        holder.setViewOnClickListener(R.id.llyt_collect, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                collect(item, holder, position);
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

    private void collect(MusicModel item, CommonHolder holder, int position) {
        MoreUtil.collect(mContext, item, type, true);
        //status "item.isCollected" is changed
        if (type == MusicDB.COLLECTION_MUSIC && !item.isCollected) {
            mDatas.remove(position);
            notifyDataSetChanged();
            if (listener != null) {
                listener.notifyDataCountChanged(mDatas.size());
            }
        } else {
            holder.setText(R.id.tv_collect, item.isCollected ? "已收藏" : "收藏");
            //将下拉菜单收回
            pullUp(item, holder);
        }
    }

    private void pullUp(final MusicModel item, final CommonHolder holder) {
        item.isChecked = false;
        holder.setChecked(R.id.cb_more, false);
        holder.setViewVisibility(R.id.llyt_more_cover, View.GONE);
    }
}
