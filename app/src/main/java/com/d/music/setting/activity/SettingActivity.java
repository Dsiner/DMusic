package com.d.music.setting.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.mvp.base.BaseActivity;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.common.view.RowLayout;
import com.d.lib.common.view.TitleLayout;
import com.d.music.R;
import com.d.music.common.Preferences;
import com.d.music.utils.StatusBarCompat;

import butterknife.BindView;
import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * SettingActivity
 * Created by D on 2017/6/13.
 */
public class SettingActivity extends BaseActivity<MvpBasePresenter> implements MvpView, RowLayout.OnToggleListener {
    @BindView(R.id.tl_title)
    TitleLayout tlTitle;
    @BindView(R.id.rl_mode_auto_play)
    RowLayout rlModeAutoPlay;
    @BindView(R.id.rl_mode_sub)
    RowLayout rlModeSub;
    @BindView(R.id.rl_mode_add)
    RowLayout rlModeAdd;
    @BindView(R.id.rl_mode_rotate)
    RowLayout rlModeRotate;
    @BindView(R.id.rl_mode_shake)
    RowLayout rlModeShake;
    @BindView(R.id.rl_mode_menu)
    RowLayout rlModeMenu;

    private Preferences p;

    @OnClick({R.id.iv_title_left, R.id.rl_koan, R.id.rl_skin, R.id.rl_sleep, R.id.rl_player_mode, R.id.rl_about})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.rl_koan:
                startActivity(new Intent(SettingActivity.this, KoanActivity.class));
                break;
            case R.id.rl_skin:
                startActivity(new Intent(SettingActivity.this, SkinActivity.class));
                break;
            case R.id.rl_sleep:
                startActivity(new Intent(SettingActivity.this, SleepActivity.class));
                break;
            case R.id.rl_player_mode:
                startActivity(new Intent(SettingActivity.this, PlayerModeActivity.class));
                break;
            case R.id.rl_about:
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
                break;
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
        StatusBarCompat.compat(SettingActivity.this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
        initTitle();
        p = Preferences.getInstance(SettingActivity.this);
        rlModeAutoPlay.setOpen(p.getIsAutoPlay());
        rlModeSub.setOpen(p.getIsSubPull());
        rlModeAdd.setOpen(p.getIsShowAdd());
        rlModeRotate.setOpen(p.getIsAlbumRotate());
        rlModeShake.setOpen(p.getIsShake());
        rlModeMenu.setOpen(p.getIsShowMenuIcon());

        rlModeAutoPlay.setOnToggleListener(this);
        rlModeSub.setOnToggleListener(this);
        rlModeAdd.setOnToggleListener(this);
        rlModeRotate.setOnToggleListener(this);
        rlModeShake.setOnToggleListener(this);
        rlModeMenu.setOnToggleListener(this);
    }

    private void initTitle() {
        TextView tvTitle = (TextView) tlTitle.findViewById(R.id.tv_title_title);
        tvTitle.setText("设置");
        tlTitle.setVisibility(R.id.iv_title_right, View.GONE);
    }

    @Override
    public void onToggle(View v, boolean isOpen) {
        switch (v.getId()) {
            case R.id.rl_mode_auto_play:
                p.putIsAutoPlay(isOpen);
                break;
            case R.id.rl_mode_sub:
                p.putIsSubPull(isOpen);
                break;
            case R.id.rl_mode_add:
                p.putIsShowAdd(isOpen);
                break;
            case R.id.rl_mode_rotate:
                p.putIsAlbumRotate(isOpen);
                break;
            case R.id.rl_mode_shake:
                p.putIsShake(isOpen);
                break;
            case R.id.rl_mode_menu:
                p.putIsShowMenuIcon(isOpen);
                break;
        }
    }

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
    }
}
