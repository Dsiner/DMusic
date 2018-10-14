package com.d.music.transfer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.d.lib.common.component.loader.AbsLazyFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.activity.TransferActivity;
import com.d.music.transfer.manager.TransferDataObservable;
import com.d.music.transfer.manager.operation.Operater;
import com.d.music.transfer.presenter.TransferPresenter;
import com.d.music.transfer.view.ITransferView;

import java.util.List;

/**
 * TransferFragment
 * Created by D on 2018/8/25.
 */
public abstract class TransferFragment extends AbsLazyFragment<TransferModel, TransferPresenter> implements ITransferView {
    public final static int TYPE_SONG = 0;
    public final static int TYPE_MV = 1;

    public final static String ARG_TYPE = "type";

    protected int type;
    private TransferDataObservable observable;

    public static TransferFragment getFragment(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TYPE, type);
        TransferFragment fragment;
        if (type == TYPE_MV) {
            fragment = new MVTransferFragment();
        } else {
            fragment = new SongTransferFragment();
        }
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
        observable = new TransferDataObservable() {
            @Override
            public void notifyDataSetChanged(List<List<TransferModel>> lists) {
                if (!isLazyLoaded || mPresenter == null) {
                    final int countDownloading = lists.get(0).size();
                    ((TransferActivity) getActivity()).setTabNumber(type,
                            countDownloading > 0 ? "" + countDownloading : "",
                            countDownloading > 0 ? View.VISIBLE : View.GONE);
                    return;
                }
                TransferFragment.this.notifyDataSetChanged(lists);
            }
        };
        getOperater().register(observable);
    }

    @Override
    public void notifyDataSetChanged(List<List<TransferModel>> lists) {
        commonLoader.page = 1;
        setData(mPresenter.getDatas(lists));
    }

    @Override
    protected void initList() {
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        super.initList();
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.load(type);
    }

    @Override
    public void onDestroy() {
        getOperater().unregister(observable);
        super.onDestroy();
    }

    @NonNull
    protected abstract Operater getOperater();
}
