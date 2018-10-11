package com.d.lib.common.component.mvp.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.view.DSLayout;
import com.d.lib.common.view.dialog.AlertDialogFactory;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BaseActivity
 * Created by D on 2017/4/27.
 */
public abstract class BaseActivity<T extends MvpBasePresenter> extends Activity implements MvpView {
    protected Context mContext;
    protected Activity mActivity;
    protected T mPresenter;
    protected DSLayout dslDs;
    private AlertDialog loadingDlg;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutRes());
        if (getDSLayoutRes() != 0) {
            dslDs = (DSLayout) findViewById(getDSLayoutRes());
        }
        bindView();
        unbinder = ButterKnife.bind(this);
        mPresenter = getPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(getMvpView());
        }
        init();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView(false);
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void setState(int state) {
        if (dslDs != null) {
            dslDs.setState(state);
        }
    }

    /**
     * Show loading dialog
     */
    public void showLoading() {
        if (loadingDlg == null) {
            loadingDlg = AlertDialogFactory.createFactory(mContext).getLoadingDialog();
        }
        if (!loadingDlg.isShowing()) {
            loadingDlg.show();
        }
    }

    /**
     * Dismiss loading dialog
     */
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

    protected void bindView() {
    }

    protected abstract void init();
}
