package com.d.lib.common.module.mvp.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.d.lib.common.common.AlertDialogFactory;
import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.view.DSLayout;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BaseFragment
 * Created by D on 2017/4/27.
 */
public abstract class BaseFragment<T extends MvpBasePresenter> extends Fragment implements MvpView {
    protected Context mContext;
    protected Activity mActivity;
    protected T mPresenter;
    protected View rootView;
    protected DSLayout dslDs;
    private AlertDialog loadingDlg;
    private Unbinder unbinder;

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
        if (rootView == null) {
            int layoutRes = getLayoutRes();
            mPresenter = getPresenter();
            rootView = getActivity().getLayoutInflater().inflate(layoutRes, null);
            if (getDSLayoutRes() != 0) {
                dslDs = (DSLayout) rootView.findViewById(getDSLayoutRes());
            }
            bindView(rootView);
            unbinder = ButterKnife.bind(this, rootView);
            init();
        } else {
            if (rootView.getParent() != null) {
                ((ViewGroup) rootView.getParent()).removeView(rootView);
            }
            if (getDSLayoutRes() != 0) {
                dslDs = (DSLayout) rootView.findViewById(getDSLayoutRes());
            }
            bindView(rootView);
            unbinder = ButterKnife.bind(this, rootView);
        }
        return rootView;
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
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setState(int state) {
        if (dslDs != null) {
            dslDs.setState(state);
        }
    }

    @Override
    public void showLoading() {
        if (loadingDlg == null) {
            loadingDlg = AlertDialogFactory.createFactory(mContext).getLoadingDialog();
        }
        if (!loadingDlg.isShowing()) {
            loadingDlg.show();
        }
    }

    @Override
    public void closeLoading() {
        if (loadingDlg != null && loadingDlg.isShowing()) {
            loadingDlg.dismiss();
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
