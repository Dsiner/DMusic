package com.d.music.transfer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.d.lib.common.component.loader.AbsLazyFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.MultiItemTypeSupport;
import com.d.music.R;
import com.d.music.component.greendao.bean.TransferModel;
import com.d.music.transfer.adapter.TransferAdapter;
import com.d.music.transfer.manager.TransferManager;
import com.d.music.transfer.manager.event.TransferEvent;
import com.d.music.transfer.presenter.TransferPresenter;
import com.d.music.transfer.view.ITransferView;
import com.d.music.view.SongHeaderView;

import java.util.ArrayList;
import java.util.List;

/**
 * TransferFragment
 * Created by D on 2018/8/25.
 */
public class TransferFragment extends AbsLazyFragment<TransferModel, TransferPresenter> implements ITransferView {
    public final static int TYPE_SONG = 0;
    public final static int TYPE_MV = 1;

    public final static String ARG_TYPE = "type";

    private int type;
    private SongHeaderView header;
    private TransferEvent transferEvent;

    public static TransferFragment getFragment(int type) {
        TransferFragment fragment = new TransferFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public TransferPresenter getPresenter() {
        return new TransferPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments() != null ? getArguments().getInt(ARG_TYPE, TYPE_SONG) : TYPE_SONG;
        initTransfer();
    }

    private void initTransfer() {
        transferEvent = new TransferEvent() {
            @Override
            public void onEvent() {
                if (!isLazyLoaded || mPresenter == null) {
                    return;
                }
                commonLoader.page = 1;
                setData(mPresenter.getDatas(type));
            }
        };
        if (type == TYPE_SONG) {
            TransferManager.getIns().optSong().pipe().register(transferEvent);
        } else if (type == TYPE_MV) {
            TransferManager.getIns().optMV().pipe().register(transferEvent);
        }
    }

    @Override
    protected CommonAdapter<TransferModel> getAdapter() {
        return new TransferAdapter(mContext, new ArrayList<TransferModel>(), type,
                new MultiItemTypeSupport<TransferModel>() {
                    @Override
                    public int getLayoutId(int viewType) {
                        switch (viewType) {
                            case TransferModel.VIEW_TYPE_HEAD_NOT:
                                return R.layout.module_transfer_adapter_head_downloading;
                            case TransferModel.VIEW_TYPE_HEAD_DONE:
                                return R.layout.module_transfer_adapter_head_downloaded;
                            default:
                                return R.layout.module_transfer_adapter_song;
                        }
                    }

                    @Override
                    public int getItemViewType(int position, TransferModel model) {
                        return model.viewType;
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
    protected void onLoad(int page) {
        mPresenter.load(type);
    }

    @Override
    public void setData(List<TransferModel> datas) {
        header.setVisibility(datas.size() > 0 ? View.VISIBLE : View.GONE);
        super.setData(datas);
    }

    @Override
    public void onDestroy() {
        if (type == TYPE_SONG) {
            TransferManager.getIns().optSong().pipe().unregister(transferEvent);
        } else if (type == TYPE_MV) {
            TransferManager.getIns().optMV().pipe().unregister(transferEvent);
        }
        super.onDestroy();
    }
}
