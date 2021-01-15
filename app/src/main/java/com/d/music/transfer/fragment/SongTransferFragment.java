package com.d.music.transfer.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.d.lib.pulllayout.Pullable;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.MultiItemTypeSupport;
import com.d.lib.pulllayout.util.RefreshableCompat;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControl;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.activity.TransferActivity;
import com.d.music.transfer.adapter.TransferAdapter;
import com.d.music.transfer.manager.TransferManager;
import com.d.music.transfer.manager.operation.TransferOperator;
import com.d.music.widget.SongHeaderView;

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
    protected TransferOperator getOperator() {
        return TransferManager.getInstance().optSong();
    }

    @Override
    protected CommonAdapter<TransferModel> getAdapter() {
        return new TransferAdapter(mContext, new ArrayList<TransferModel>(), mType,
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
        ((Pullable) mPullList).setCanPullDown(false);
        ((Pullable) mPullList).setCanPullUp(false);
        if (mType == TYPE_SONG) {
            RefreshableCompat.addHeaderView(mPullList, header);
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
                MediaControl.getInstance(mContext).
                        init(TransferModel.convertTo(TransferManager.getInstance().optSong().pipe().list()),
                                0, true);
            }

            @Override
            public void onHandle() {

            }
        });
    }

    @Override
    public void loadSuccess(List<TransferModel> datas) {
        header.setVisibility(datas.size() > 0 ? View.VISIBLE : View.GONE);
        super.loadSuccess(datas);
    }

    @Override
    public void notifyDataSetChanged(List<List<TransferModel>> lists) {
        final int countDownloading = lists.get(0).size();
        final int countDownloaded = lists.get(1).size();
        final int count = countDownloading + countDownloaded;
        ((TransferActivity) getActivity()).setTabNumber(mType,
                countDownloading > 0 ? "" + countDownloading : "",
                countDownloading > 0 ? View.VISIBLE : View.GONE);
        header.setSongCount(count);
        super.notifyDataSetChanged(lists);
    }
}
