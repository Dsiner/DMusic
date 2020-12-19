package com.d.lib.common.component.mvp.app.v4;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpBaseView;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.widget.DSLayout;
import com.d.lib.common.widget.dialog.AlertDialogFactory;

/**
 * BaseFragment
 * Created by D on 2017/4/27.
 */
public abstract class BaseFragment<T extends MvpBasePresenter>
        extends Fragment implements MvpBaseView {

    protected Context mContext;
    protected Activity mActivity;
    protected T mPresenter;
    protected View mRootView;
    protected DSLayout mDslDs;
    protected Dialog mLoadingDlg;
    protected boolean mIsPrepared;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mRootView == null) {
            int layoutRes = getLayoutRes();
            mPresenter = getPresenter();
            mRootView = getActivity().getLayoutInflater().inflate(layoutRes, null);
            if (getDSLayoutRes() != 0) {
                mDslDs = (DSLayout) mRootView.findViewById(getDSLayoutRes());
            }
            bindView(mRootView);
            mIsPrepared = true;
            init();
        } else {
            if (mRootView.getParent() != null) {
                ((ViewGroup) mRootView.getParent()).removeView(mRootView);
            }
            if (getDSLayoutRes() != 0) {
                mDslDs = (DSLayout) mRootView.findViewById(getDSLayoutRes());
            }
            bindView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null) {
            mPresenter.attachView(getMvpView());
        }
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.detachView(false);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setState(int state) {
        if (mDslDs != null) {
            mDslDs.setState(state);
        }
    }

    @Override
    public void showLoadingDialog() {
        if (mLoadingDlg == null) {
            mLoadingDlg = AlertDialogFactory.createFactory(mContext).getLoadingDialog();
        }
        if (!mLoadingDlg.isShowing()) {
            mLoadingDlg.show();
        }
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
            mLoadingDlg.dismiss();
        }
    }

    /**
     * Return the layout resource like R.layout.my_layout
     *
     * @return the layout resource or zero ("0"), if you don't want to have an UI
     */
    protected abstract int getLayoutRes();

    /**
     * Return the resId resource like R.id.dsl_ds
     *
     * @return the resId resource or zero ("0"), if you don't want to have an DSLayout
     */
    protected int getDSLayoutRes() {
        return 0;
    }

    public abstract T getPresenter();

    protected abstract MvpView getMvpView();

    protected void bindView(View rootView) {
    }

    protected abstract void init();
}
