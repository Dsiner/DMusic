package com.d.music.online.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.d.lib.common.widget.dialog.AbsSheetDialog;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControl;
import com.d.music.component.operation.MoreOperator;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.transfer.manager.TransferManager;
import com.d.music.widget.dialog.OperationDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * DetailAdapter
 * Created by D on 2018/8/11.
 */
public class DetailAdapter extends CommonAdapter<MusicModel> {

    public DetailAdapter(Context context, List<MusicModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(final int position, CommonHolder holder, final MusicModel item) {
        holder.setText(R.id.tv_seq, "" + (position + 1));
        holder.setText(R.id.tv_title, "" + item.songName);
        holder.setText(R.id.tv_singer, "" + item.artistName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControl.getInstance(mContext).init(getDatas(), position, true);
            }
        });
        holder.setOnClickListener(R.id.iv_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<OperationDialog.Bean> datas = new ArrayList<>();
                datas.add(new OperationDialog.Bean().with(mContext, OperationDialog.Bean.TYPE_ADDLIST, false));
                datas.add(new OperationDialog.Bean().with(mContext, OperationDialog.Bean.TYPE_DOWNLOAD, false));
                OperationDialog.getOperationDialog(mContext, OperationDialog.TYPE_NORMAL, "", datas,
                        new AbsSheetDialog.OnItemClickListener<OperationDialog.Bean>() {
                            @Override
                            public void onClick(Dialog dlg, int position, OperationDialog.Bean bean) {
                                if (bean.type == OperationDialog.Bean.TYPE_ADDLIST) {
                                    MoreOperator.addToList(mContext, -1, item);
                                } else if (bean.type == OperationDialog.Bean.TYPE_DOWNLOAD) {
                                    TransferManager.getInstance().optSong().add(item);
                                }
                            }

                            @Override
                            public void onCancel(Dialog dlg) {

                            }
                        });
            }
        });
    }
}
