package com.d.music.transfer.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.d.lib.common.component.loader.v4.BaseLazyLoaderFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.pulllayout.Pullable;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.activity.TransferActivity;
import com.d.music.transfer.manager.TransferDataObservable;
import com.d.music.transfer.manager.operation.TransferOperator;
import com.d.music.transfer.presenter.TransferPresenter;
import com.d.music.transfer.view.ITransferView;

import java.util.List;

/**
 * TransferFragment
 * Created by D on 2018/8/25.
 */
public abstract class TransferFragment extends BaseLazyLoaderFragment<TransferModel, TransferPresenter>
        implements ITransferView {
    public static final int TYPE_SONG = 0;
    public static final int TYPE_MV = 1;

    public static final String EXTRA_TYPE = "type";

    protected int mType;
    private TransferDataObservable mTransferDataObservable;

    public static TransferFragment getFragment(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TYPE, type);
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
        mType = getArguments() != null ? getArguments().getInt(EXTRA_TYPE, TYPE_SONG) : TYPE_SONG;
        initTransfer();
    }

    private void initTransfer() {
        mTransferDataObservable = new TransferDataObservable() {
            @Override
            public void notifyDataSetChanged(List<List<TransferModel>> lists) {
                if (!mIsLazyLoaded || mPresenter == null) {
                    final int countDownloading = lists.get(0).size();
                    ((TransferActivity) getActivity()).setTabNumber(mType,
                            countDownloading > 0 ? "" + countDownloading : "",
                            countDownloading > 0 ? View.VISIBLE : View.GONE);
                    return;
                }
                TransferFragment.this.notifyDataSetChanged(lists);
            }
        };
        getOperator().register(mTransferDataObservable);
    }

    @Override
    public void notifyDataSetChanged(List<List<TransferModel>> lists) {
        mCommonLoader.page = 1;
        loadSuccess(mPresenter.getDatas(lists));
    }

    @Override
    protected void initList() {
        ((Pullable) mPullList).setCanPullDown(false);
        ((Pullable) mPullList).setCanPullUp(false);
        super.initList();
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.load(mType);
    }

    @Override
    public void onDestroy() {
        getOperator().unregister(mTransferDataObservable);
        super.onDestroy();
    }

    @NonNull
    protected abstract TransferOperator getOperator();
}
