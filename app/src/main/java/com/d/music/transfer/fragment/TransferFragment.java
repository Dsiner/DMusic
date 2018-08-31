package com.d.music.transfer.fragment;

import android.view.View;

import com.d.lib.common.module.loader.AbsLazyFragment;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.MultiItemTypeSupport;
import com.d.music.R;
import com.d.music.transfer.adapter.TransferAdapter;
import com.d.music.transfer.model.TransferModel;
import com.d.music.transfer.presenter.TransferPresenter;
import com.d.music.transfer.view.ITransferView;
import com.d.music.view.SongHeaderView;

import java.util.ArrayList;

/**
 * TransferFragment
 * Created by D on 2018/8/25.
 */
public class TransferFragment extends AbsLazyFragment<TransferModel, TransferPresenter> implements ITransferView {
    private SongHeaderView header;

    @Override
    public TransferPresenter getPresenter() {
        return new TransferPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected CommonAdapter<TransferModel> getAdapter() {
        return new TransferAdapter(mContext, new ArrayList<TransferModel>(), new MultiItemTypeSupport<TransferModel>() {
            @Override
            public int getLayoutId(int viewType) {
                switch (viewType) {
                    case 1:
                        return R.layout.module_transfer_adapter_head_downloading;
                    case 2:
                        return R.layout.module_transfer_adapter_head_downloaded;
                    default:
                        return R.layout.module_transfer_adapter_song;
                }
            }

            @Override
            public int getItemViewType(int position, TransferModel transferModel) {
                return 0;
            }
        });
    }

    @Override
    protected void initList() {
        initHead();
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        xrvList.addHeaderView(header);
        super.initList();
    }

    private void initHead() {
        header = new SongHeaderView(mContext);
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
    protected void onLoad(int page) {
        mPresenter.getDatas();
    }
}
