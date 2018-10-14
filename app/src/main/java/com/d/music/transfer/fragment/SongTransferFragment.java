package com.d.music.transfer.fragment;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.MultiItemTypeSupport;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.activity.TransferActivity;
import com.d.music.transfer.adapter.TransferAdapter;
import com.d.music.transfer.manager.TransferManager;
import com.d.music.transfer.manager.operation.Operater;
import com.d.music.view.SongHeaderView;

import java.util.ArrayList;
import java.util.List;

/**
 * SongTransferFragment
 * Created by D on 2018/10/12.
 */
public class SongTransferFragment extends TransferFragment {
    private SongHeaderView header;

    @NonNull
    @Override
    protected Operater getOperater() {
        return TransferManager.getIns().optSong();
    }

    @Override
    protected CommonAdapter<TransferModel> getAdapter() {
        return new TransferAdapter(mContext, new ArrayList<TransferModel>(), type,
                new MultiItemTypeSupport<TransferModel>() {
                    @Override
                    public int getLayoutId(int viewType) {
                        switch (viewType) {
                            case TransferModel.TRANSFER_TYPE_HEAD_NOT:
                                return R.layout.module_transfer_adapter_head_downloading;
                            case TransferModel.TRANSFER_TYPE_HEAD_DONE:
                                return R.layout.module_transfer_adapter_head_downloaded;
                            case TransferModel.TRANSFER_TYPE_SONG:
                            default:
                                return R.layout.module_transfer_adapter_song;
                        }
                    }

                    @Override
                    public int getItemViewType(int position, TransferModel model) {
                        return model.transferType;
                    }
                });
    }

    @Override
    protected void initList() {
        initHead();
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        if (type == TYPE_SONG) {
            xrvList.addHeaderView(header);
        }
        super.initList();
    }

    private void initHead() {
        header = new SongHeaderView(mContext);
        header.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lib_pub_color_bg_sub));
        header.setVisibility(R.id.flyt_header_song_handler, View.GONE);
        header.setVisibility(View.GONE);
        header.setOnHeaderListener(new SongHeaderView.OnHeaderListener() {
            @Override
            public void onPlayAll() {

            }

            @Override
            public void onHandle() {

            }
        });
    }

    @Override
    public void setData(List<TransferModel> datas) {
        header.setVisibility(datas.size() > 0 ? View.VISIBLE : View.GONE);
        super.setData(datas);
    }

    @Override
    public void notifyDataSetChanged(List<List<TransferModel>> lists) {
        final int countDownloading = lists.get(0).size();
        final int countDownloaded = lists.get(1).size();
        ((TransferActivity) getActivity()).setTabNumber(type,
                countDownloading > 0 ? "" + countDownloading : "",
                countDownloading > 0 ? View.VISIBLE : View.GONE);
        header.setSongCount(countDownloaded);
        super.notifyDataSetChanged(lists);
    }
}
