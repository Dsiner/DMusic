package com.d.music.local.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.d.lib.common.component.quickclick.OnAvailableClickListener;
import com.d.lib.common.widget.dialog.AbsSheetDialog;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControl;
import com.d.music.component.operation.MoreOperator;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.widget.dialog.OperationDialog;

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
        holder.setVisibility(R.id.llyt_section, item.exIsLetter ? View.VISIBLE : View.GONE);
        holder.setVisibility(R.id.v_right_space, item.exLetter != null ? View.VISIBLE : View.GONE);
        if (item.exIsLetter) {
            holder.setText(R.id.tv_letter, item.exLetter);
        }

        holder.setText(R.id.tv_list_name, item.songName);
        holder.setText(R.id.tv_title, item.artistName);
        holder.setChecked(R.id.cb_more, item.exIsChecked);
        holder.setVisibility(R.id.llyt_more_cover, item.exIsChecked ? View.VISIBLE : View.GONE);
        holder.setText(R.id.tv_collect, item.isCollected
                ? mContext.getResources().getString(R.string.module_common_collected)
                : mContext.getResources().getString(R.string.module_common_collect));
        holder.setOnClickListener(R.id.llyt_song, new OnAvailableClickListener() {
            @Override
            public void onAvailableClick(View v) {
                MediaControl.getInstance(mContext).init(mDatas, position, true);
            }
        });
        holder.setOnClickListener(R.id.flyt_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSubPull) {
                    OperationDialog.getOperationDialog(mContext, OperationDialog.TYPE_NORMAL, "",
                            Arrays.asList(new OperationDialog.Bean().with(mContext, OperationDialog.Bean.TYPE_ADDLIST, false),
                                    new OperationDialog.Bean().with(mContext, OperationDialog.Bean.TYPE_FAV, false)
                                            .item(item.isCollected
                                                    ? mContext.getResources().getString(R.string.module_common_collected)
                                                    : mContext.getResources().getString(R.string.module_common_collect)),
                                    new OperationDialog.Bean().with(mContext, OperationDialog.Bean.TYPE_INFO, false)),
                            new AbsSheetDialog.OnItemClickListener<OperationDialog.Bean>() {
                                @Override
                                public void onClick(Dialog dlg, int index, OperationDialog.Bean bean) {
                                    if (bean.type == OperationDialog.Bean.TYPE_ADDLIST) {
                                        MoreOperator.addToList(mContext, type, item);
                                    } else if (bean.type == OperationDialog.Bean.TYPE_FAV) {
                                        collect(item, holder, position);
                                    } else if (bean.type == OperationDialog.Bean.TYPE_INFO) {
                                        MoreOperator.showInfo(mContext, item);
                                    }
                                }

                                @Override
                                public void onCancel(Dialog dlg) {

                                }
                            });
                } else {
                    item.exIsChecked = !item.exIsChecked;
                    holder.setChecked(R.id.cb_more, item.exIsChecked);
                    holder.setVisibility(R.id.llyt_more_cover, item.exIsChecked ? View.VISIBLE : View.GONE);
                }
            }
        });
        holder.setOnClickListener(R.id.llyt_collect, new OnAvailableClickListener() {
            @Override
            public void onAvailableClick(View v) {
                collect(item, holder, position);
            }
        });
        holder.setOnClickListener(R.id.llyt_add_to_list, new OnAvailableClickListener() {
            @Override
            public void onAvailableClick(View v) {
                MoreOperator.addToList(mContext, type, item);
            }
        });
        holder.setOnClickListener(R.id.llyt_info, new OnAvailableClickListener() {
            @Override
            public void onAvailableClick(View v) {
                MoreOperator.showInfo(mContext, item);
            }
        });
    }

    private void collect(MusicModel item, CommonHolder holder, int position) {
        MoreOperator.collect(mContext, type, item, true);
        // Status "item.isCollected" is changed
        if (type == AppDatabase.COLLECTION_MUSIC && !item.isCollected) {
            mDatas.remove(item);
            notifyDataSetChanged();
            if (listener != null) {
                listener.onChange(mDatas.size());
            }
        } else {
            holder.setText(R.id.tv_collect, item.isCollected
                    ? mContext.getResources().getString(R.string.module_common_collected)
                    : mContext.getResources().getString(R.string.module_common_collect));
            // 将下拉菜单收回
            pullUp(item, holder);
        }
    }

    private void pullUp(final MusicModel item, final CommonHolder holder) {
        item.exIsChecked = false;
        holder.setChecked(R.id.cb_more, false);
        holder.setVisibility(R.id.llyt_more_cover, View.GONE);
    }

    public void setOnDataChangedListener(OnDataChangedListener l) {
        this.listener = l;
    }

    public interface OnDataChangedListener {
        void onChange(int count);
    }
}
