package com.d.music.local.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.d.lib.common.component.repeatclick.OnClickFastListener;
import com.d.lib.common.view.dialog.AbsSheetDialog;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.module.greendao.db.AppDB;
import com.d.music.module.media.controler.MediaControler;
import com.d.music.module.utils.MoreUtil;
import com.d.music.view.dialog.OperationDialog;

import java.util.Arrays;
import java.util.List;

public class SongAdapter extends CommonAdapter<MusicModel> {
    private int type; // 列表标识
    private boolean isSubPull;
    private OnDataChangedListener listener;

    public SongAdapter(Context context, List<MusicModel> datas, int layoutId, int type) {
        super(context, datas, layoutId);
        this.type = type;
    }

    public void setSubPull(boolean subPull) {
        isSubPull = subPull;
    }

    @Override
    public void convert(final int position, final CommonHolder holder, final MusicModel item) {
        holder.setViewVisibility(R.id.llyt_section, item.exIsLetter ? View.VISIBLE : View.GONE);
        holder.setViewVisibility(R.id.v_right_space, item.exLetter != null ? View.VISIBLE : View.GONE);
        if (item.exIsLetter) {
            holder.setText(R.id.tv_letter, item.exLetter);
        }

        holder.setText(R.id.tv_list_name, item.songName);
        holder.setText(R.id.tv_title, item.artistName);
        holder.setChecked(R.id.cb_more, item.exIsChecked);
        holder.setViewVisibility(R.id.llyt_more_cover, item.exIsChecked ? View.VISIBLE : View.GONE);
        holder.setText(R.id.tv_collect, item.isCollected ? "已收藏" : "收藏");
        holder.setViewOnClickListener(R.id.llyt_song, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                MediaControler.getIns(mContext).init(mDatas, position, true);
            }
        });
        holder.setViewOnClickListener(R.id.flyt_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSubPull) {
                    OperationDialog.getOperationDialog(mContext, OperationDialog.TYPE_NORMAL, "",
                            Arrays.asList(new OperationDialog.Bean().with(mContext, OperationDialog.Bean.TYPE_ADDLIST, false),
                                    new OperationDialog.Bean().with(mContext, OperationDialog.Bean.TYPE_FAV, false)
                                            .item(item.isCollected ? "已收藏" : "收藏"),
                                    new OperationDialog.Bean().with(mContext, OperationDialog.Bean.TYPE_INFO, false)),
                            new AbsSheetDialog.OnItemClickListener<OperationDialog.Bean>() {
                                @Override
                                public void onClick(Dialog dlg, int position, OperationDialog.Bean bean) {
                                    if (bean.type == OperationDialog.Bean.TYPE_ADDLIST) {
                                        MoreUtil.addToList(mContext, type, item);
                                    } else if (bean.type == OperationDialog.Bean.TYPE_FAV) {
                                        collect(item, holder, position);
                                    } else if (bean.type == OperationDialog.Bean.TYPE_INFO) {
                                        MoreUtil.showInfo(mContext, item);
                                    }
                                }

                                @Override
                                public void onCancel(Dialog dlg) {

                                }
                            });
                } else {
                    item.exIsChecked = !item.exIsChecked;
                    holder.setChecked(R.id.cb_more, item.exIsChecked);
                    holder.setViewVisibility(R.id.llyt_more_cover, item.exIsChecked ? View.VISIBLE : View.GONE);
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
                MoreUtil.addToList(mContext, type, item);
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
        MoreUtil.collect(mContext, type, item, true);
        // Status "item.isCollected" is changed
        if (type == AppDB.COLLECTION_MUSIC && !item.isCollected) {
            mDatas.remove(position);
            notifyDataSetChanged();
            if (listener != null) {
                listener.onChange(mDatas.size());
            }
        } else {
            holder.setText(R.id.tv_collect, item.isCollected ? "已收藏" : "收藏");
            // 将下拉菜单收回
            pullUp(item, holder);
        }
    }

    private void pullUp(final MusicModel item, final CommonHolder holder) {
        item.exIsChecked = false;
        holder.setChecked(R.id.cb_more, false);
        holder.setViewVisibility(R.id.llyt_more_cover, View.GONE);
    }

    public interface OnDataChangedListener {
        void onChange(int count);
    }

    public void setOnDataChangedListener(OnDataChangedListener l) {
        this.listener = l;
    }
}
