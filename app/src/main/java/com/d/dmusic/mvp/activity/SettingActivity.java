package com.d.dmusic.mvp.activity;

import android.view.View;
import android.widget.TextView;

import com.d.commen.base.BaseActivity;
import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.R;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.utils.StatusBarCompat;
import com.d.dmusic.view.TitleLayout;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * SettingActivity
 * Created by D on 2017/6/13.
 */
public class SettingActivity extends BaseActivity<MvpBasePresenter> implements MvpView {
    @Bind(R.id.tl_title)
    TitleLayout tlTitle;

    @OnClick({R.id.iv_title_left})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_setting;
    }

    @Override
    public MvpBasePresenter getPresenter() {
        return new MvpBasePresenter(getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void init() {
        StatusBarCompat.compat(SettingActivity.this, getResources().getColor(R.color.color_main));//沉浸式状态栏
        initTitle();
    }

    private void initTitle() {
        TextView tvTitle = (TextView) tlTitle.findViewById(R.id.tv_title_title);
        tvTitle.setText("设置");
        tlTitle.setVisibility(R.id.iv_title_right, View.GONE);
    }
}
