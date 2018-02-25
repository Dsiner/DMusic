package com.d.lib.common.module.mvp.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

import com.d.lib.common.common.AlertDialogFactory;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.view.DSLayout;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.feng.skin.manager.base.BaseSkinFragmentActivity;

/**
 * BaseFragmentActivity
 * Created by D on 2017/4/27.
 */
public abstract class BaseFragmentActivity extends BaseSkinFragmentActivity implements MvpView {
    protected Activity mActivity;
    protected Context mContext;
    protected DSLayout dslDs;
    private AlertDialog loadingDlg;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutRes());
        if (getDSLayoutRes() != 0) {
            dslDs = (DSLayout) findViewById(getDSLayoutRes());
        }
        unbinder = ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
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

    protected abstract void init();
}
